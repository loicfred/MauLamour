package my.dating.app.service;

import my.dating.app.object.ViewCache;
import my.dating.app.object.chatting.Chat;
import my.dating.app.object.chatting.ChatDto;
import my.dating.app.object.chatting.ChatDto.AttachmentData;
import my.dating.app.object.chatting.ChatDto.Event;
import my.dating.app.object.chatting.ChatDto.MessageData;
import my.dating.app.object.msg.Draft;
import my.dating.app.object.msg.Message;
import my.dating.app.object.msg.Message_Reaction;
import my.dating.app.object.msg.attachment.Draft_Attachment;
import my.dating.app.object.msg.attachment.Message_Attachment;
import org.solarframework.auth.obj.Account_User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Everything the chat does, independent of how requests arrive (STOMP or HTTP).
// Methods called over STOMP rely on WebSocketSecurityConfig for chat membership; they still
// check that the targeted message belongs to that chat and, where it matters, to the caller.
// The "username" parameters are the name Spring Security knows the caller by (Principal.getName()), which is their email.
@Service
public class ChatService {
    private static final int MAX_REACTIONS_PER_MESSAGE = 16;

    private final ApplicationEventPublisher events;
    public ChatService(ApplicationEventPublisher events) {
        this.events = events;
    }

    public boolean isMember(long chatId, String username) {
        Account_User user = Account_User.getByEmail(username);
        return user != null && Chat.isMember(chatId, user.getID());
    }

    public String usernameOf(String login) {
        return Account_User.getByEmail(login).getUsername();
    }

    // ---------- Reading ----------

    public ChatDto.OpenedChat open(long chatId, String username) {
        requireMember(chatId, username);
        Chat.Chatlist_Item chat = Chat.Chatlist_Item.find(chatId, userId(username));
        List<Message.Message_View> messages = chat.getMessages();
        for (Message.Message_View message : messages) { // skip per-message queries when there is nothing to load
            if (message.getAttachmentCount() == 0) message.attachments = new ArrayList<>();
            if (message.getReactionCount() == 0) message.reactions = new ArrayList<>();
        }
        chat.readAllMessages(chat.getPartnerID());
        return new ChatDto.OpenedChat(
                messages.stream().map(MessageData::from).toList(),
                new ChatDto.Partner(chat.getPartnerUsername(), chat.getPartnerName(), chat.getPartnerID()),
                ChatDto.DraftData.from(Draft.get(chatId, chat.getMyId())));
    }

    public void markRead(long chatId, String username) {
        Chat.Chatlist_Item chat = Chat.Chatlist_Item.find(chatId, userId(username));
        chat.readAllMessages(chat.getPartnerID());
    }

    public Optional<Message_Attachment> messageAttachment(long attachmentId, String username) {
        return Optional.ofNullable(Message_Attachment.getById(attachmentId)).filter(attachment -> {
            Message message = Message.getById(attachment.MessageID);
            return message != null && isMember(message.ChatID, username);
        });
    }

    public Optional<Draft_Attachment> draftAttachment(long attachmentId, String username) {
        return Optional.ofNullable(Draft_Attachment.getById(attachmentId)).filter(attachment -> {
            Draft draft = Draft.getById(attachment.MessageID);
            return draft != null && draft.UserID == userId(username);
        });
    }

    // ---------- Drafts ----------

    public void saveDraft(long chatId, String username, String content) {
        new Draft(chatId, userId(username), content).Upsert();
    }

    public List<AttachmentData> addDraftAttachments(long chatId, String username, List<MultipartFile> files) throws IOException {
        requireMember(chatId, username);
        long userId = userId(username);
        Draft draft = Optional.ofNullable(Draft.get(chatId, userId))
                .orElseGet(() -> new Draft(chatId, userId, "").UpsertThenReturn().orElseThrow());
        for (MultipartFile file : files)
            if (!file.isEmpty()) new Draft_Attachment(draft.ID, file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return AttachmentData.from(Draft_Attachment.getByDraft(draft.ID));
    }

    public void deleteDraftAttachment(long attachmentId, String username) {
        draftAttachment(attachmentId, username)
                .orElseThrow(() -> new AccessDeniedException("Cannot delete this attachment"))
                .Delete();
    }

    // ---------- Messages ----------

    // Turns the draft (its attachments) plus the typed content into a message.
    public Optional<Event> send(long chatId, String username, String content) {
        long userId = userId(username);
        Draft draft = Draft.get(chatId, userId);
        List<Draft_Attachment> files = draft == null ? List.of() : draft.getAttachments();
        if (content.isBlank() && files.isEmpty()) return Optional.empty();

        Message message = new Message(chatId, userId, content);
        for (Draft_Attachment file : files) message.addAttachments(new Message_Attachment(file, message.ID));
        if (draft != null) {
            draft.ClearAttachments();
            draft.Delete();
        }
        chatListChanged(chatId, username, true);
        return Optional.of(new Event("message/receive", MessageData.from(message)));
    }

    public Event edit(long chatId, String username, long messageId, String content) {
        Message message = ownMessage(chatId, messageId, username);
        message.setContent(content);
        message.setUpdatedAtTime(Instant.now().toEpochMilli());
        message.setEdited(true);
        message.Update();
        chatListChanged(chatId, username, false);
        return new Event("message/update", MessageData.from(message));
    }

    public Event delete(long chatId, String username, long messageId) {
        Message message = ownMessage(chatId, messageId, username);
        deleteWithChildren(message);
        chatListChanged(chatId, username, false);
        return new Event("message/delete", message.ID);
    }

    // Deleting the last attachment of a message without text deletes the message.
    public Optional<Event> deleteAttachment(long chatId, String username, long attachmentId) {
        Message_Attachment attachment = Message_Attachment.getById(attachmentId);
        if (attachment == null) return Optional.empty();
        Message message = ownMessage(chatId, attachment.MessageID, username);
        attachment.Delete();
        ViewCache.forgetChats(); // message_view counts the attachments

        if (message.Content.isBlank() && Message_Attachment.getByMessage(message.ID).isEmpty()) {
            deleteWithChildren(message);
            chatListChanged(chatId, username, false);
            return Optional.of(new Event("message/delete", message.ID));
        }
        return Optional.of(new Event("attachment/delete", AttachmentData.from(attachment)));
    }

    // ---------- Reactions ----------

    public Event react(long chatId, String username, long messageId, String emoji) {
        Message.Message_View message = inChat(chatId, Message.Message_View.getById(messageId));
        long userId = userId(username);
        if (Message_Reaction.get(userId, messageId, emoji) == null) {
            if (message.getReactionCount() >= MAX_REACTIONS_PER_MESSAGE)
                throw new AccessDeniedException("You cannot send more than " + MAX_REACTIONS_PER_MESSAGE + " reactions in a message.");
            new Message_Reaction(userId, messageId, emoji);
        }
        return reactionsEvent(messageId);
    }

    public Optional<Event> unreact(long chatId, String username, long messageId, String emoji) {
        inChat(chatId, Message.getById(messageId));
        Message_Reaction reaction = Message_Reaction.get(userId(username), messageId, emoji);
        if (reaction == null) return Optional.empty();
        reaction.Delete();
        return Optional.of(reactionsEvent(messageId));
    }

    // ---------- Helpers ----------

    static long userId(String username) {
        Account_User user = Account_User.getByEmail(username);
        if (user == null) throw new AccessDeniedException("Unknown account");
        return user.getID();
    }

    private void requireMember(long chatId, String username) {
        if (!isMember(chatId, username)) throw new AccessDeniedException("Cannot access this chat");
    }

    private static <M extends Message> M inChat(long chatId, M message) {
        if (message == null || message.ChatID != chatId) throw new AccessDeniedException("This message is not in this chat");
        return message;
    }

    private static Message ownMessage(long chatId, long messageId, String username) {
        Message message = inChat(chatId, Message.getById(messageId));
        if (message.UserID != userId(username)) throw new AccessDeniedException("This is not your message");
        return message;
    }

    private static void deleteWithChildren(Message message) {
        message.ClearAttachments();
        message.ClearReactions();
        message.Delete();
    }

    private static Event reactionsEvent(long messageId) {
        ViewCache.forgetChats(); // the counts are read straight after the reaction was written
        return new Event("reaction/refresh",
                new ChatDto.ReactionsData(messageId, Message_Reaction.Message_Reaction_View.getByMessage(messageId)));
    }

    private void chatListChanged(long chatId, String username, boolean newMessage) {
        ViewCache.forgetChats(); // ChatNotifier reads the chat list just after this write
        events.publishEvent(new ChatNotifier.ChatListChanged(chatId, username, newMessage));
    }
}
