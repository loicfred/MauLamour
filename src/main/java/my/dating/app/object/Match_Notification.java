package my.dating.app.object;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.solarframework.db.spring.DatabaseObject;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.solarframework.db.spring.DatabaseRegistry.DefaultDBService;
import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// FR-6: "someone new matches your search". Kept apart from SolarFramework's account_notification, which has no link
// and no unique pair: here (SearchProfileID, MatchedUserID) is unique (db/V4), so nobody is told twice about one person.
@Entity
@Table(name = "match_notification")
public class Match_Notification extends DatabaseObject.ID_OBJ<Long, Match_Notification> {
    public Long UserID;          // who is told: the owner of the search
    public Long SearchProfileID;
    public Long MatchedUserID;
    public Integer Percent;
    public String Text;
    public String Link;          // the matched member's profile, relative to the site
    public Instant CreatedAt;
    public Instant ReadAt;       // null while unread

    public Match_Notification() {}

    public static Match_Notification createFor(long ownerId, Search_Profile search, long matchedUserId, String matchedUsername, int percent) {
        Match_Notification n = new Match_Notification();
        n.ID = nextId();
        n.UserID = ownerId;
        n.SearchProfileID = search.ID;
        n.MatchedUserID = matchedUserId;
        n.Percent = percent;
        n.Text = textFor(search.Name, percent);
        n.Link = "/profile/" + matchedUsername;
        n.CreatedAt = Instant.now();
        return n;
    }
    public static String textFor(String searchName, int percent) {
        return "Someone new matches your search " + searchName + " at " + percent + "%";
    }

    public String getText() { return Text; }
    public String getLink() { return Link; }
    public Instant getCreatedAt() { return CreatedAt; }
    public boolean isUnread() { return ReadAt == null; }
    public String getCreatedAtShown() {
        return CreatedAt == null ? "" : SHOWN.format(CreatedAt.atZone(ZoneId.systemDefault()));
    }
    private static final DateTimeFormatter SHOWN = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.UK);

    // Writes it unless this person was already notified for this search; false then, so nothing is pushed or mailed.
    public boolean writeIfNew() {
        try {
            Write();
            return true;
        } catch (RuntimeException e) {
            if (isDuplicateKey(e)) return false; // another save of the same profile got there first
            throw e;
        }
    }
    private static boolean isDuplicateKey(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause())
            if (t instanceof SQLIntegrityConstraintViolationException || (t.getMessage() != null && t.getMessage().contains("Duplicate entry"))) return true;
        return false;
    }

    public static long countUnread(long userId) {
        return SolarDBManager.Count(Match_Notification.class, "UserID = ? AND ReadAt IS NULL", userId);
    }
    public static List<Match_Notification> ofUser(long userId) {
        return SolarDBManager.getAllWhere(Match_Notification.class, "UserID = ? ORDER BY CreatedAt DESC LIMIT 100", userId);
    }
    public static void markAllRead(long userId) {
        // With the class, so the cached count and list are cleared and the bell drops to zero on the next page.
        DefaultDBService.doUpdate(Match_Notification.class, "UPDATE match_notification SET ReadAt = ? WHERE UserID = ? AND ReadAt IS NULL", Instant.now(), userId);
    }
    // The searches whose owners were already told about this member.
    public static Set<Long> notifiedSearchIds(long matchedUserId) {
        Set<Long> ids = new HashSet<>();
        for (var row : DefaultDBService.doQueryAll("SELECT SearchProfileID FROM match_notification WHERE MatchedUserID = ?", matchedUserId))
            ids.add(((Number) row.valueOf("SearchProfileID")).longValue());
        return ids;
    }
}
