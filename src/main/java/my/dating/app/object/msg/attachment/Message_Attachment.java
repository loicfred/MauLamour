package my.dating.app.object.msg.attachment;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "message_attachment")
public class Message_Attachment extends BaseAttachment<Message_Attachment> {

    public Message_Attachment() {}
    public Message_Attachment(Draft_Attachment draftAttachment, long messageID) {
        super(messageID, draftAttachment.FileName, draftAttachment.FileType, draftAttachment.FileData);
        Write();
    }
    public Message_Attachment(long messageID, String fileName, String fileType, byte[] fileData) {
        super(messageID, fileName, fileType, fileData);
        Write();
    }

    public static Message_Attachment getById(long id) {
        return SolarDBManager.getById(Message_Attachment.class, id).orElse(null);
    }
    public static List<Message_Attachment> getByMessage(long messageID) {
        return SolarDBManager.getAllWhere(Message_Attachment.class, "MessageID = ?", messageID);
    }

}
