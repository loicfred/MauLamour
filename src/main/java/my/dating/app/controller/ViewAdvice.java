package my.dating.app.controller;

import my.dating.app.object.Match_Notification;
import org.solarframework.auth.obj.Account_User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice(annotations = org.springframework.stereotype.Controller.class)
public class ViewAdvice {

    // Spring Security knows a member by their email, but pages show and link them by username (header.html, profile links).
    @ModelAttribute("me")
    public Account_User me(Principal loggedUser) {
        return loggedUser == null ? null : Account_User.getByAuthentication(loggedUser);
    }

    // The number on the header's bell (FR-6).
    @ModelAttribute("unreadNotifications")
    public long unreadNotifications(Principal loggedUser) {
        Account_User me = loggedUser == null ? null : Account_User.getByAuthentication(loggedUser);
        return me == null ? 0 : Match_Notification.countUnread(me.getID());
    }
}
