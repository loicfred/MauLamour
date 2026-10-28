package my.dating.app.controller;

import my.dating.app.object.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.WebDataBinder;

import static org.junit.jupiter.api.Assertions.*;

// @ModelAttribute Profile binds every property with a setter, so a forged form could set the avatar without passing
// the picture check, or post another member's ID. The controller's binder refuses both.
class ProfileBindingTest {

    @Test
    void forgedAvatarAndIdAreNotBound() {
        Profile profile = new Profile();
        WebDataBinder binder = new WebDataBinder(profile);
        ProfileController.refuseUnsafeFields(binder);
        MutablePropertyValues form = new MutablePropertyValues();
        form.add("Name", "Anya");
        form.add("Avatar", "not a picture");
        form.add("avatar", "not a picture");
        form.add("ID", "999");
        binder.bind(form);
        assertEquals("Anya", profile.Name);
        assertNull(profile.Avatar);
        assertNull(profile.ID);
    }
}
