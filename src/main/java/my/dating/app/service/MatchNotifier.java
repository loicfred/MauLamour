package my.dating.app.service;

import my.dating.app.object.Match_Notification;
import my.dating.app.object.Profile;
import my.dating.app.object.Search_Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solarframework.auth.obj.Account_User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;
import static org.solarframework.mail.spring.MailRegistry.DefaultMailService;

// FR-6, FR-7: when a finished profile is saved, tells the owner of every other search it matches at 80% or more,
// on the site and by email, once per person and search.
@Service
public class MatchNotifier {
    static final int THRESHOLD = 80;
    private static final Logger log = LoggerFactory.getLogger(MatchNotifier.class);

    public record ProfileSaved(long profileId) {}
    public record NewMatch(Search_Profile search, int percent) {}
    public record MatchPush(String text, String link) {} // only what the bell needs, never the entity and its ids
    public interface EmailSender {
        void send(String to, String subject, String body);
    }

    private final Predicate<Match_Notification> writer;   // false: already notified
    private final BiConsumer<String, MatchPush> push;
    private final EmailSender mail;
    private final String publicUrl;

    @Autowired
    public MatchNotifier(SimpMessagingTemplate messaging, @Value("${maulamour.public-url:http://localhost:8080}") String publicUrl) {
        this(Match_Notification::writeIfNew,
                (email, n) -> messaging.convertAndSendToUser(email, "/queue/match", n), // STOMP users are known by their email
                (to, subject, body) -> DefaultMailService.send(to, subject, body),
                publicUrl);
    }
    MatchNotifier(Predicate<Match_Notification> writer, BiConsumer<String, MatchPush> push, EmailSender mail, String publicUrl) {
        this.writer = writer;
        this.push = push;
        this.mail = mail;
        this.publicUrl = publicUrl;
    }

    // After the save is committed, on another thread, so the member's save never waits for this or for the mail server.
    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void onProfileSaved(ProfileSaved event) {
        Profile.Profile_View person = Profile.Profile_View.getView(event.profileId());
        if (person == null || !person.isFinished()) return;

        List<Search_Profile> searches = SolarDBManager.getAllWhere(Search_Profile.class, "NOT UserID = ?", person.ID);
        if (searches.isEmpty()) return;
        Search_Profile.loadChoicesFor(searches);
        Map<Long, Profile> searcherProfiles = SolarDBManager.getAllWhere(Profile.class, "ID IN (SELECT UserID FROM search_profile)")
                .stream().collect(Collectors.toMap(p -> p.ID, Function.identity()));
        Map<Long, String> ownerEmails = SolarDBManager.getAllWhere(Account_User.class, "ID IN (SELECT UserID FROM search_profile)")
                .stream().collect(Collectors.toMap(Account_User::getID, Account_User::getEmail, (a, _) -> a));

        for (NewMatch match : findNewMatches(person, searches, searcherProfiles, Match_Notification.notifiedSearchIds(person.ID), LocalDate.now())) {
            String email = ownerEmails.get(match.search().UserID);
            if (email != null) deliver(match, person.ID, person.Username, match.search().UserID, email);
        }
    }

    // The searches, other than the person's own and those already notified, that the person matches at THRESHOLD or more.
    static List<NewMatch> findNewMatches(Profile person, List<Search_Profile> searches, Map<Long, Profile> searcherProfiles,
                                         Set<Long> alreadyNotifiedSearchIds, LocalDate today) {
        List<NewMatch> found = new ArrayList<>();
        for (Search_Profile search : searches) {
            if (search.isOwnedBy(person.ID) || alreadyNotifiedSearchIds.contains(search.ID)) continue;
            Profile searcher = searcherProfiles.get(search.UserID);
            if (searcher == null) continue;
            int percent = search.matchPercent(person, searcher, today);
            if (percent >= THRESHOLD) found.add(new NewMatch(search, percent));
        }
        return found;
    }

    // Stores the notification, then pushes it and mails it. A failed push or email never undoes the stored notification.
    void deliver(NewMatch match, long matchedUserId, String matchedUsername, long ownerId, String ownerEmail) {
        Match_Notification n = Match_Notification.createFor(ownerId, match.search(), matchedUserId, matchedUsername, match.percent());
        if (!writer.test(n)) return;
        try {
            push.accept(ownerEmail, new MatchPush(n.Text, n.Link));
        } catch (RuntimeException e) {
            log.warn("New-match notification {} not pushed: {}", n.ID, e.getClass().getSimpleName()); // the bell still shows it on the next page
        }
        try {
            mail.send(ownerEmail, "Someone new matches your search " + match.search().Name, n.Text
                    + "\n\nSee their profile: " + publicUrl + n.Link
                    + "\n\nYou are getting this email because one of your searches on MauLamour matched someone new.");
        } catch (RuntimeException e) {
            log.warn("New-match email for notification {} not sent: {}", n.ID, e.getClass().getSimpleName()); // no address or text in the log (NFR-7)
        }
    }
}
