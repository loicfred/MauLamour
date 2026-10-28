package my.dating.app.object;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.dating.app.object.enums.SearchChoice;
import org.solarframework.auth.obj.Account_User;
import org.solarframework.db.spring.DatabaseObject;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// Someone a member is looking for. Every setting left empty means "I don't mind" and doesn't count towards the match %.
@Entity
@Table(name = "search_profile")
public class Search_Profile extends DatabaseObject.ID_OBJ<Long, Search_Profile> {

    public Search_Profile() {}

    public Long UserID;
    public String Name;
    public String getName() { return Name; }

    public Integer MinAge;
    public Integer MaxAge;
    public Integer MinHeightCm;
    public Integer MaxHeightCm;
    public Integer LocationRadiusKM; // around the member's own profile location

    // Yes / No / null ("I don't mind"), compared with the person's own answer.
    public Boolean PhysicalHasTattoos;
    public Boolean LifestylePets;
    public Boolean LifestyleAllergies;    // Yes: the person wrote something about allergies or illness
    public Boolean LifestyleDisability;   // Yes: the person wrote something about a disability
    public Boolean MessageLeaveOnRead;
    public Boolean LovelifeHadPastPartners;
    public Boolean LovelifeVirginity;
    public Boolean LovelifeMarriage;
    public Boolean LovelifeReligionMatter;
    public Boolean LovelifeAlreadyHaveChildren;
    public Boolean LovelifeWillingToRelocate;

    // Each yes/no setting with the person's answer to it, in the order the form shows them.
    public record YesNoSetting(String field, String label, Function<Search_Profile, Boolean> wanted, BiConsumer<Search_Profile, Boolean> setWanted, Function<Profile, Boolean> answerOf) {}
    public static final List<YesNoSetting> YES_NO_SETTINGS = List.of(
            new YesNoSetting("PhysicalHasTattoos", "Has tattoos", s -> s.PhysicalHasTattoos, (s, v) -> s.PhysicalHasTattoos = v, p -> p.PhysicalHasTattoos),
            new YesNoSetting("LifestylePets", "Has pets", s -> s.LifestylePets, (s, v) -> s.LifestylePets = v, p -> p.LifestylePets),
            new YesNoSetting("LifestyleAllergies", "Has allergies or an illness", s -> s.LifestyleAllergies, (s, v) -> s.LifestyleAllergies = v, p -> isFilled(p.LifestyleAllergies)),
            new YesNoSetting("LifestyleDisability", "Has a disability", s -> s.LifestyleDisability, (s, v) -> s.LifestyleDisability = v, p -> isFilled(p.LifestyleDisability)),
            new YesNoSetting("MessageLeaveOnRead", "Often leaves people on read", s -> s.MessageLeaveOnRead, (s, v) -> s.MessageLeaveOnRead = v, p -> p.MessageLeaveOnRead),
            new YesNoSetting("LovelifeHadPastPartners", "Has had partners before", s -> s.LovelifeHadPastPartners, (s, v) -> s.LovelifeHadPastPartners = v, p -> p.LovelifePastPartners == null ? null : p.LovelifePastPartners > 0),
            new YesNoSetting("LovelifeVirginity", "Is a virgin", s -> s.LovelifeVirginity, (s, v) -> s.LovelifeVirginity = v, p -> p.LovelifeVirginity),
            new YesNoSetting("LovelifeMarriage", "Is looking to marry", s -> s.LovelifeMarriage, (s, v) -> s.LovelifeMarriage = v, p -> p.LovelifeMarriage),
            new YesNoSetting("LovelifeReligionMatter", "Wants a partner of the same religion", s -> s.LovelifeReligionMatter, (s, v) -> s.LovelifeReligionMatter = v, p -> p.LovelifeReligionMatter),
            new YesNoSetting("LovelifeAlreadyHaveChildren", "Already has children", s -> s.LovelifeAlreadyHaveChildren, (s, v) -> s.LovelifeAlreadyHaveChildren = v, p -> p.LovelifeAlreadyHaveChildren),
            new YesNoSetting("LovelifeWillingToRelocate", "Is willing to relocate", s -> s.LovelifeWillingToRelocate, (s, v) -> s.LovelifeWillingToRelocate = v, p -> p.LovelifeWillingToRelocate));

    private static boolean isFilled(String text) {
        return text != null && !text.isBlank();
    }

    // The ticked answers for each choice; a choice with nothing ticked is "I don't mind".
    public transient Map<SearchChoice, Set<String>> choices;
    public Map<SearchChoice, Set<String>> getChoices() {
        if (choices != null) return choices;
        choices = new EnumMap<>(SearchChoice.class);
        if (ID == null) return choices; // not saved yet, so nothing stored
        for (var row : DefaultDBService.doQueryAll("SELECT Setting, Value FROM search_profile_choice WHERE SearchProfileID = ?", ID))
            addChoice(row.valueOf("Setting").toString(), row.valueOf("Value").toString());
        return choices;
    }
    public void setChoices(Map<SearchChoice, Set<String>> choices) {
        this.choices = choices;
    }
    // Ignores a setting or value the code no longer knows, so a renamed choice never breaks the page.
    void addChoice(String setting, String value) {
        if (choices == null) choices = new EnumMap<>(SearchChoice.class);
        Arrays.stream(SearchChoice.values()).filter(c -> c.name().equals(setting) && c.isOption(value)).findFirst()
                .ifPresent(c -> choices.computeIfAbsent(c, _ -> new HashSet<>()).add(value));
    }
    // Loads the choices of many searches in one query instead of one per search (NFR-10).
    public static void loadChoicesFor(List<Search_Profile> searches) {
        Map<Long, Search_Profile> byId = new HashMap<>();
        for (Search_Profile s : searches) {
            s.choices = new EnumMap<>(SearchChoice.class);
            byId.put(s.ID, s);
        }
        for (var row : DefaultDBService.doQueryAll("SELECT SearchProfileID, Setting, Value FROM search_profile_choice")) {
            Search_Profile s = byId.get(((Number) row.valueOf("SearchProfileID")).longValue());
            if (s != null) s.addChoice(row.valueOf("Setting").toString(), row.valueOf("Value").toString());
        }
    }
    public boolean isTicked(SearchChoice choice, String value) {
        return getChoices().getOrDefault(choice, Set.of()).contains(value);
    }

    public boolean isOwnedBy(long userId) {
        return UserID != null && UserID == userId;
    }
    // The member's own search profile, or 403: a missing one and someone else's answer the same, so ids can't be probed.
    public static Search_Profile requireOwnedBy(long searchId, long userId) {
        return checkOwner(getById(searchId), userId);
    }
    static Search_Profile checkOwner(Search_Profile search, long userId) {
        if (search == null || !search.isOwnedBy(userId)) throw new AccessDeniedException("This search belongs to another member.");
        return search;
    }

    // Copies the posted form into this search, every field included, so a field emptied on the form is cleared.
    // Returns the error messages, each saying how to fix it; empty when the form is valid.
    public List<String> applyForm(Map<String, List<String>> form) {
        List<String> errors = new ArrayList<>();
        String name = first(form, "Name");
        Name = name == null ? null : name.trim();
        if (Name == null || Name.isEmpty()) errors.add("Give this search a name, for example \"Weekend in Curepipe\".");
        else if (Name.length() > 60) errors.add("Keep the name to 60 characters or fewer.");

        MinAge = wholeNumber(form, "MinAge", "the lowest age", errors);
        MaxAge = wholeNumber(form, "MaxAge", "the highest age", errors);
        if (isOutside(MinAge, 18, 99) || isOutside(MaxAge, 18, 99)) errors.add("Ages go from 18 to 99.");
        else if (MinAge != null && MaxAge != null && MinAge > MaxAge) errors.add("The lowest age must be at or below the highest age.");

        MinHeightCm = wholeNumber(form, "MinHeightCm", "the lowest height", errors);
        MaxHeightCm = wholeNumber(form, "MaxHeightCm", "the highest height", errors);
        if (isOutside(MinHeightCm, 100, 250) || isOutside(MaxHeightCm, 100, 250)) errors.add("Heights go from 100 to 250 cm.");
        else if (MinHeightCm != null && MaxHeightCm != null && MinHeightCm > MaxHeightCm) errors.add("The lowest height must be at or below the highest height.");

        LocationRadiusKM = wholeNumber(form, "LocationRadiusKM", "the distance", errors);
        if (LocationRadiusKM != null && LocationRadiusKM < 1) errors.add("The distance must be at least 1 km, or left empty.");

        for (YesNoSetting setting : YES_NO_SETTINGS) {
            String value = first(form, setting.field());
            setting.setWanted().accept(this, "true".equals(value) ? Boolean.TRUE : "false".equals(value) ? Boolean.FALSE : null);
        }

        choices = new EnumMap<>(SearchChoice.class);
        for (SearchChoice choice : SearchChoice.values())
            for (String value : form.getOrDefault(choice.name(), List.of())) addChoice(choice.name(), value);
        return errors;
    }
    private static String first(Map<String, List<String>> form, String key) {
        List<String> values = form.get(key);
        return values == null || values.isEmpty() ? null : values.getFirst();
    }
    private static Integer wholeNumber(Map<String, List<String>> form, String key, String what, List<String> errors) {
        String value = first(form, key);
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            errors.add("Write " + what + " as a whole number, for example 30.");
            return null;
        }
    }
    private static boolean isOutside(Integer value, int min, int max) {
        return value != null && (value < min || value > max);
    }

    // Replaces the stored choices with the ones on this search. Each statement runs on its own: SolarFramework offers no
    // transaction to join yet (TransactionalUsageTest). With the class, so the cached choice rows are cleared.
    public void saveChoices() {
        DefaultDBService.doUpdate(Search_Profile.class, "DELETE FROM search_profile_choice WHERE SearchProfileID = ?", ID);
        for (Map.Entry<SearchChoice, Set<String>> choice : getChoices().entrySet())
            for (String value : choice.getValue())
                DefaultDBService.doUpdate(Search_Profile.class, "INSERT INTO search_profile_choice (SearchProfileID, Setting, Value) VALUES (?,?,?)", ID, choice.getKey().name(), value);
    }
    // With its choices and the notifications it raised, whose text names a search that no longer exists.
    public void deleteWithChoices() {
        DefaultDBService.doUpdate(Search_Profile.class, "DELETE FROM search_profile_choice WHERE SearchProfileID = ?", ID);
        DefaultDBService.doUpdate(Match_Notification.class, "DELETE FROM match_notification WHERE SearchProfileID = ?", ID);
        Delete();
    }

    // How well a person fits this search, from 0 to 100: the settings they match divided by the settings set,
    // rounded down so that 80% always means at least 80%. The distance is measured from the searcher's own location.
    public int matchPercent(Profile candidate, Profile searcher, LocalDate today) {
        int total = 0, matched = 0;
        for (Map.Entry<SearchChoice, Set<String>> choice : getChoices().entrySet()) {
            if (choice.getValue().isEmpty()) continue;
            total++;
            if (!Collections.disjoint(choice.getValue(), choice.getKey().answersOf(candidate))) matched++;
        }
        if (MinAge != null || MaxAge != null) {
            total++;
            if (candidate.DateOfBirth != null && isWithin(Period.between(candidate.DateOfBirth, today).getYears(), MinAge, MaxAge)) matched++;
        }
        if (MinHeightCm != null || MaxHeightCm != null) {
            total++;
            if (candidate.PhysicalHeightCm != null && isWithin(candidate.PhysicalHeightCm, MinHeightCm, MaxHeightCm)) matched++;
        }
        if (LocationRadiusKM != null) {
            total++;
            if (hasLocation(candidate) && hasLocation(searcher) && distanceKm(searcher, candidate) <= LocationRadiusKM) matched++;
        }
        for (YesNoSetting setting : YES_NO_SETTINGS) {
            Boolean wanted = setting.wanted().apply(this);
            if (wanted == null) continue;
            total++;
            if (wanted.equals(setting.answerOf().apply(candidate))) matched++;
        }
        return total == 0 ? 100 : matched * 100 / total;
    }

    private static boolean isWithin(int value, Integer min, Integer max) {
        return (min == null || value >= min) && (max == null || value <= max);
    }
    private static boolean hasLocation(Profile p) {
        return p.Latitude != null && p.Longitude != null;
    }
    // Great-circle distance (haversine), Earth radius 6371 km.
    static double distanceKm(Profile a, Profile b) {
        double dLat = Math.toRadians(b.Latitude - a.Latitude), dLon = Math.toRadians(b.Longitude - a.Longitude);
        double h = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(a.Latitude)) * Math.cos(Math.toRadians(b.Latitude)) * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * 6371 * Math.asin(Math.sqrt(h));
    }

    public static Search_Profile getById(long id) {
        return SolarDBManager.getById(Search_Profile.class, id).orElse(null);
    }
    public static List<Search_Profile> getByUser(Account_User user) {
        return SolarDBManager.getAllWhere(Search_Profile.class, "UserID = ? ORDER BY Name", user.getID());
    }
}
