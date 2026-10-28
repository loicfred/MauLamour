package my.dating.app.object.msg;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.dating.app.object.msg.attachment.Draft_Attachment;

import java.util.ArrayList;
import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "draft")
public class Draft extends BaseMessage<Draft> {
    private transient List<Draft_Attachment> attachments;

    public Draft() {}
    public Draft(long chatid, long userid, String content) {
        super(chatid, userid, content);
    }

    public static Draft getById(long id) {
        return SolarDBManager.getById(Draft.class, id).orElse(null);
    }
    public static Draft get(long chatid, long userid) {
        return SolarDBManager.getWhere(Draft.class, "ChatID = ? AND UserID = ?", chatid, userid).orElse(null);
    }

    public List<Draft_Attachment> getAttachments() {
        return attachments == null ? attachments = Draft_Attachment.getByDraft(ID) : attachments;
    }
    public void addAttachments(Draft_Attachment... attachment) {
        if (attachments == null) attachments = new ArrayList<>();
        attachments.addAll(List.of(attachment));
    }
    public int ClearAttachments() {
        return DefaultDBService.doUpdate(Draft_Attachment.class, "DELETE FROM draft_attachment WHERE MessageID = ?", ID);
    }

}
