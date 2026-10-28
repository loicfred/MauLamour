package my.dating.app.object.msg.attachment;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "draft_attachment")
public class Draft_Attachment extends BaseAttachment<Draft_Attachment> {

    public Draft_Attachment() {}
    public Draft_Attachment(long messageID, String fileName, String fileType, byte[] fileData) {
        super(messageID, fileName, fileType, fileData);
        Write();
    }

    public static Draft_Attachment getById(long id) {
        return SolarDBManager.getById(Draft_Attachment.class, id).orElse(null);
    }
    public static List<Draft_Attachment> getByDraft(long draftMessageID) {
        return SolarDBManager.getAllWhere(Draft_Attachment.class, "MessageID = ?", draftMessageID);
    }

}
