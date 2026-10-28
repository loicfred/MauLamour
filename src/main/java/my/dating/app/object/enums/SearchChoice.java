package my.dating.app.object.enums;

import my.dating.app.object.Profile;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// The search profile settings where the member ticks the answers they accept. A person matches one when their answer
// is among the ticked ones (for interests and languages: when they share at least one). Stored in search_profile_choice.
public enum SearchChoice {
    GENDER("Essentials", "Gender", Gender.class, p -> one(p.Gender)),
    PERSONALITY("Essentials", "Personality", PersonalityType.class, p -> one(p.PersonalityType)),
    INTERESTS("Essentials", "Interests (at least one shared)", Interest.class, p -> names(p.getInterests())),
    LANGUAGES("Essentials", "Languages (at least one shared)", Language.class, p -> names(p.getLanguages())),
    BODY_TYPE("Physical", "Body type", BodyType.class, p -> one(p.PhysicalBodyType)),
    SKIN_COLOUR("Physical", "Skin colour", SkinColour.class, p -> one(p.PhysicalSkinColour)),
    EYE_COLOUR("Physical", "Eye colour", EyeColour.class, p -> one(p.PhysicalEyeColour)),
    HAIR_COLOUR("Physical", "Hair colour", HairColour.class, p -> one(p.PhysicalHairColour)),
    ETHNIC_BACKGROUND("Physical", "Ethnic background", EthnicBackground.class, p -> one(p.PhysicalEthnicBackground)),
    EDUCATION_LEVEL("Lifestyle", "Highest education", EducationLevel.class, p -> one(p.LifestyleEducationLevel)),
    LIVING_SITUATION("Lifestyle", "Lives", LivingSituation.class, p -> one(p.LifestyleLivingSituation)),
    RELIGION("Lifestyle", "Religion", Religion.class, p -> one(p.LifestyleReligion)),
    DIET("Lifestyle", "Diet", Diet.class, p -> one(p.LifestyleDiet)),
    WOULD_COOK("Lifestyle", "Would cook for a partner", LifestylePreference.class, p -> one(p.LifestyleWouldCook)),
    WOULD_CHORE("Lifestyle", "Would do the chores", LifestylePreference.class, p -> one(p.LifestyleWouldChore)),
    SMOKING("Lifestyle", "Smokes", BadHabit.class, p -> one(p.LifestyleSmoking)),
    DRINKING("Lifestyle", "Drinks alcohol", BadHabit.class, p -> one(p.LifestyleDrinking)),
    EXERCISE("Lifestyle", "Exercises", BadHabit.class, p -> one(p.LifestyleExercise)),
    REPLY_SPEED("Messaging", "Replies to messages", Rapidity.class, p -> one(p.MessageReplySpeed)),
    RELATIONSHIP_STATUS("Love life", "Relationship status", RelationshipStatus.class, p -> one(p.LovelifeRelationshipStatus)),
    WANT_KIDS("Love life", "Wants children", KidsPreference.class, p -> one(p.LovelifeWantKids));

    private final String section;
    private final String label;
    private final Class<? extends Enum<?>> options;
    private final Function<Profile, Set<String>> answersOf;

    SearchChoice(String section, String label, Class<? extends Enum<?>> options, Function<Profile, Set<String>> answersOf) {
        this.section = section;
        this.label = label;
        this.options = options;
        this.answersOf = answersOf;
    }

    public String getSection() {
        return section;
    }
    // The choices under their form section, sections in the order they first appear.
    public static Map<String, List<SearchChoice>> bySection() {
        return Arrays.stream(values()).collect(Collectors.groupingBy(SearchChoice::getSection, LinkedHashMap::new, Collectors.toList()));
    }
    public String getLabel() {
        return label;
    }
    public List<Displayable> getOptions() {
        return Arrays.stream(options.getEnumConstants()).map(o -> (Displayable) o).toList();
    }
    public boolean isOption(String value) {
        return Arrays.stream(options.getEnumConstants()).anyMatch(o -> o.name().equals(value));
    }
    // The person's answer as a set: one element for a single answer, none when they left it blank.
    public Set<String> answersOf(Profile person) {
        return answersOf.apply(person);
    }

    private static Set<String> one(String answer) {
        return answer == null || answer.isBlank() ? Set.of() : Set.of(answer);
    }
    private static Set<String> names(Set<? extends Enum<?>> answers) {
        return answers == null ? Set.of() : answers.stream().map(Enum::name).collect(Collectors.toSet());
    }
}
