package my.dating.app.controller;

import jakarta.validation.Valid;
import my.dating.app.object.chatting.ChatDto;
import my.dating.app.object.chatting.ChatDto.Event;
import my.dating.app.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

// Transport only: maps HTTP and STOMP requests to ChatService.
// STOMP handlers live under /app/chat/{chatId}/... (membership is enforced by WebSocketSecurityConfig);
// what they return is published to both members on /topic/chat/{chatId}. A null return publishes nothing.
@Controller
@RequestMapping("/chat")
public class ChatController {
    private static final String CHAT_TOPIC = "/topic/chat/{chatId}";

    private final ChatService chats;
    public ChatController(ChatService chats) {
        this.chats = chats;
    }

    // ---------- HTTP ----------

    @GetMapping("/user/{chatId}")
    @ResponseBody
    public ChatDto.OpenedChat openChat(@PathVariable long chatId, Principal principal) {
        return chats.open(chatId, principal.getName());
    }

    // made use of Ajax here because STOMP doesn't support files, and it involves communication between
    // only one client (partner doesn't care about your drafts)
    @PostMapping("/draft/attachment/upload")
    @ResponseBody
    public List<ChatDto.AttachmentData> uploadDraftFiles(@RequestParam long chatId, @RequestParam("attachments") List<MultipartFile> files, Principal principal) throws IOException {
        return chats.addDraftAttachments(chatId, principal.getName(), files);
    }

    @DeleteMapping("/draft/attachment/delete")
    public ResponseEntity<Void> deleteDraftFile(@RequestParam long attachmentId, Principal principal) {
        chats.deleteDraftAttachment(attachmentId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // ---------- STOMP: no broadcast ----------

    @MessageMapping("/chat/{chatId}/draft")
    public void saveDraft(@DestinationVariable long chatId, @Valid @Payload ChatDto.ContentRequest draft, Principal principal) {
        chats.saveDraft(chatId, principal.getName(), draft.Content);
    }

    @MessageMapping("/chat/{chatId}/read")
    public void markRead(@DestinationVariable long chatId, Principal principal) {
        chats.markRead(chatId, principal.getName());
    }

    // ---------- STOMP: broadcast to the chat ----------

    @MessageMapping("/chat/{chatId}/typing")
    @SendTo(CHAT_TOPIC)
    public Event typing(Principal principal) {
        return new Event("typing", chats.usernameOf(principal.getName())); // the partner sees this, so never the email
    }

    @MessageMapping("/chat/{chatId}/message/send")
    @SendTo(CHAT_TOPIC)
    public Event send(@DestinationVariable long chatId, @Valid @Payload ChatDto.ContentRequest message, Principal principal) {
        return chats.send(chatId, principal.getName(), message.Content).orElse(null);
    }

    @MessageMapping("/chat/{chatId}/message/edit")
    @SendTo(CHAT_TOPIC)
    public Event edit(@DestinationVariable long chatId, @Valid @Payload ChatDto.EditRequest edit, Principal principal) {
        return chats.edit(chatId, principal.getName(), edit.ID, edit.Content);
    }

    @MessageMapping("/chat/{chatId}/message/delete")
    @SendTo(CHAT_TOPIC)
    public Event delete(@DestinationVariable long chatId, @Payload long messageId, Principal principal) {
        return chats.delete(chatId, principal.getName(), messageId);
    }

    @MessageMapping("/chat/{chatId}/message/attachment/delete")
    @SendTo(CHAT_TOPIC)
    public Event deleteAttachment(@DestinationVariable long chatId, @Payload long attachmentId, Principal principal) {
        return chats.deleteAttachment(chatId, principal.getName(), attachmentId).orElse(null);
    }

    @MessageMapping("/chat/{chatId}/reaction/send")
    @SendTo(CHAT_TOPIC)
    public Event react(@DestinationVariable long chatId, @Valid @Payload ChatDto.ReactionRequest react, Principal principal) {
        return chats.react(chatId, principal.getName(), react.MessageID, react.Emoji);
    }

    @MessageMapping("/chat/{chatId}/reaction/delete")
    @SendTo(CHAT_TOPIC)
    public Event unreact(@DestinationVariable long chatId, @Valid @Payload ChatDto.ReactionRequest react, Principal principal) {
        return chats.unreact(chatId, principal.getName(), react.MessageID, react.Emoji).orElse(null);
    }

    // WebRTC signaling (offer/answer, ICE candidates, cancel/accept/decline), relayed as-is to the chat.
    @MessageMapping("/chat/{chatId}/call/{signal:sdp|ice|action}")
    @SendTo(CHAT_TOPIC)
    public Event relayCallSignal(@DestinationVariable String signal, @Payload Object data, Principal principal) {
        return new Event("call/" + signal, new ChatDto.CallSignal(chats.usernameOf(principal.getName()), data));
    }
}
