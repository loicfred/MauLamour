package my.dating.app.object.msg;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.dating.app.object.msg.attachment.Message_Attachment;

import java.util.ArrayList;
import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "message")
public class Message extends BaseMessage<Message> {
    public transient List<Message_Attachment> attachments;
    public transient List<Message_Reaction.Message_Reaction_View> reactions;

    public Long UpdatedAtTime;
    public boolean isEdited = false;
    public boolean isRead = false;

    public Message() {}
    public Message(long chatid, long userid, String content) {
        super(chatid, userid, content);
        this.ID = nextId();
        Write();
    }

    public static Message getById(long id) {
        return SolarDBManager.getById(Message.class, id).orElse(null);
    }

    public boolean isEdited() {
        return isEdited;
    }
    public boolean isRead() {
        return isRead;
    }

    public void setUserID(long userid) {
        UserID = userid;
    }
    public void setContent(String content) {
        if (!content.isEmpty()) Content = content;
    }
    public void setEdited(boolean edited) {
        isEdited = edited;
    }
    public void setRead(boolean edited) {
        isRead = edited;
    }
    public void setUpdatedAtTime(Long time) {
        UpdatedAtTime = time;
    }

    public List<Message_Attachment> getAttachments() {
        return attachments == null ? attachments = Message_Attachment.getByMessage(ID) : attachments;
    }
    public void addAttachments(Message_Attachment... attachment) {
        if (attachments == null) attachments = new ArrayList<>();
        attachments.addAll(List.of(attachment));
    }
    public int ClearAttachments() {
        return DefaultDBService.doUpdate(Message_Attachment.class, "DELETE FROM message_attachment WHERE MessageID = ?", ID);
    }

    public int ClearReactions() {
        return DefaultDBService.doUpdate(Message_Reaction.class, "DELETE FROM message_reaction WHERE MessageID = ?", ID);
    }

    public List<Message_Reaction.Message_Reaction_View> getReactions() {
        return reactions == null ? reactions = Message_Reaction.Message_Reaction_View.getByMessage(ID) : reactions;
    }

    @Table(name = "message_view")
    public static class Message_View extends Message {
        public Long AttachmentCount;
        public Long ReactionCount;

        public Message_View() {}

        public static List<Message_View> getByChat(long chatid) {
            return SolarDBManager.getAllWhere(Message_View.class, "ChatID = ? ORDER BY ID", chatid);
        }
        public static Message_View getById(long id) {
            return SolarDBManager.getWhere(Message_View.class, "ID = ?", id).orElse(null);
        }

        public Long getAttachmentCount() {
            return AttachmentCount;
        }
        public Long getReactionCount() {
            return ReactionCount;
        }
    }
}
