package my.dating.app.service;

import my.dating.app.object.Profile;
import my.dating.app.object.Search_Profile;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MatchRankingTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 28);

    private static Profile.Profile_View person(long id, int yesAnswers, Instant joinedAt) {
        Profile.Profile_View p = new Profile.Profile_View();
        p.ID = id;
        p.Name = "Member " + id;
        p.Gender = "FEMALE";
        p.DateOfBirth = TODAY.minusYears(30);
        p.Latitude = -20.2;
        p.Longitude = 57.5;
        p.interests = Set.of();
        p.languages = Set.of();
        p.JoinedAt = joinedAt;
        p.LifestylePets = yesAnswers >= 1;
        p.PhysicalHasTattoos = yesAnswers >= 2;
        p.LovelifeMarriage = yesAnswers >= 3;
        p.LovelifeWillingToRelocate = yesAnswers >= 4;
        return p;
    }
    // Four settings, all "Yes": a person with n "Yes" answers scores n * 25.
    private static Search_Profile fourYesSearch() {
        Search_Profile sp = new Search_Profile();
        sp.setChoices(new EnumMap<>(my.dating.app.object.enums.SearchChoice.class));
        sp.LifestylePets = true;
        sp.PhysicalHasTattoos = true;
        sp.LovelifeMarriage = true;
        sp.LovelifeWillingToRelocate = true;
        return sp;
    }
    private static Profile me() {
        Profile p = new Profile();
        p.ID = 1L;
        p.Latitude = -20.2;
        p.Longitude = 57.5;
        return p;
    }

    @Test
    void highestFirst() {
        Instant t = Instant.parse("2026-10-01T00:00:00Z");
        List<MatchRanking.Match> ranked = MatchRanking.rank(fourYesSearch(), me(),
                List.of(person(2, 2, t), person(3, 4, t), person(4, 3, t)), TODAY);
        assertEquals(List.of(100, 75, 50), ranked.stream().map(MatchRanking.Match::percent).toList());
    }

    @Test
    void tieNewestFirst() {
        List<MatchRanking.Match> ranked = MatchRanking.rank(fourYesSearch(), me(), List.of(
                person(2, 4, Instant.parse("2026-01-01T00:00:00Z")),
                person(3, 4, Instant.parse("2026-09-01T00:00:00Z"))), TODAY);
        assertEquals(3L, ranked.getFirst().profile().ID);
    }

    @Test
    void selfAndUnfinishedExcluded() {
        Instant t = Instant.parse("2026-10-01T00:00:00Z");
        Profile.Profile_View myOwnRow = person(1, 4, t);
        Profile.Profile_View noLocation = person(5, 4, t);
        noLocation.Latitude = null;
        List<MatchRanking.Match> ranked = MatchRanking.rank(fourYesSearch(), me(), List.of(myOwnRow, noLocation, person(6, 1, t)), TODAY);
        assertEquals(List.of(6L), ranked.stream().map(m -> m.profile().ID).toList());
    }

    @Test
    void missingJoinDateSortsLast() {
        List<MatchRanking.Match> ranked = MatchRanking.rank(fourYesSearch(), me(), List.of(
                person(2, 4, null), person(3, 4, Instant.parse("2026-01-01T00:00:00Z"))), TODAY);
        assertEquals(List.of(3L, 2L), ranked.stream().map(m -> m.profile().ID).toList());
    }

    @Test
    void pageOutOfRange() {
        List<MatchRanking.Match> ranked = new ArrayList<>();
        IntStream.range(0, 25).forEach(i -> ranked.add(new MatchRanking.Match(person(i + 10, 4, null), 100)));
        assertEquals(20, MatchRanking.page(ranked, 0).size());
        assertEquals(20, MatchRanking.page(ranked, 1).size());
        assertEquals(5, MatchRanking.page(ranked, 2).size());
        assertEquals(0, MatchRanking.page(ranked, 3).size());
        assertEquals(0, MatchRanking.page(ranked, Integer.MAX_VALUE).size());
    }
}
