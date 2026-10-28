package my.dating.app.object.msg.attachment;

import jakarta.persistence.MappedSuperclass;
import org.solarframework.db.spring.DatabaseObject;

@MappedSuperclass
public class BaseAttachment<T> extends DatabaseObject.ID_OBJ<Long, T> {
    public long MessageID;
    public String FileName;
    public String FileType;
    public byte[] FileData;

    public BaseAttachment() {}
    public BaseAttachment(long messageID, String fileName, String fileType, byte[] fileData) {
        this.ID = nextId();
        this.MessageID = messageID;
        this.FileName = fileName;
        this.FileType = fileType;
        this.FileData = fileData;
    }

    public long getMessageID() {
        return MessageID;
    }
    public String getFileName() {
        return FileName;
    }
    public String getFileType() {
        return FileType;
    }
    public byte[] getFileData() {
        return FileData;
    }

}
