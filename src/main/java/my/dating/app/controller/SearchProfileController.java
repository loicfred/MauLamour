package my.dating.app.controller;

import my.dating.app.object.Search_Profile;
import org.solarframework.auth.obj.Account_User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// FR-2, FR-3, FR-4: a member's own search profiles. Every action checks on the server that the search is theirs.
@Controller
public class SearchProfileController {
    // Every profile save is checked against every search (MatchNotifier), so one member can't make that unbounded.
    static final int MAX_SEARCHES_PER_MEMBER = 20;

    @GetMapping("/profile/search")
    public String newSearch(Model model) {
        return showForm(model, new Search_Profile(), List.of());
    }

    @GetMapping("/profile/search/{id}")
    public String editSearch(@PathVariable long id, Model model, Principal loggedUser) {
        return showForm(model, Search_Profile.requireOwnedBy(id, memberId(loggedUser)), List.of());
    }

    @PostMapping("/post/profile/search")
    public String saveSearch(@RequestParam MultiValueMap<String, String> form, Model model, Principal loggedUser) {
        long me = memberId(loggedUser);
        String id = form.getFirst("ID");
        boolean isNew = id == null || id.isBlank();
        Search_Profile search = isNew ? new Search_Profile() : Search_Profile.requireOwnedBy(parseId(id), me);
        List<String> errors = search.applyForm(form);
        if (isNew && SolarDBManager.Count(Search_Profile.class, "UserID = ?", me) >= MAX_SEARCHES_PER_MEMBER)
            errors = List.of("You can keep up to " + MAX_SEARCHES_PER_MEMBER + " searches. Delete one you no longer use to make room.");
        if (!errors.isEmpty()) return showForm(model, search, errors); // the member's input is kept on the form

        if (isNew) {
            search.ID = Search_Profile.nextId();
            search.UserID = me;
            search.Write();
        } else search.Update();
        search.saveChoices();
        return "redirect:/matches?searchId=" + search.ID;
    }

    @PostMapping("/post/profile/search/{id}/delete")
    public String deleteSearch(@PathVariable long id, Principal loggedUser) {
        Search_Profile.requireOwnedBy(id, memberId(loggedUser)).deleteWithChoices();
        return "redirect:/matches?deleted";
    }

    private static String showForm(Model model, Search_Profile search, List<String> errors) {
        model.addAttribute("search", search);
        model.addAttribute("errors", errors);
        return "profile/search";
    }
    private static long memberId(Principal loggedUser) {
        return Account_User.getByAuthentication(loggedUser).getID();
    }
    // A forged, non-numeric id gets the same 403 as someone else's search.
    private static long parseId(String id) {
        try {
            return Long.parseLong(id.trim());
        } catch (NumberFormatException e) {
            throw new AccessDeniedException("This search belongs to another member.");
        }
    }
}
