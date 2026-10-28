package my.dating.app.service;

import my.dating.app.object.chatting.Chat;
import org.solarframework.auth.obj.Account_User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// Keeps both members' chat list (left pane) up to date. It is per user rather than per chat,
// so it goes to /user/queue/notification/... instead of the chat topic.
@Component
public class ChatNotifier {

    // username is the name Spring Security knows the sender by (their email)
    public record ChatListChanged(long chatId, String username, boolean newMessage) {}

    private final SimpMessagingTemplate messagingTemplate;
    public ChatNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Runs once the change is committed, so nobody is notified about data that was rolled back.
    @TransactionalEventListener(fallbackExecution = true)
    public void onChatListChanged(ChatListChanged event) {
        String destination = event.newMessage() ? "/queue/notification/receive" : "/queue/notification/update";
        long me = ChatService.userId(event.username());
        Chat.Chatlist_Item mine = Chat.Chatlist_Item.find(event.chatId(), me);
        if (mine == null) return;
        Account_User partner = SolarDBManager.getById(Account_User.class, mine.getPartnerID()).orElse(null);
        messagingTemplate.convertAndSendToUser(event.username(), destination, mine);
        if (partner != null) messagingTemplate.convertAndSendToUser(partner.getEmail(), destination, Chat.Chatlist_Item.find(event.chatId(), partner.getID()));
    }
}
