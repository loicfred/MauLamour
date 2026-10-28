package my.dating.app.controller;

import my.dating.app.object.Profile;
import my.dating.app.object.Profile_Photo;
import my.dating.app.object.msg.attachment.BaseAttachment;
import my.dating.app.service.ChatService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/file")
public class FileController {

    private final ChatService chats;
    public FileController(ChatService chats) {
        this.chats = chats;
    }

    // Profile picture
    @GetMapping("/avatar/{username}.png")
    @Cacheable(value = "profilePic", key = "#username")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable String username) {
        Profile.Profile_View user = Profile.Profile_View.getView(username);
        Profile stored = user == null ? null : Profile.getById(user.ID); // the view's Avatar is always NULL: reads leave blobs out
        byte[] avatar = stored == null ? null : stored.fetchAvatar();
        HttpHeaders headers = new HttpHeaders();
        if (avatar == null || avatar.length == 0) {
            headers.setLocation(URI.create("/img/default-pfp.png"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
            return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
        }
    }

    // Banner image
    @GetMapping("/photo/{id}.png")
    @Cacheable(value = "photos", key = "#id")
    public ResponseEntity<byte[]> getBanner(@PathVariable Long id) {
        Profile_Photo photo = Profile_Photo.getById(id);
        if (photo.Image == null) return ResponseEntity.notFound().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(photo.Image, headers, HttpStatus.OK);
    }

    // Only members of the chat (or the draft's owner) can fetch an attachment; anyone else gets a 404.
    // Not cached server-side: a cache hit would skip the access check. Browsers cache it privately instead.
    @GetMapping("/message/attachment/{id}")
    public ResponseEntity<byte[]> getMessageAttachment(@PathVariable long id, Principal principal) {
        return sendAttachment(chats.messageAttachment(id, principal.getName()));
    }
    @GetMapping("/draft/attachment/{id}")
    public ResponseEntity<byte[]> getDraftAttachment(@PathVariable long id, Principal principal) {
        return sendAttachment(chats.draftAttachment(id, principal.getName()));
    }

    // Attachments are user uploads served from our own origin, so they must never run as a page:
    // the sandbox CSP blocks scripts (HTML, SVG...), and only images and videos are displayed inline.
    private ResponseEntity<byte[]> sendAttachment(Optional<? extends BaseAttachment<?>> found) {
        if (found.isEmpty()) return ResponseEntity.notFound().build();
        BaseAttachment<?> att = found.get();
        MediaType type = parseMediaType(att.getFileType());
        boolean inline = type.getType().equals("image") || type.getType().equals("video");
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, (inline ? ContentDisposition.inline() : ContentDisposition.attachment())
                        .filename(att.getFileName() == null ? "file" : att.getFileName(), StandardCharsets.UTF_8).build().toString())
                .header("Content-Security-Policy", "sandbox")
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePrivate())
                .body(att.getFileData());
    }
    private static MediaType parseMediaType(String type) {
        try {
            return type == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(type);
        } catch (InvalidMediaTypeException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}