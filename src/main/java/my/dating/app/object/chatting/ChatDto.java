package my.dating.app.object.chatting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import my.dating.app.object.msg.Draft;
import my.dating.app.object.msg.Message;
import my.dating.app.object.msg.Message_Reaction;
import my.dating.app.object.msg.attachment.BaseAttachment;

import java.util.List;

// What the chat sends and receives over the wire. Field names match what chat.js reads.
// File bytes are never included: clients fetch them from /file/... instead.
public final class ChatDto {
    private ChatDto() {}

    // ---------- Outgoing ----------

    // Everything published on /topic/chat/{chatId} is wrapped in an Event so one subscription can carry every kind of update.
    public record Event(String Type, Object Data) {}

    public record OpenedChat(List<MessageData> messages, Partner partner, DraftData draft) {}

    public record Partner(String Username, String Name, Long ID) {}

    public record AttachmentData(long ID, long MessageID, String FileName, String FileType, long FileSize) {
        public static AttachmentData from(BaseAttachment<?> a) {
            return new AttachmentData(a.ID, a.MessageID, a.FileName, a.FileType, a.FileData == null ? 0 : a.FileData.length);
        }
        public static List<AttachmentData> from(List<? extends BaseAttachment<?>> attachments) {
            return attachments.stream().<AttachmentData>map(AttachmentData::from).toList();
        }
    }

    public record MessageData(Long ID, long ChatID, long UserID, String Content, Long UpdatedAtTime, boolean isEdited, boolean isRead,
                              List<AttachmentData> attachments, List<Message_Reaction.Message_Reaction_View> reactions) {
        public static MessageData from(Message m) {
            return new MessageData(m.ID, m.ChatID, m.UserID, m.Content, m.UpdatedAtTime, m.isEdited, m.isRead,
                    AttachmentData.from(m.getAttachments()), m.getReactions());
        }
    }

    public record DraftData(String Content, List<AttachmentData> attachments) {
        public static DraftData from(Draft d) {
            return d == null ? null : new DraftData(d.Content, AttachmentData.from(d.getAttachments()));
        }
    }

    public record ReactionsData(long MessageID, List<Message_Reaction.Message_Reaction_View> reactions) {}

    // WebRTC signaling relayed between the two members; From is always set by the server.
    public record CallSignal(String From, Object Signal) {}

    // ---------- Incoming ----------

    public static class ContentRequest {
        @NotNull @Size(max = 1024)
        public String Content;
    }

    public static class EditRequest {
        @NotNull
        public Long ID;
        @NotBlank @Size(max = 1024)
        public String Content;
    }

    public static class ReactionRequest {
        @NotNull
        public Long MessageID;
        @NotBlank @Size(max = 32)
        public String Emoji;
    }
}
