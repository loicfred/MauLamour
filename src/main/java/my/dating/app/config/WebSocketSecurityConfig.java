package my.dating.app.config;

import my.dating.app.service.ChatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

// Every STOMP frame goes through these rules before reaching a @MessageMapping or the broker.
// Anything not listed here is denied.
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages, ChatService chats) {
        AuthorizationManager<MessageAuthorizationContext<?>> chatMember = (authentication, context) ->
                new AuthorizationDecision(isChatMember(chats, context.getVariables().get("chatId"), authentication.get().getName()));

        return messages
                .nullDestMatcher().authenticated() // CONNECT, UNSUBSCRIBE, DISCONNECT, heartbeats
                .simpSubscribeDestMatchers("/user/queue/**").authenticated()
                .simpSubscribeDestMatchers("/topic/chat/{chatId}").access(chatMember)
                .simpDestMatchers("/app/chat/{chatId}/**").access(chatMember)
                .anyMessage().denyAll()
                .build();
    }

    private static boolean isChatMember(ChatService chats, String chatId, String username) {
        try {
            return chats.isMember(Long.parseLong(chatId), username);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
