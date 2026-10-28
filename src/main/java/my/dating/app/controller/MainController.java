package my.dating.app.controller;

import my.dating.app.object.*;
import my.dating.app.object.chatting.Chat;
import org.solarframework.auth.obj.Account_User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String defaultPage(Model model, Principal loggedUser) {
        model.addAttribute("principal", loggedUser);
        return "home";
    }
    @GetMapping("/home")
    public String home(Model model, Principal loggedUser) {
        return defaultPage(model, loggedUser);
    }


    @GetMapping("/chat")
    public String openChat(Model model, Principal loggedUser) {
        model.addAttribute("principal", loggedUser);
        Profile.Profile_View currentUser = Profile.Profile_View.getView(Account_User.getByAuthentication(loggedUser).getUsername());
        List<Chat.Chatlist_Item> latestChats = Chat.Chatlist_Item.getWithUser(currentUser.ID);
        model.addAttribute("latestChats", latestChats);
        model.addAttribute("Me", currentUser);
        return "chat";
    }
}