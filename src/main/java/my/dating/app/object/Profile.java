package my.dating.app.object;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.dating.app.object.enums.*;
import org.solarframework.db.api.Lazy;
import org.solarframework.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "profile")
public class Profile extends DatabaseObject.ID_OBJ<Long, Profile> {
    private transient List<Profile_Photo> photos;

    public String Name;
    public String getName() {
        return Name;
    }
    public void setName(String val) {
        Name = val;
    }

    public String Gender;
    public String getGender() {
        return Gender;
    }
    public void setGender(String val) {
        if (val != null && !val.isEmpty()) Gender = my.dating.app.object.enums.Gender.valueOf(val).name();
    }

    public byte[] Avatar = null;
    public void setAvatar(byte[] avatar) {
        Avatar = avatar;
    }
    // Update() writes every column, and the form only sends a photo when a new one is picked.
    public void keepStoredAvatarFrom(Profile stored) {
        if (Avatar == null && stored != null) Avatar = stored.Avatar;
    }

    public LocalDate DateOfBirth;
    public LocalDate getDateOfBirth() {
        return DateOfBirth;
    }
    public void setDateOfBirth(LocalDate val) {
        if (val != null) DateOfBirth = val;
    }

    public String WhoAmI;
    public String getWhoAmI() {
        return WhoAmI;
    }
    public void setWhoAmI(String val) {
        if (val != null && !val.isEmpty()) WhoAmI = val;
    }

    public String WhatIWant;
    public String getWhatIWant() {
        return WhatIWant;
    }
    public void setWhatIWant(String val) {
        if (val != null && !val.isEmpty()) WhatIWant = val;
    }

    public String WhatIDislike;
    public String getWhatIDislike() {
        return WhatIDislike;
    }
    public void setWhatIDislike(String val) {
        if (val != null && !val.isEmpty()) WhatIDislike = val;
    }

    public String PersonalityType;
    public String getPersonalityType() {
        return PersonalityType;
    }
    public void setPersonalityType(String val) {
        if (val != null && !val.isEmpty()) PersonalityType = my.dating.app.object.enums.PersonalityType.valueOf(val).name();
    }

    public transient Set<Interest> interests;
    public Set<Interest> getInterests() {
        return interests == null ? interests = DefaultDBService.doQueryAll("SELECT Value FROM profile_to_interest WHERE UserID = ?", ID).stream()
                .map(row -> Interest.valueOf(row.valueOf("Value").toString())).collect(Collectors.toSet()) : interests;
    }
    // Kept in memory only: Spring calls this while binding the form, before the controller checks whose profile it is.
    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
    }

    public transient Set<Language> languages;
    public Set<Language> getLanguages() {
        return languages == null ? languages = DefaultDBService.doQueryAll("SELECT Value FROM profile_to_language WHERE UserID = ?", ID).stream()
                .map(row -> Language.valueOf(row.valueOf("Value").toString())).collect(Collectors.toSet()) : languages;
    }
    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    // Replaces the stored interests and languages with the ones set on this profile. With Profile.class, so the cached
    // rows of the old ones are cleared; the views' own cache is ViewCache's.
    public void saveInterestsAndLanguages() {
        DefaultDBService.doUpdate(Profile.class, "DELETE FROM profile_to_interest WHERE UserID = ?", ID);
        for (Interest I : getInterests())
            DefaultDBService.doUpdate(Profile.class, "INSERT INTO profile_to_interest (UserID, Value) VALUES (?,?)", ID, I.name());
        DefaultDBService.doUpdate(Profile.class, "DELETE FROM profile_to_language WHERE UserID = ?", ID);
        for (Language I : getLanguages())
            DefaultDBService.doUpdate(Profile.class, "INSERT INTO profile_to_language (UserID, Value) VALUES (?,?)", ID, I.name());
        ViewCache.forgetProfiles();
    }

    // Reads leave the photo out (a blob column), so it is fetched on its own, only where the bytes are sent.
    public byte[] fetchAvatar() {
        return Lazy.unloaded(Avatar) || Avatar == null ? refetchAttribute("Avatar", byte[].class) : Avatar;
    }

    public Double Latitude;
    public Double Longitude;
    public Double getLatitude() {
        return Latitude;
    }
    public Double getLongitude() {
        return Longitude;
    }
    public void setLatitude(Double val) {
        Latitude = val;
    }
    public void setLongitude(Double val) {
        Longitude = val;
    }


    public String LifestyleEducation;
    public String getLifestyleEducation() {
        return LifestyleEducation;
    }
    public void setLifestyleEducation(String val) {
        if (val != null && !val.isEmpty()) LifestyleEducation = val;
    }

    public String LifestyleOccupation;
    public String getLifestyleOccupation() {
        return LifestyleOccupation;
    }
    public void setLifestyleOccupation(String val) {
        if (val != null && !val.isEmpty()) LifestyleOccupation = val;
    }

    public String LifestyleReligion;
    public String getLifestyleReligion() {
        return LifestyleReligion;
    }
    public void setLifestyleReligion(String val) {
        if (val != null && !val.isEmpty()) LifestyleReligion = my.dating.app.object.enums.Religion.valueOf(val).name();
    }

    public String LifestyleDiet;
    public String getLifestyleDiet() {
        return LifestyleDiet;
    }
    public void setLifestyleDiet(String val) {
        if (val != null && !val.isEmpty()) LifestyleDiet = Diet.valueOf(val).name();
    }

    public String LifestyleWouldCook;
    public String getLifestyleWouldCook() {
        return LifestyleWouldCook;
    }
    public void setLifestyleWouldCook(String val) {
        if (val != null && !val.isEmpty()) LifestyleWouldCook = LifestylePreference.valueOf(val).name();
    }

    public String LifestyleWouldChore;
    public String getLifestyleWouldChore() {
        return LifestyleWouldChore;
    }
    public void setLifestyleWouldChore(String val) {
        if (val != null && !val.isEmpty()) LifestyleWouldChore = LifestylePreference.valueOf(val).name();
    }

    public String LifestyleSmoking;
    public String getLifestyleSmoking() {
        return LifestyleSmoking;
    }
    public void setLifestyleSmoking(String val) {
        if (val != null && !val.isEmpty()) LifestyleSmoking = BadHabit.valueOf(val).name();
    }

    public String LifestyleDrinking;
    public String getLifestyleDrinking() {
        return LifestyleDrinking;
    }
    public void setLifestyleDrinking(String val) {
        if (val != null && !val.isEmpty()) LifestyleDrinking = BadHabit.valueOf(val).name();
    }

    public String LifestyleExercise;
    public String getLifestyleExercise() {
        return LifestyleExercise;
    }
    public void setLifestyleExercise(String val) {
        if (val != null && !val.isEmpty()) LifestyleExercise = BadHabit.valueOf(val).name();
    }

    public String LifestyleAllergies;
    public String getLifestyleAllergies() {
        return LifestyleAllergies;
    }
    public void setLifestyleAllergies(String val) {
        if (val != null && !val.isEmpty()) LifestyleAllergies = val;
    }

    public String LifestyleDisability;
    public String getLifestyleDisability() {
        return LifestyleDisability;
    }
    public void setLifestyleDisability(String val) {
        if (val != null && !val.isEmpty()) LifestyleDisability = val;
    }

    public Boolean LifestylePets;
    public Boolean getLifestylePets() {
        return LifestylePets;
    }
    public void setLifestylePets(Boolean val) {
        LifestylePets = val;
    }

    public String LifestyleEducationLevel;
    public String getLifestyleEducationLevel() {
        return LifestyleEducationLevel;
    }
    public void setLifestyleEducationLevel(String val) {
        if (val != null && !val.isEmpty()) LifestyleEducationLevel = EducationLevel.valueOf(val).name();
    }

    public String LifestyleLivingSituation;
    public String getLifestyleLivingSituation() {
        return LifestyleLivingSituation;
    }
    public void setLifestyleLivingSituation(String val) {
        if (val != null && !val.isEmpty()) LifestyleLivingSituation = LivingSituation.valueOf(val).name();
    }


    public Integer PhysicalHeightCm;
    public Integer getPhysicalHeightCm() {
        return PhysicalHeightCm;
    }
    public void setPhysicalHeightCm(Integer val) {
        if (val != null && val >= 100 && val <= 250) PhysicalHeightCm = val;
    }

    public String PhysicalBodyType;
    public String getPhysicalBodyType() {
        return PhysicalBodyType;
    }
    public void setPhysicalBodyType(String val) {
        if (val != null && !val.isEmpty()) PhysicalBodyType = BodyType.valueOf(val).name();
    }

    public String PhysicalSkinColour;
    public String getPhysicalSkinColour() {
        return PhysicalSkinColour;
    }
    public void setPhysicalSkinColour(String val) {
        if (val != null && !val.isEmpty()) PhysicalSkinColour = SkinColour.valueOf(val).name();
    }

    public String PhysicalEyeColour;
    public String getPhysicalEyeColour() {
        return PhysicalEyeColour;
    }
    public void setPhysicalEyeColour(String val) {
        if (val != null && !val.isEmpty()) PhysicalEyeColour = EyeColour.valueOf(val).name();
    }

    public String PhysicalHairColour;
    public String getPhysicalHairColour() {
        return PhysicalHairColour;
    }
    public void setPhysicalHairColour(String val) {
        if (val != null && !val.isEmpty()) PhysicalHairColour = HairColour.valueOf(val).name();
    }

    public String PhysicalEthnicBackground;
    public String getPhysicalEthnicBackground() {
        return PhysicalEthnicBackground;
    }
    public void setPhysicalEthnicBackground(String val) {
        if (val != null && !val.isEmpty()) PhysicalEthnicBackground = EthnicBackground.valueOf(val).name();
    }

    public Boolean PhysicalHasTattoos;
    public Boolean getPhysicalHasTattoos() {
        return PhysicalHasTattoos;
    }
    public void setPhysicalHasTattoos(Boolean val) {
        PhysicalHasTattoos = val;
    }


    public boolean MessageLeaveOnRead = false;
    public boolean getMessageLeaveOnRead() {
        return MessageLeaveOnRead;
    }
    public void setMessageLeaveOnRead(boolean val) {
        MessageLeaveOnRead = val;
    }

    public String MessageReplySpeed;
    public String getMessageReplySpeed() {
        return MessageReplySpeed;
    }
    public void setMessageReplySpeed(String val) {
        if (val != null && !val.isEmpty()) MessageReplySpeed = Rapidity.valueOf(val).name();
    }


    public Integer LovelifePastPartners; // null when the member leaves the box empty
    public Integer getLovelifePastPartners() {
        return LovelifePastPartners;
    }
    public void setLovelifePastPartners(Integer val) {
        LovelifePastPartners = val == null ? null : Math.max(0, val);
    }

    public boolean LovelifeVirginity = false;
    public boolean getLovelifeVirginity() {
        return LovelifeVirginity;
    }
    public void setLovelifeVirginity(boolean val) {
        LovelifeVirginity = val;
    }

    public boolean LovelifeMarriage = true;
    public boolean getLovelifeMarriage() {
        return LovelifeMarriage;
    }
    public void setLovelifeMarriage(boolean val) {
        LovelifeMarriage = val;
    }

    public boolean LovelifeReligionMatter = false;
    public boolean getLovelifeReligionMatter() {
        return LovelifeReligionMatter;
    }
    public void setLovelifeReligionMatter(boolean val) {
        LovelifeReligionMatter = val;
    }

    public String LovelifeWantKids;
    public String getLovelifeWantKids() {
        return LovelifeWantKids;
    }
    public void setLovelifeWantKids(String val) {
        if (val != null && !val.isEmpty()) LovelifeWantKids = KidsPreference.valueOf(val).name();
    }

    public boolean LovelifeAlreadyHaveChildren = false;
    public boolean getLovelifeAlreadyHaveChildren() {
        return LovelifeAlreadyHaveChildren;
    }
    public void setLovelifeAlreadyHaveChildren(boolean val) {
        LovelifeAlreadyHaveChildren = val;
    }

    public boolean LovelifeWillingToRelocate = false;
    public boolean getLovelifeWillingToRelocate() {
        return LovelifeWillingToRelocate;
    }
    public void setLovelifeWillingToRelocate(boolean val) {
        LovelifeWillingToRelocate = val;
    }

    public String LovelifeRelationshipStatus;
    public String getLovelifeRelationshipStatus() {
        return LovelifeRelationshipStatus;
    }
    public void setLovelifeRelationshipStatus(String val) {
        if (val != null && !val.isEmpty()) LovelifeRelationshipStatus = RelationshipStatus.valueOf(val).name();
    }

    // Only a finished profile shows up in searches and sets off new-match notifications.
    public boolean isFinished() {
        return Name != null && !Name.isBlank() && Gender != null && DateOfBirth != null && Latitude != null && Longitude != null;
    }

    public record Answer(String question, String answer) {}

    // Every answered question in the wording the profile page shows; unanswered ones are left out.
    public List<Answer> getAnswers() {
        List<Answer> answers = new ArrayList<>();
        addAnswer(answers, "Gender", shown(my.dating.app.object.enums.Gender.class, Gender));
        addAnswer(answers, "Personality", shown(my.dating.app.object.enums.PersonalityType.class, PersonalityType));
        addAnswer(answers, "Height", PhysicalHeightCm == null ? null : PhysicalHeightCm + " cm");
        addAnswer(answers, "Body type", shown(BodyType.class, PhysicalBodyType));
        addAnswer(answers, "Skin colour", shown(SkinColour.class, PhysicalSkinColour));
        addAnswer(answers, "Eye colour", shown(EyeColour.class, PhysicalEyeColour));
        addAnswer(answers, "Hair colour", shown(HairColour.class, PhysicalHairColour));
        addAnswer(answers, "Ethnic background", shown(EthnicBackground.class, PhysicalEthnicBackground));
        addAnswer(answers, "Tattoos", yesNo(PhysicalHasTattoos));
        addAnswer(answers, "Studies", LifestyleEducation);
        addAnswer(answers, "Highest education", shown(EducationLevel.class, LifestyleEducationLevel));
        addAnswer(answers, "Job", LifestyleOccupation);
        addAnswer(answers, "Lives", shown(LivingSituation.class, LifestyleLivingSituation));
        addAnswer(answers, "Religion", shown(Religion.class, LifestyleReligion));
        addAnswer(answers, "Diet", shown(Diet.class, LifestyleDiet));
        addAnswer(answers, "Would cook for a partner", shown(LifestylePreference.class, LifestyleWouldCook));
        addAnswer(answers, "Would do the chores", shown(LifestylePreference.class, LifestyleWouldChore));
        addAnswer(answers, "Smokes", shown(BadHabit.class, LifestyleSmoking));
        addAnswer(answers, "Drinks alcohol", shown(BadHabit.class, LifestyleDrinking));
        addAnswer(answers, "Exercises", shown(BadHabit.class, LifestyleExercise));
        addAnswer(answers, "Allergies or illness", LifestyleAllergies);
        addAnswer(answers, "Disabilities", LifestyleDisability);
        addAnswer(answers, "Pets", yesNo(LifestylePets));
        addAnswer(answers, "Replies to messages", shown(Rapidity.class, MessageReplySpeed));
        addAnswer(answers, "Often leaves people on read", yesNo(MessageLeaveOnRead));
        addAnswer(answers, "Relationship status", shown(RelationshipStatus.class, LovelifeRelationshipStatus));
        addAnswer(answers, "Past partners", LovelifePastPartners == null ? null : String.valueOf(LovelifePastPartners));
        addAnswer(answers, "Looking to marry", yesNo(LovelifeMarriage));
        addAnswer(answers, "Partner must share my religion", yesNo(LovelifeReligionMatter));
        addAnswer(answers, "Wants children", shown(KidsPreference.class, LovelifeWantKids));
        addAnswer(answers, "Already has children", yesNo(LovelifeAlreadyHaveChildren));
        addAnswer(answers, "Willing to relocate", yesNo(LovelifeWillingToRelocate));
        return answers;
    }
    private static void addAnswer(List<Answer> answers, String question, String answer) {
        if (answer != null && !answer.isBlank()) answers.add(new Answer(question, answer));
    }
    private static <E extends Enum<E> & Displayable> String shown(Class<E> type, String name) {
        return name == null ? null : Enum.valueOf(type, name).getDisplayName();
    }
    private static String yesNo(Boolean value) {
        return value == null ? null : value ? "Yes" : "No";
    }



    public Profile() {}
    public Profile(long userid) {
        this.ID = userid;
        Write();
    }

    public static Profile getById(long id) {
        return SolarDBManager.getById(Profile.class, id).orElse(null);
    }
    public static void ensureExists(long userid) {
        if (SolarDBManager.Count(Profile.class, "ID = ?", userid) == 0) new Profile(userid);
    }

    public List<Profile_Photo> getPhotos() {
        return photos == null ? photos = Profile_Photo.getByUser(ID) : photos;
    }


    @Table(name = "profile_view") // a view class needs its own @Table, or getTableName takes the parent's (ViewTableNamesTest)
    public static class Profile_View extends Profile {
        public String Username;
        public String Email;
        public Long Age;
        public Instant JoinedAt; // when the account was created: equal match scores show the newest member first
        public Boolean HasAvatar; // reads leave Avatar itself NULL (a blob), so pages ask this instead

        public Profile_View() {}

        public static Profile_View getView(long id) {
            return SolarDBManager.getWhere(Profile_View.class, "ID = ?", id).orElse(null);
        }
        public static Profile_View getView(String username) {
            return SolarDBManager.getWhere(Profile_View.class, "Username = ?", username).orElse(null);
        }
    }
}
