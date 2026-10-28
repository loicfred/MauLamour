package my.dating.app.object;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

// A missing search profile and someone else's give the same answer, so nobody can probe which ids exist.
class SearchProfileAccessTest {

    @Test
    void otherMembersProfileIsRefused() {
        Search_Profile sp = new Search_Profile();
        sp.UserID = 1L;
        assertTrue(sp.isOwnedBy(1));
        assertFalse(sp.isOwnedBy(2));
        assertThrows(AccessDeniedException.class, () -> Search_Profile.checkOwner(sp, 2));
        assertSame(sp, Search_Profile.checkOwner(sp, 1));
    }

    @Test
    void missingProfileIsRefused() {
        assertThrows(AccessDeniedException.class, () -> Search_Profile.checkOwner(null, 1));
    }

    @Test
    void profileWithoutOwnerIsRefused() {
        assertThrows(AccessDeniedException.class, () -> Search_Profile.checkOwner(new Search_Profile(), 1));
    }
}
