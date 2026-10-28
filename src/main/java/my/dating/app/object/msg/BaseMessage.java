package my.dating.app.object.msg;

import jakarta.persistence.MappedSuperclass;
import org.solarframework.db.spring.DatabaseObject;

@MappedSuperclass
public class BaseMessage<T> extends DatabaseObject.ID_OBJ<Long, T> {
    public long ChatID;
    public long UserID;
    public String Content;

    public BaseMessage() {}
    public BaseMessage(long chatid, long userid, String content) {
        this.ChatID = chatid;
        this.UserID = userid;
        this.Content = content;
    }

    public long getChatID() {
        return ChatID;
    }
    public long getUserID() {
        return UserID;
    }
    public String getContent() {
        return Content;
    }
}
