package my.dating.app.object;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileAnswersTest {

    @Test
    void answersUseTheReadableWording() {
        Profile profile = new Profile();
        profile.PhysicalSkinColour = "DARK_BROWN";
        profile.PhysicalHeightCm = 172;
        profile.PhysicalHasTattoos = false;
        List<Profile.Answer> answers = profile.getAnswers();
        assertTrue(answers.contains(new Profile.Answer("Skin colour", "Dark brown")));
        assertTrue(answers.contains(new Profile.Answer("Height", "172 cm")));
        assertTrue(answers.contains(new Profile.Answer("Tattoos", "No")));
    }

    @Test
    void blankAnswersAreLeftOut() {
        Profile profile = new Profile();
        profile.PhysicalEyeColour = "GREEN";
        assertTrue(profile.getAnswers().stream().noneMatch(a -> a.question().equals("Skin colour")));
        assertTrue(profile.getAnswers().stream().noneMatch(a -> a.question().equals("Allergies or illness")));
    }
}
