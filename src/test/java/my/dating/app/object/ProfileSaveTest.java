package my.dating.app.object;

import my.dating.app.object.enums.Interest;
import my.dating.app.object.enums.Language;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProfileSaveTest {

    // Spring calls the setters while binding the form, before the controller checks whose profile it is,
    // so they must not write to the database (there is none in this test: a write would throw).
    @Test
    void settingInterestsOnlyKeepsThemInMemory() {
        Profile profile = new Profile();
        Interest anyInterest = Interest.values()[0];
        Language anyLanguage = Language.values()[0];
        assertDoesNotThrow(() -> {
            profile.setInterests(Set.of(anyInterest));
            profile.setLanguages(Set.of(anyLanguage));
        });
        assertEquals(Set.of(anyInterest), profile.getInterests());
        assertEquals(Set.of(anyLanguage), profile.getLanguages());
    }

    // Update() writes every column, and the form only sends a photo when a new one is picked.
    @Test
    void storedAvatarIsKeptWhenNoNewPhotoIsSent() {
        Profile stored = new Profile();
        stored.Avatar = new byte[] {1, 2, 3};
        Profile posted = new Profile();
        posted.keepStoredAvatarFrom(stored);
        assertArrayEquals(new byte[] {1, 2, 3}, posted.Avatar);
    }

    @Test
    void newAvatarIsNotReplacedByTheStoredOne() {
        Profile stored = new Profile();
        stored.Avatar = new byte[] {1, 2, 3};
        Profile posted = new Profile();
        posted.Avatar = new byte[] {9};
        posted.keepStoredAvatarFrom(stored);
        assertArrayEquals(new byte[] {9}, posted.Avatar);
    }
}
