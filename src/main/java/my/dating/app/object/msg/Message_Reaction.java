package my.dating.app.object.msg;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.solarframework.db.spring.DatabaseObject;

import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "message_reaction")
public class Message_Reaction extends DatabaseObject.ID_OBJ<Long, Message_Reaction> {

    public long MessageID;
    public long UserID;
    public String Emoji;

    public Message_Reaction() {}
    public Message_Reaction(long userID, long messageID, String emoji) {
        ID = nextId();
        MessageID = messageID;
        UserID = userID;
        Emoji = emoji;
        Write();
    }

    public long getMessageID() {
        return MessageID;
    }
    public long getUserID() {
        return UserID;
    }
    public String getEmoji() {
        return Emoji;
    }

    public static Message_Reaction getById(long id) {
        return SolarDBManager.getById(Message_Reaction.class, id).orElse(null);
    }
    public static Message_Reaction get(long userid, long messageid, String emoji) {
        return SolarDBManager.getWhere(Message_Reaction.class, "UserID = ? AND MessageID = ? AND Emoji = ?", userid, messageid, emoji).orElse(null);
    }

    @Table(name = "message_reaction_view")
    public static class Message_Reaction_View extends Message_Reaction {
        public long Count = 0;

        public Message_Reaction_View() {}

        public static List<Message_Reaction_View> getByMessage(long messageID) {
            return SolarDBManager.getAllWhere(Message_Reaction_View.class, "MessageID = ?", messageID);
        }

        public long getCount() {
            return Count;
        }
    }
}
