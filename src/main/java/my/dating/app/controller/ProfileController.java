package my.dating.app.controller;

import my.dating.app.object.Profile;
import my.dating.app.object.Profile_Photo;
import my.dating.app.object.enums.Interest;
import my.dating.app.object.enums.Language;
import my.dating.app.service.MatchNotifier;
import org.solarframework.auth.obj.Account_User;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;

@Controller
public class ProfileController {
    private final ApplicationEventPublisher events;
    private final CacheManager cacheManager;
    public ProfileController(ApplicationEventPublisher events, CacheManager cacheManager) {
        this.events = events;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model, Principal loggedUser) {
        model.addAttribute("principal", loggedUser);
        Profile.Profile_View profile = Profile.Profile_View.getView(username);
        if (profile == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No member has this address.");
        while (profile.getPhotos().size() < 20) profile.getPhotos().add(new Profile_Photo());
        model.addAttribute("profile", profile);
        model.addAttribute("isOwner", loggedUser != null && Account_User.getByAuthentication(loggedUser).getUsername().equals(profile.Username));
        return "profile/view";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal loggedUser) {
        model.addAttribute("principal", loggedUser);
        model.addAttribute("profile", Profile.Profile_View.getView(Account_User.getByAuthentication(loggedUser).getUsername()));
        return "profile/edit";
    }

    // A forged form must not set the photo past the picture check, nor name another member's profile.
    @InitBinder("profile")
    public void initProfileBinder(WebDataBinder binder) {
        refuseUnsafeFields(binder);
    }
    static void refuseUnsafeFields(WebDataBinder binder) {
        binder.setDisallowedFields("ID", "id", "Avatar", "avatar");
    }

    @PostMapping("/post/profile/edit")
    public String saveProfile(@ModelAttribute Profile profile, Principal loggedUser, @RequestParam("profilePicFile") MultipartFile profilePicFile,
                              @RequestParam(required = false) Set<Interest> interests, @RequestParam(required = false) Set<Language> languages) throws IOException {
        Account_User me = Account_User.getByAuthentication(loggedUser);
        profile.ID = me.getID(); // the posted ID is never bound (refuseUnsafeFields): a member only ever saves their own profile
        if (!profilePicFile.isEmpty()) {
            if (profilePicFile.getContentType() == null || !profilePicFile.getContentType().startsWith("image/")) return "redirect:/profile/edit?notAPicture";
            profile.setAvatar(profilePicFile.getBytes());
            Cache pictures = cacheManager.getCache("profilePic"); // FileController caches each member's picture
            if (pictures != null) pictures.evict(me.getUsername());
        }
        profile.keepStoredAvatarFrom(Profile.getById(profile.ID));
        profile.setInterests(interests == null ? Set.of() : interests); // nothing posted means every tag was unticked
        profile.setLanguages(languages == null ? Set.of() : languages);
        profile.Update();
        profile.saveInterestsAndLanguages(); // also clears the cached profile views, which the redirect and the notifier read
        events.publishEvent(new MatchNotifier.ProfileSaved(profile.ID)); // FR-6, on another thread
        return "redirect:/profile/edit?updated";
    }

}
