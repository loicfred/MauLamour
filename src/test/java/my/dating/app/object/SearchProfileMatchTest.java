package my.dating.app.object;

import my.dating.app.object.enums.Interest;
import my.dating.app.object.enums.SearchChoice;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

// The owner's rule: every setting counts the same, and the % is the settings matched divided by all the settings.
// Settings left on "I don't mind" don't count; a blank answer counts as not matched.
class SearchProfileMatchTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 28);

    private static Profile candidate() {
        Profile p = new Profile();
        p.Name = "Candidate";
        p.Gender = "MALE";
        p.DateOfBirth = TODAY.minusYears(25);
        p.Latitude = -20.3163; // Curepipe
        p.Longitude = 57.5259;
        p.interests = Set.of();
        p.languages = Set.of();
        return p;
    }
    private static Profile searcher() {
        Profile p = new Profile();
        p.Latitude = -20.1609; // Port Louis
        p.Longitude = 57.5012;
        return p;
    }
    private static Search_Profile search(Map<SearchChoice, Set<String>> choices) {
        Search_Profile sp = new Search_Profile();
        sp.setChoices(new EnumMap<>(SearchChoice.class));
        sp.getChoices().putAll(choices);
        return sp;
    }

    @Test
    void noSettingsIsHundred() {
        assertEquals(100, search(Map.of()).matchPercent(candidate(), searcher(), TODAY));
    }

    @Test
    void sevenOfNineIsSeventySeven() {
        Profile c = candidate();
        c.LifestyleReligion = "HINDUISM";
        c.PhysicalSkinColour = "OLIVE";
        c.PhysicalEyeColour = "BROWN";
        c.PhysicalHairColour = "BLACK";
        c.PhysicalHasTattoos = false;
        c.LifestylePets = true;
        c.PhysicalHeightCm = 180;
        Search_Profile sp = search(Map.of(
                SearchChoice.GENDER, Set.of("MALE"),
                SearchChoice.RELIGION, Set.of("HINDUISM"),
                SearchChoice.SKIN_COLOUR, Set.of("OLIVE"),
                SearchChoice.EYE_COLOUR, Set.of("GREEN"),      // not matched
                SearchChoice.HAIR_COLOUR, Set.of("BLACK")));
        sp.PhysicalHasTattoos = false;
        sp.LifestylePets = false;                              // not matched
        sp.MinAge = 20;
        sp.MinHeightCm = 170;
        assertEquals(77, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void fourOfFiveIsEighty() {
        Profile c = candidate();
        c.LifestyleReligion = "HINDUISM";
        c.PhysicalSkinColour = "OLIVE";
        c.PhysicalEyeColour = "BROWN";
        Search_Profile sp = search(Map.of(
                SearchChoice.GENDER, Set.of("MALE"),
                SearchChoice.RELIGION, Set.of("HINDUISM"),
                SearchChoice.SKIN_COLOUR, Set.of("OLIVE"),
                SearchChoice.EYE_COLOUR, Set.of("BROWN"),
                SearchChoice.HAIR_COLOUR, Set.of("RED")));     // not matched
        assertEquals(80, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void blankAnswerIsNotMatched() {
        Search_Profile sp = search(Map.of(SearchChoice.SKIN_COLOUR, Set.of("OLIVE")));
        assertEquals(0, sp.matchPercent(candidate(), searcher(), TODAY));
    }

    @Test
    void choiceMatchesAnyTicked() {
        Profile c = candidate();
        c.LifestyleReligion = "HINDUISM";
        Search_Profile sp = search(Map.of(SearchChoice.RELIGION, Set.of("CHRISTIANITY", "HINDUISM")));
        assertEquals(100, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void sharedInterestCountsOnce() {
        Profile c = candidate();
        c.interests = Set.of(Interest.MUSIC);
        Search_Profile sp = search(Map.of(SearchChoice.INTERESTS, Set.of("MUSIC", "TRAVELING", "SPORTS")));
        assertEquals(100, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void genderMismatchOnlyLowersScore() {
        Search_Profile sp = search(Map.of(SearchChoice.GENDER, Set.of("FEMALE")));
        sp.MinAge = 20;
        assertEquals(50, sp.matchPercent(candidate(), searcher(), TODAY));
    }

    @Test
    void openEndedAgeRange() {
        Profile c = candidate();
        c.DateOfBirth = TODAY.minusYears(45);
        Search_Profile sp = search(Map.of());
        sp.MinAge = 30;
        assertEquals(100, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void ageWithoutBirthDateIsNotMatched() {
        Profile c = candidate();
        c.DateOfBirth = null;
        Search_Profile sp = search(Map.of());
        sp.MaxAge = 40;
        assertEquals(0, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void distanceWithinRadius() {
        Search_Profile sp = search(Map.of());
        sp.LocationRadiusKM = 50; // Port Louis to Curepipe is about 17 km
        assertEquals(100, sp.matchPercent(candidate(), searcher(), TODAY));
        sp.LocationRadiusKM = 10;
        assertEquals(0, sp.matchPercent(candidate(), searcher(), TODAY));
    }

    @Test
    void searcherWithoutLocationNeverMatchesDistance() {
        Profile me = searcher();
        me.Latitude = null;
        Search_Profile sp = search(Map.of());
        sp.LocationRadiusKM = 5000;
        assertEquals(0, sp.matchPercent(candidate(), me, TODAY));
    }

    @Test
    void allergiesYesMeansTextFilled() {
        Search_Profile sp = search(Map.of());
        sp.LifestyleAllergies = true;
        Profile c = candidate();
        c.LifestyleAllergies = "Peanuts";
        assertEquals(100, sp.matchPercent(c, searcher(), TODAY));
        c.LifestyleAllergies = "";
        assertEquals(0, sp.matchPercent(c, searcher(), TODAY));
    }

    @Test
    void pastPartnersComparesWithTheCount() {
        Search_Profile sp = search(Map.of());
        sp.LovelifeHadPastPartners = false;
        Profile c = candidate();
        c.LovelifePastPartners = 0;
        assertEquals(100, sp.matchPercent(c, searcher(), TODAY));
        c.LovelifePastPartners = 2;
        assertEquals(0, sp.matchPercent(c, searcher(), TODAY));
    }
}
