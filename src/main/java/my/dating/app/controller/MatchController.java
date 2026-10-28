package my.dating.app.controller;

import my.dating.app.object.Profile;
import my.dating.app.object.Search_Profile;
import my.dating.app.service.MatchRanking;
import org.solarframework.auth.obj.Account_User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

// FR-5: the matches page. Without a search picked it shows the picker only.
@Controller
public class MatchController {
    private final MatchRanking ranking;
    public MatchController(MatchRanking ranking) {
        this.ranking = ranking;
    }

    @GetMapping("/matches")
    public String showMatchPage(@RequestParam(required = false) Long searchId, @RequestParam(defaultValue = "1") int page,
                                Model model, Principal loggedUser) {
        Account_User user = Account_User.getByAuthentication(loggedUser);
        model.addAttribute("searchProfiles", Search_Profile.getByUser(user));
        model.addAttribute("SearchID", searchId == null ? 0L : searchId);
        if (searchId == null) return "matches";

        Search_Profile search = Search_Profile.requireOwnedBy(searchId, user.getID());
        Profile me = Profile.getById(user.getID());
        if (me == null) me = new Profile(); // WebConfig creates it on the first visit; this only guards a race with that
        List<MatchRanking.Match> ranked = MatchRanking.rank(search, me, ranking.loadCandidates(), LocalDate.now());
        int shownPage = Math.max(page, 1);
        model.addAttribute("search", search);
        model.addAttribute("matches", MatchRanking.page(ranked, shownPage));
        int lastPage = Math.max(1, (ranked.size() + MatchRanking.PAGE_SIZE - 1) / MatchRanking.PAGE_SIZE);
        model.addAttribute("page", shownPage);
        model.addAttribute("previousPage", Math.min(shownPage - 1, lastPage)); // past the end, Previous leads back to the last real page
        model.addAttribute("hasNext", (long) shownPage * MatchRanking.PAGE_SIZE < ranked.size());
        model.addAttribute("isDistanceIgnored", search.LocationRadiusKM != null && (me.Latitude == null || me.Longitude == null));
        return "matches";
    }
}
