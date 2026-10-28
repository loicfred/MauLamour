package my.dating.app.controller;

import my.dating.app.object.Match_Notification;
import org.solarframework.auth.obj.Account_User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

// FR-6: the member's own new-match notifications, newest first. Opening the page marks them as read.
@Controller
public class NotificationController {

    @GetMapping("/notifications")
    public String showNotifications(Model model, Principal loggedUser) {
        long me = Account_User.getByAuthentication(loggedUser).getID();
        List<Match_Notification> notifications = Match_Notification.ofUser(me); // read before marking, so the unread ones still stand out
        Match_Notification.markAllRead(me);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadNotifications", 0L); // the header's bell: they are read now
        return "notifications";
    }
}
