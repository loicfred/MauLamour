package my.dating.app;

import my.dating.app.object.Match_Notification;
import my.dating.app.object.Profile;
import my.dating.app.object.Search_Profile;
import my.dating.app.object.enums.SearchChoice;
import my.dating.app.service.MatchRanking;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Renders the pages that don't bind a form (th:field needs a running MVC request), the way Spring Boot resolves
// them, so a wrong expression fails here instead of as an error page. No database: every value is set in memory.
class TemplateRenderTest {
    private static SpringTemplateEngine engine;

    @BeforeAll
    static void setUpEngine() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.refresh();
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(context);
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    private static String render(String template, Map<String, Object> variables, String... params) {
        MockServletContext servletContext = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        for (String p : params) request.setParameter(p, "");
        var exchange = JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, new MockHttpServletResponse());
        Map<String, Object> all = new HashMap<>(variables);
        all.putIfAbsent("me", null); // signed out: the header leaves out the bell and its scripts
        all.putIfAbsent("unreadNotifications", 0L);
        return engine.process(template, new WebContext(exchange, Locale.UK, all));
    }

    private static Search_Profile savedSearch() {
        Search_Profile search = new Search_Profile();
        search.ID = 42L;
        search.Name = "Weekend in Curepipe";
        search.MinAge = 25;
        search.LovelifeMarriage = true;
        search.setChoices(new EnumMap<>(SearchChoice.class));
        search.getChoices().put(SearchChoice.SKIN_COLOUR, new HashSet<>(Set.of("OLIVE")));
        return search;
    }

    @Test
    void newSearchFormRenders() {
        Search_Profile search = new Search_Profile();
        search.setChoices(new EnumMap<>(SearchChoice.class));
        String html = render("profile/search", Map.of("search", search, "errors", List.of()));
        assertTrue(html.contains("Describe who you are looking for"));
        assertTrue(html.contains("data-choice-group=\"SKIN_COLOUR\""));
        assertFalse(html.contains("Delete this search"));
    }

    @Test
    void savedSearchFormKeepsWhatWasSaved() {
        String html = render("profile/search", Map.of("search", savedSearch(), "errors", List.of("Ages go from 18 to 99.")));
        assertTrue(html.contains("value=\"Weekend in Curepipe\""));
        assertTrue(html.contains("name=\"SKIN_COLOUR\" value=\"OLIVE\""));
        assertTrue(html.contains("<option value=\"true\" selected=\"selected\">Yes</option>"));
        assertTrue(html.contains("Ages go from 18 to 99."));
        assertTrue(html.contains("/post/profile/search/42/delete"));
    }

    @Test
    void matchesPageWithoutSearches() {
        String html = render("matches", Map.of("searchProfiles", List.of(), "SearchID", 0L));
        assertTrue(html.contains("You have no searches yet."));
    }

    @Test
    void matchesPageShowsRealPercent() {
        Profile.Profile_View match = new Profile.Profile_View();
        match.ID = 7L;
        match.Name = "Anya";
        match.Username = "anya-uuid";
        match.Gender = "FEMALE";
        match.Age = 28L;
        match.DateOfBirth = LocalDate.of(1998, 1, 1);
        match.HasAvatar = true; // reads leave Avatar itself NULL, so the page asks the view whether there is one
        Search_Profile search = savedSearch();
        String html = render("matches", Map.of("searchProfiles", List.of(search), "SearchID", 42L, "search", search,
                "matches", List.of(new MatchRanking.Match(match, 86)), "page", 2, "previousPage", 1, "hasNext", true, "isDistanceIgnored", false), "deleted");
        assertTrue(html.contains("86%"));
        assertTrue(html.contains("src=\"/file/avatar/anya-uuid.png\""));
        assertTrue(html.contains("href=\"/profile/anya-uuid\""));
        assertTrue(html.contains("Female"));
        assertTrue(html.contains("page=1") && html.contains("page=3"));
        assertTrue(html.contains("The search has been deleted."));
        assertFalse(html.contains("10%"));
    }

    // On ?page=1000 with 30 matches, Previous must lead back to the last page that has any.
    @Test
    void previousFromPastTheEndGoesToTheLastRealPage() {
        Search_Profile search = savedSearch();
        String html = render("matches", Map.of("searchProfiles", List.of(search), "SearchID", 42L, "search", search,
                "matches", List.of(), "page", 1000, "previousPage", 2, "hasNext", false, "isDistanceIgnored", false));
        assertTrue(html.contains("page=2"));
        assertFalse(html.contains("page=999"));
    }

    @Test
    void notificationsPage() {
        Match_Notification n = new Match_Notification();
        n.Text = Match_Notification.textFor("Weekend in Curepipe", 86);
        n.Link = "/profile/anya-uuid";
        n.CreatedAt = Instant.parse("2026-10-28T09:30:00Z");
        String html = render("notifications", Map.of("notifications", List.of(n)));
        assertTrue(html.contains("Someone new matches your search Weekend in Curepipe at 86%"));
        assertTrue(html.contains("href=\"/profile/anya-uuid\""));
        assertTrue(html.contains(">New<"));
        assertTrue(render("notifications", Map.of("notifications", List.of())).contains("No notifications yet."));
    }
}
