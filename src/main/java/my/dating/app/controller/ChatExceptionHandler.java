package my.dating.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

// Errors thrown by any @MessageMapping are sent back to the session that caused them, on /user/queue/errors.
@ControllerAdvice
public class ChatExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatExceptionHandler.class);

    @MessageExceptionHandler(AccessDeniedException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public String accessDenied(AccessDeniedException e) {
        return e.getMessage();
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public String invalid(MethodArgumentNotValidException e) {
        return "Invalid request.";
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public String unexpected(Exception e) {
        log.error("Chat message handling failed", e);
        return "Something went wrong.";
    }
}
