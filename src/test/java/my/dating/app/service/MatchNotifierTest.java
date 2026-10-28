package my.dating.app.service;

import my.dating.app.object.Match_Notification;
import my.dating.app.object.Profile;
import my.dating.app.object.Search_Profile;
import my.dating.app.object.enums.SearchChoice;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MatchNotifierTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 28);

    private static Profile person() {
        Profile p = new Profile();
        p.ID = 7L;
        p.Name = "Newcomer";
        p.Gender = "FEMALE";
        p.DateOfBirth = TODAY.minusYears(28);
        p.Latitude = -20.2;
        p.Longitude = 57.5;
        p.interests = Set.of();
        p.languages = Set.of();
        p.LifestylePets = true;
        p.PhysicalHasTattoos = true;
        p.LovelifeMarriage = true;
        p.LovelifeWillingToRelocate = true;
        return p;
    }
    // A search the person() scores 100, 80 or 60 against: four "Yes" settings they match, plus misses for 80 and 60.
    private static Search_Profile scoring(long id, long ownerId, int percent) {
        Search_Profile sp = new Search_Profile();
        sp.ID = id;
        sp.UserID = ownerId;
        sp.Name = "Search " + id;
        sp.setChoices(new EnumMap<>(SearchChoice.class));
        sp.LifestylePets = true;
        sp.PhysicalHasTattoos = true;
        sp.LovelifeMarriage = true;
        sp.LovelifeWillingToRelocate = true;
        // A fifth setting the person misses brings 4/4 down to 4/5 = 80%.
        if (percent == 80) sp.LovelifeVirginity = true;
        // Missing two of five (3/5 = 60%) stands for "below 80".
        if (percent == 60) { sp.LovelifeVirginity = true; sp.LovelifeWillingToRelocate = false; }
        return sp;
    }
    private static Map<Long, Profile> searchers(long... ownerIds) {
        Map<Long, Profile> map = new HashMap<>();
        for (long id : ownerIds) {
            Profile p = new Profile();
            p.ID = id;
            map.put(id, p);
        }
        return map;
    }

    @Test
    void eightyIsEnough() {
        List<MatchNotifier.NewMatch> found = MatchNotifier.findNewMatches(person(),
                List.of(scoring(1, 100, 80), scoring(2, 200, 60)), searchers(100, 200), Set.of(), TODAY);
        assertEquals(List.of(1L), found.stream().map(m -> m.search().ID).toList());
        assertEquals(80, found.getFirst().percent());
    }

    @Test
    void ownSearchSkipped() {
        List<MatchNotifier.NewMatch> found = MatchNotifier.findNewMatches(person(),
                List.of(scoring(1, 7, 100)), searchers(7), Set.of(), TODAY);
        assertTrue(found.isEmpty());
    }

    @Test
    void alreadyNotifiedSkipped() {
        List<MatchNotifier.NewMatch> found = MatchNotifier.findNewMatches(person(),
                List.of(scoring(1, 100, 100)), searchers(100), Set.of(1L), TODAY);
        assertTrue(found.isEmpty());
    }

    @Test
    void searcherWithoutProfileSkipped() {
        List<MatchNotifier.NewMatch> found = MatchNotifier.findNewMatches(person(),
                List.of(scoring(1, 100, 100)), searchers(), Set.of(), TODAY);
        assertTrue(found.isEmpty());
    }

    @Test
    void textIsExact() {
        assertEquals("Someone new matches your search Weekend in Curepipe at 86%",
                Match_Notification.textFor("Weekend in Curepipe", 86));
    }

    @Test
    void mailFailureKeepsSiteNotification() {
        List<Match_Notification> written = new ArrayList<>();
        List<String> pushedTo = new ArrayList<>();
        MatchNotifier notifier = new MatchNotifier(n -> written.add(n), (to, n) -> pushedTo.add(to),
                (to, subject, body) -> { throw new IllegalStateException("SMTP down"); }, "http://localhost:8080");
        assertDoesNotThrow(() -> notifier.deliver(new MatchNotifier.NewMatch(scoring(1, 100, 100), 100), 7L, "newcomer-uuid", 100L, "owner@example.test"));
        assertEquals(1, written.size());
        assertEquals("/profile/newcomer-uuid", written.getFirst().Link);
        assertEquals(List.of("owner@example.test"), pushedTo);
    }

    // The live push carries only what the bell needs, never the entity with its ids.
    @Test
    void pushCarriesOnlyTextAndLink() {
        List<MatchNotifier.MatchPush> pushed = new ArrayList<>();
        MatchNotifier notifier = new MatchNotifier(n -> true, (to, payload) -> pushed.add(payload), (to, subject, body) -> {}, "http://localhost:8080");
        notifier.deliver(new MatchNotifier.NewMatch(scoring(1, 100, 100), 100), 7L, "newcomer-uuid", 100L, "owner@example.test");
        assertEquals(new MatchNotifier.MatchPush("Someone new matches your search Search 1 at 100%", "/profile/newcomer-uuid"), pushed.getFirst());
    }

    @Test
    void duplicateIsNeitherPushedNorMailed() {
        List<String> sent = new ArrayList<>();
        MatchNotifier notifier = new MatchNotifier(n -> false, (to, n) -> sent.add("push"),
                (to, subject, body) -> sent.add("mail"), "http://localhost:8080");
        notifier.deliver(new MatchNotifier.NewMatch(scoring(1, 100, 100), 100), 7L, "newcomer-uuid", 100L, "owner@example.test");
        assertTrue(sent.isEmpty());
    }

    @Test
    void emailLinksToTheProfile() {
        List<String> bodies = new ArrayList<>();
        MatchNotifier notifier = new MatchNotifier(n -> true, (to, n) -> {}, (to, subject, body) -> bodies.add(subject + "|" + body), "https://maulamour.example");
        notifier.deliver(new MatchNotifier.NewMatch(scoring(1, 100, 100), 100), 7L, "newcomer-uuid", 100L, "owner@example.test");
        assertTrue(bodies.getFirst().startsWith("Someone new matches your search Search 1|Someone new matches your search Search 1 at 100%"));
        assertTrue(bodies.getFirst().contains("https://maulamour.example/profile/newcomer-uuid"));
    }
}
