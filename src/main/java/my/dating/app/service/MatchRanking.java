package my.dating.app.service;

import my.dating.app.object.Profile;
import my.dating.app.object.Search_Profile;
import my.dating.app.object.enums.Interest;
import my.dating.app.object.enums.Language;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// FR-5: the members ranked by how well they match one search profile.
@Service
public class MatchRanking {
    public static final int PAGE_SIZE = 20;

    public record Match(Profile.Profile_View profile, int percent) {}

    // Highest % first; equal scores show the newest member first. Leaves out the searcher and unfinished profiles.
    public static List<Match> rank(Search_Profile search, Profile searcher, List<Profile.Profile_View> candidates, LocalDate today) {
        return candidates.stream()
                .filter(c -> c.isFinished() && !Objects.equals(c.ID, searcher.ID))
                .map(c -> new Match(c, search.matchPercent(c, searcher, today)))
                .sorted(Comparator.comparingInt(Match::percent).reversed()
                        .thenComparing(m -> m.profile().JoinedAt, Comparator.nullsLast(Comparator.<Instant>reverseOrder())))
                .toList();
    }

    // One page of PAGE_SIZE; a page below 1 is page 1, a page past the end is empty.
    public static List<Match> page(List<Match> ranked, int page) {
        long from = (Math.max(page, 1) - 1L) * PAGE_SIZE;
        if (from >= ranked.size()) return List.of();
        return ranked.subList((int) from, (int) Math.min(from + PAGE_SIZE, ranked.size()));
    }

    // Every finished profile with its interests and languages, in three queries whatever the number of members (NFR-10).
    public List<Profile.Profile_View> loadCandidates() {
        List<Profile.Profile_View> candidates = SolarDBManager.getAllWhere(Profile.Profile_View.class,
                "Name IS NOT NULL AND Gender IS NOT NULL AND DateOfBirth IS NOT NULL AND Latitude IS NOT NULL AND Longitude IS NOT NULL");
        Map<Long, Set<Interest>> interests = valuesByUser("profile_to_interest", Interest::valueOf);
        Map<Long, Set<Language>> languages = valuesByUser("profile_to_language", Language::valueOf);
        for (Profile.Profile_View c : candidates) {
            c.setInterests(interests.getOrDefault(c.ID, Set.of()));
            c.setLanguages(languages.getOrDefault(c.ID, Set.of()));
        }
        return candidates;
    }

    // table is one of the two constants above, never user input.
    private static <E extends Enum<E>> Map<Long, Set<E>> valuesByUser(String table, Function<String, E> parse) {
        Map<Long, Set<E>> byUser = new HashMap<>();
        for (var row : DefaultDBService.doQueryAll("SELECT UserID, Value FROM " + table)) {
            try {
                byUser.computeIfAbsent(((Number) row.valueOf("UserID")).longValue(), _ -> new HashSet<>()).add(parse.apply(row.valueOf("Value").toString()));
            } catch (IllegalArgumentException ignored) {} // a value the code no longer knows
        }
        return byUser;
    }
}
