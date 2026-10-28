package my.dating.app.object.chatting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.dating.app.object.ViewCache;
import my.dating.app.object.msg.Message;
import org.solarframework.auth.obj.Account_User;
import org.solarframework.db.spring.DatabaseObject;

import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "chat")
public class Chat extends DatabaseObject.ID_OBJ<Long, Chat> {
    private transient List<Message.Message_View> Messages;

    public long UserID1;
    public long UserID2;

    public Chat() {}
    public Chat(Account_User user, Account_User user2) {
        this.ID = nextId();
        this.UserID1 = user.getID();
        this.UserID2 = user2.getID();
        Write();
    }

    public static Chat getById(long id) {
        return SolarDBManager.getById(Chat.class, id).orElse(null);
    }
    public List<Message.Message_View> getMessages() {
        return Messages == null ? Messages = Message.Message_View.getByChat(ID) : Messages;
    }

    public static boolean isMember(long chatid, long userid) {
        return SolarDBManager.Count(Chat.class, "ID = ? AND (UserID1 = ? OR UserID2 = ?)", chatid, userid, userid) > 0;
    }

    public void readAllMessages(long userid) {
        DefaultDBService.doUpdate(Message.class, "UPDATE message SET isRead = ? WHERE ChatID = ? AND UserID = ?", true, ID, userid); // with the class, so the cached messages are cleared
        ViewCache.forgetChats(); // the unread counts
    }

    @Table(name = "chatlist_item")
    public static class Chatlist_Item extends Chat {

        public Long LatestMessageID;
        public Long SenderID;
        public String Content;

        public Long PartnerID;
        public String PartnerUsername;
        public String PartnerName;

        public Long Unreads;

        public static List<Chatlist_Item> getWithUser(long userid) {
            return SolarDBManager.getAllWhere(Chatlist_Item.class, "(UserID1 = ? OR UserID2 = ?) AND NOT PartnerID = ?", userid, userid, userid);
        }
        public static Chatlist_Item find(long chatid, long userid) {
            return SolarDBManager.getWhere(Chatlist_Item.class, "ID = ? AND NOT PartnerID = ?", chatid, userid).orElse(null);
        }

        public Long getLatestMessageID() {
            return LatestMessageID;
        }
        public Long getSenderID() {
            return SenderID;
        }
        public String getContent() {
            return Content;
        }
        public String getPartnerUsername() {
            return PartnerUsername;
        }
        public String getPartnerName() {
            return PartnerName;
        }
        public Long getPartnerID() {
            return PartnerID;
        }
        public Long getUnreads() {
            return Unreads;
        }

        public void setLatestMessageID(Long latestMessageID) {
            LatestMessageID = latestMessageID;
        }
        public void setSenderID(Long senderID) {
            SenderID = senderID;
        }
        public void setContent(String content) {
            Content = content;
        }
        public void setPartnerUsername(String username) {
            PartnerUsername = username;
        }
        public void setPartnerName(String name) {
            PartnerName = name;
        }
        public void setUnreads(Long unreads) {
            Unreads = unreads;
        }

        public long getMyId() {
            return PartnerID == UserID2 ? UserID1 : UserID2;
        }

    }
}
