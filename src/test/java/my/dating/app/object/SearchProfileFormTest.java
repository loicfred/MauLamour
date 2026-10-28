package my.dating.app.object;

import my.dating.app.object.enums.SearchChoice;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// The form posts each field once, each ticked choice as a repeated parameter, and "" for "I don't mind".
class SearchProfileFormTest {

    private static Map<String, List<String>> form(String... keyValues) {
        Map<String, List<String>> form = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2)
            form.computeIfAbsent(keyValues[i], _ -> new java.util.ArrayList<>()).add(keyValues[i + 1]);
        return form;
    }

    @Test
    void nameRequired() {
        assertEquals(List.of("Give this search a name, for example \"Weekend in Curepipe\"."),
                new Search_Profile().applyForm(form("Name", "  ")));
    }

    @Test
    void nameTooLong() {
        assertEquals(List.of("Keep the name to 60 characters or fewer."),
                new Search_Profile().applyForm(form("Name", "x".repeat(61))));
    }

    @Test
    void minAboveMax() {
        assertEquals(List.of("The lowest age must be at or below the highest age."),
                new Search_Profile().applyForm(form("Name", "A", "MinAge", "40", "MaxAge", "30")));
    }

    @Test
    void ageOutOfRange() {
        assertEquals(List.of("Ages go from 18 to 99."),
                new Search_Profile().applyForm(form("Name", "A", "MinAge", "17")));
    }

    @Test
    void heightOutOfRange() {
        assertEquals(List.of("Heights go from 100 to 250 cm."),
                new Search_Profile().applyForm(form("Name", "A", "MaxHeightCm", "300")));
    }

    @Test
    void notANumber() {
        assertEquals(List.of("Write the lowest age as a whole number, for example 30."),
                new Search_Profile().applyForm(form("Name", "A", "MinAge", "thirty")));
    }

    @Test
    void radiusTooSmall() {
        assertEquals(List.of("The distance must be at least 1 km, or left empty."),
                new Search_Profile().applyForm(form("Name", "A", "LocationRadiusKM", "0")));
    }

    @Test
    void blankFieldsMeanDontMind() {
        Search_Profile sp = new Search_Profile();
        List<String> errors = sp.applyForm(form("Name", "A", "MinAge", "", "LifestylePets", "", "GENDER", ""));
        assertEquals(List.of(), errors);
        assertTrue(sp.getChoices().isEmpty());
        assertNull(sp.MinAge);
        Search_Profile.YES_NO_SETTINGS.forEach(s -> assertNull(s.wanted().apply(sp), s.field()));
    }

    @Test
    void valuesAreApplied() {
        Search_Profile sp = new Search_Profile();
        sp.applyForm(form("Name", " Weekend ", "MinAge", "25", "LovelifeMarriage", "true", "LifestylePets", "false",
                "RELIGION", "HINDUISM", "RELIGION", "ISLAM"));
        assertEquals("Weekend", sp.Name);
        assertEquals(25, sp.MinAge);
        assertTrue(sp.LovelifeMarriage);
        assertFalse(sp.LifestylePets);
        assertEquals(Set.of("HINDUISM", "ISLAM"), sp.getChoices().get(SearchChoice.RELIGION));
    }

    @Test
    void unknownChoiceValueIgnored() {
        Search_Profile sp = new Search_Profile();
        sp.applyForm(form("Name", "A", "SKIN_COLOUR", "OLIVE", "SKIN_COLOUR", "NOPE"));
        assertEquals(Set.of("OLIVE"), sp.getChoices().get(SearchChoice.SKIN_COLOUR));
    }

    @Test
    void savingAgainClearsWhatWasEmptied() {
        Search_Profile sp = new Search_Profile();
        sp.applyForm(form("Name", "A", "MinAge", "25", "LifestylePets", "true", "GENDER", "MALE"));
        sp.applyForm(form("Name", "A"));
        assertNull(sp.MinAge);
        assertNull(sp.LifestylePets);
        assertTrue(sp.getChoices().isEmpty());
    }
}
