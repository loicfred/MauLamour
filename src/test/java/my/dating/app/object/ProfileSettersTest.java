package my.dating.app.object;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// The profile form posts "" for a select left on "Choose an option" and leaves out a field it doesn't have,
// so every setter must take "" and null as "no answer".
class ProfileSettersTest {

    @Test
    void enumSetterIgnoresBlank() {
        Profile profile = new Profile();
        profile.setPhysicalSkinColour("");
        profile.setPhysicalSkinColour(null);
        assertNull(profile.PhysicalSkinColour);
        profile.setPhysicalSkinColour("OLIVE");
        assertEquals("OLIVE", profile.PhysicalSkinColour);
    }

    @Test
    void existingSettersIgnoreNull() {
        Profile profile = new Profile();
        assertDoesNotThrow(() -> {
            profile.setGender(null);
            profile.setLifestyleDiet(null);
            profile.setWhoAmI(null);
        });
        assertNull(profile.Gender);
    }

    @Test
    void heightOutsideRangeIsIgnored() {
        Profile profile = new Profile();
        profile.setPhysicalHeightCm(99);
        profile.setPhysicalHeightCm(251);
        assertNull(profile.PhysicalHeightCm);
        profile.setPhysicalHeightCm(180);
        assertEquals(180, profile.PhysicalHeightCm);
    }

    // The number box sends "" when the member clears it; a primitive int made that a 400 and lost the whole form.
    @Test
    void pastPartnersMayBeEmpty() {
        Profile profile = new Profile();
        assertDoesNotThrow(() -> profile.setLovelifePastPartners(null));
        assertNull(profile.LovelifePastPartners);
        profile.setLovelifePastPartners(-3);
        assertEquals(0, profile.LovelifePastPartners);
    }

    @Test
    void finishedNeedsNameGenderBirthAndLocation() {
        Profile profile = new Profile();
        profile.Name = "Anya";
        profile.Gender = "FEMALE";
        profile.DateOfBirth = LocalDate.of(1998, 4, 2);
        profile.Latitude = -20.16;
        profile.Longitude = 57.50;
        assertTrue(profile.isFinished());
        profile.Latitude = null;
        assertFalse(profile.isFinished());
    }
}
