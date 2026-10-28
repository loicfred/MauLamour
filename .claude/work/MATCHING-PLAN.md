# Profile questions, search profiles, match % and notifications: implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Members answer more profile questions, describe who they are looking for in several search profiles, see other members ranked by match %, and are told on the site and by email when someone new matches a search profile at 80% or more.

**Architecture:** Entities stay SolarFramework `DatabaseObject.ID_OBJ` classes with public fields named after their columns. The match % is worked out in one Java method, `Search_Profile.matchPercent`. The matches page and the notifier both call it. The candidates are loaded in bulk. Views, non-entity tables and unique keys go in numbered SQL scripts in `db/`, and Hibernate (`hbm2ddl update`) adds the entity tables and columns.

**Tech Stack:** Java 25, Spring Boot 4.1 via `solar-framework` 1.0, Thymeleaf, Bootstrap 5.3, STOMP.js 7 over the existing `/ws-chat` endpoint, `spring-boot-starter-mail` (already a dependency), JUnit 5 (`spring-boot-starter-test`, already a dependency), MariaDB.

**Spec:** `.claude/work/MATCHING-DESIGN.md` (approved by the owner on 28 Oct 2026). Read it first; this plan argues from it.

## Global Constraints
- **No new dependency** (`CLAUDE.md`, Ask first). Mail goes through the `JavaMailSender` that Boot builds from the `spring.mail.*` properties already set.
- **Schema changes are only the ones in the design**, section 5.
- **Edit files with the Edit and Write tools only**; `hooks/no-shell-edits.sh` refuses `sed -i` and shell redirects.
- **Build and test:** in PowerShell, `$env:JAVA_HOME='C:\Program Files\Java\jdk-25'; .\mvnw.cmd -o -q test -Dtest=<TestClass>`. `MyDatingSiteApplicationTests.contextLoads` needs a running MariaDB server, so run the named test classes. No MariaDB service was running on 28 Oct 2026.
- **No commits** unless the owner asks (`CLAUDE.md`). The working tree also holds the uncommitted SolarFramework move from 27 Oct.
- Naming follows `NAMING.md`. Profile columns keep the existing group prefixes (`Lifestyle…`, `Lovelife…`, `Message…`), and the new physical ones use `Physical…`. Enum display text is UK English.
- On screen, no feature codes. Every error message says how to fix the problem (NFR-22).
- On the board, at most 2 stories are In progress (`USER-STORIES.md`). Update the session's `work/DONE/` entry after each task.
- Notification text, exactly: `Someone new matches your search <search name> at <percent>%`. Email subject: `Someone new matches your search <search name>`.
- Match threshold `80`. Matches page size `20`.

## Review Focus
1. **Another member's search profile id** in `/profile/search/{id}`, `/post/profile/search/{id}/delete` or `/matches?searchId=`: the page answers 403 and shows none of its data. *(Task 5, `SearchProfileAccessTest`; Task 6 reuses `requireOwnedBy`.)*
2. **Form fields sent empty or left out** (a select left on "Choose an option", a checkbox not ticked): the profile or search profile saves, and the field means blank or "I don't mind". The setters don't fail on `null` or `""`. *(Task 3, `ProfileSettersTest`; Task 5, `SearchProfileFormTest`.)*
3. **A searcher with no location** whose search profile has a radius: the distance setting counts as not matched for everyone, and the page still loads. *(Task 4, `SearchProfileMatchTest.searcherWithoutLocationNeverMatchesDistance`.)*
4. **Mail not configured or the SMTP server down:** the profile save still succeeds and the site notification is still stored and pushed. *(Task 7, `MatchNotifierTest.mailFailureKeepsSiteNotification`.)*
5. **`page` below 1 or past the end** on the matches page: it shows page 1 or an empty last page with a Previous link, never an error. *(Task 6, `MatchRankingTest.pageOutOfRange`.)*

---

### Task 1: Put the work on the board

**Files:**
- Modify: `.claude/work/REQUIREMENTS.md` (Features table: replace the two template rows)
- Modify: `.claude/work/USER-STORIES.md` (Stories table and "Checks for each story": replace the template rows)

- [ ] **Step 1: Add the features.** Write FR-1 to FR-7. The User column is *Member*, the Use case column is `—`, and all are M:
  FR-1 answer the extra profile questions (physical, background, living situation) · FR-2 create a search profile · FR-3 edit a search profile · FR-4 delete a search profile · FR-5 see members ranked by match % for one of their search profiles · FR-6 get a site notification when someone new matches a search profile at 80% or more · FR-7 get the same notification by email.
- [ ] **Step 2: Add the stories.** Write US-1 to US-7, one for each FR, in the "As a member, I must be able to … so that …" form, with Priority High and Status Backlog. Under each, add its checks, taken from the design (for example, US-5: "When two members match 7 of 9 settings, the card shows 77%"). Then move US-1 to **In progress**.

### Task 2: Database scripts and the view classes' table names

**Files:**
- Create: `db/V1__choice_tables.sql`, `db/V2__views.sql`, `db/README.md` (how to run the scripts, in order, on an empty database after the app has started once)
- Modify: `src/main/java/my/dating/app/object/Profile.java` (the `Profile_View` and `Profile_Edit` classes), `object/chatting/Chat.java` (`Chatlist_Item`), `object/msg/Message.java` (`Message_View`), `object/msg/Message_Reaction.java` (`Message_Reaction_View`)
- Modify: `src/main/resources/templates/profile/edit.html:38`

**Bug this fixes:** `DatabaseObject.getTableName` walks up to the parent's `@Table`, so `Profile_View` reads the `profile` table, not `profile_view`, and the same goes for the other views. Fix it by putting `@Table(name = "...")` (without `@Entity`) on each view class: `profile_view`, `chatlist_item`, `message_view`, `message_reaction_view`.

- [ ] **Step 1: `V1__choice_tables.sql`.** Create `profile_to_interest (UserID BIGINT, Value VARCHAR(40), PRIMARY KEY (UserID, Value))`, `profile_to_language` (the same shape) and `search_profile_choice (SearchProfileID BIGINT, Setting VARCHAR(40), Value VARCHAR(40), PRIMARY KEY (SearchProfileID, Setting, Value))`, all `IF NOT EXISTS`.
- [ ] **Step 2: `V2__views.sql`**, each `CREATE OR REPLACE VIEW`:
  - `profile_view`: `profile.*` joined to `account_user` on `ID`, plus `Username`, `Email`, `Age` (`TIMESTAMPDIFF(YEAR, DateOfBirth, CURDATE())`) and `JoinedAt` (the account's creation time; check the column name in `account_user` with `DESCRIBE`).
  - `chatlist_item`: one row per chat *per member*, with `chat.*`, `PartnerID` (the member the row is for), `PartnerUsername`, `PartnerName` (`profile.Name`), `LatestMessageID`, `SenderID`, `Content` (the newest message, the highest `ID`) and `Unreads` (that member's messages in the chat with `isRead = 0`). The code asks for the row where `PartnerID` is *not* the caller.
  - `message_view`: `message.*` plus `AttachmentCount` and `ReactionCount`.
  - `message_reaction_view`: one row for each (`MessageID`, `Emoji`), with `MIN(ID) AS ID`, `MIN(UserID) AS UserID` and `COUNT(*) AS Count`.
- [ ] **Step 3: Drop the procedure calls.** `Profile_Edit` and `CALL FetchUserdata` go. `ProfileController.editProfile` loads `Profile_View.getView(username)`, and `edit.html:38` checks `${!profile.isFinished()}` (Task 3 defines `isFinished`). `Profile_View.search` and `CALL MatchUsers` go in Task 6.
- [ ] **Step 4: Compile.** Run `.\mvnw.cmd -o -q compile`. Expected: BUILD SUCCESS.
- [ ] **Step 5: Run the scripts, if MariaDB is up.** Start the app once on an empty `maulamour` database, then run `mariadb maulamour < db/V1__choice_tables.sql` and the same for `V2`. Expected: no errors. Then `SELECT * FROM profile_view LIMIT 1` works. If no server is running, write "not run" in the worklog and in `TASKLIST.md`.

### Task 3: The new profile questions (FR-1, US-1)

**Files:**
- Create in `src/main/java/my/dating/app/object/enums/`: `BodyType`, `SkinColour`, `EyeColour`, `HairColour`, `EthnicBackground`, `EducationLevel`, `RelationshipStatus`, `LivingSituation`. Each has a `displayName` with a getter, like `BadHabit`. The values and wording are the design's table, section 1.
- Modify: `object/Profile.java`, `templates/profile/edit.html`, `templates/profile/view.html`
- Test: `src/test/java/my/dating/app/object/ProfileSettersTest.java`

**Interfaces:**
- Produces new `Profile` fields, all nullable: `Integer PhysicalHeightCm`, `String PhysicalBodyType`, `String PhysicalSkinColour`, `String PhysicalEyeColour`, `String PhysicalHairColour`, `String PhysicalEthnicBackground`, `Boolean PhysicalHasTattoos`, `String LifestyleEducationLevel`, `String LifestyleLivingSituation`, `String LovelifeRelationshipStatus`. Each has a getter and setter in the existing style.
- Produces `public boolean isFinished()`: true when `Name` is not blank and `Gender`, `DateOfBirth`, `Latitude` and `Longitude` are all set.

- [ ] **Step 1: Write the failing tests** in `ProfileSettersTest`:
  - `enumSetterIgnoresBlank`: `setPhysicalSkinColour("")` and `setPhysicalSkinColour(null)` leave the field null. `setPhysicalSkinColour("OLIVE")` stores `"OLIVE"`.
  - `existingSettersIgnoreNull`: `setGender(null)`, `setLifestyleDiet(null)` and `setWhoAmI(null)` throw nothing.
  - `heightOutsideRangeIsIgnored`: `setPhysicalHeightCm(99)` and `setPhysicalHeightCm(251)` leave it null. `setPhysicalHeightCm(180)` stores 180.
  - `finishedNeedsNameGenderBirthAndLocation`: a profile with all five set gives true, and the same profile with `Latitude = null` gives false.
- [ ] **Step 2: Run them to see them fail.** Run `-Dtest=ProfileSettersTest`. Expected: compilation fails on the missing setters.
- [ ] **Step 3: Implement.** Add the fields and `isFinished()`. Change every `if (!val.isEmpty())` in `Profile` to `if (val != null && !val.isEmpty())`. `setLifestylePets` takes a `Boolean`.
- [ ] **Step 4: Run the tests.** Expected: PASS.
- [ ] **Step 5: The form and the profile page.** In `edit.html`, add a "Physical" accordion section (height as `<input type="number" min="100" max="250">` with the label "How tall are you? (cm)", then body type, skin colour, eye colour, hair colour, ethnic background and tattoos). Add highest education and living situation to Lifestyle, and relationship status to Love life, each a `<select>` with "Choose an option" first, like the existing ones. Change "Do you have any pets?" to a checkbox. Show every new answer on `view.html` in the same style as the existing answers, using the enum's `displayName`.
- [ ] **Step 6: Compile and run the tests.** Run `.\mvnw.cmd -o -q test -Dtest=ProfileSettersTest`. Expected: PASS. Move US-1 to **To verify** (it needs the running app).

### Task 4: The search profile model and the match % (part of FR-2 and FR-5)

**Files:**
- Create: `src/main/java/my/dating/app/object/enums/SearchChoice.java`
- Modify: `src/main/java/my/dating/app/object/Search_Profile.java`
- Create: `db/V3__drop_old_search_columns.sql`: `ALTER TABLE search_profile DROP COLUMN IF EXISTS` for `Gender`, `LifestyleReligion`, `LifestyleDiet`, `LifestyleWouldCook`, `LifestyleWouldChore`, `LifestyleSmoking`, `LifestyleDrinking`, `LifestyleExercise`, `MessageReplySpeed`, `LovelifeWantKids` and `LovelifeMustBeChildless`. They become choices, and Hibernate never drops a column itself.
- Test: `src/test/java/my/dating/app/object/SearchProfileMatchTest.java`

**Interfaces:**
- Produces `enum SearchChoice`, one value for each "any of a list" or "shares at least one" setting in the design (section 2): `GENDER, PERSONALITY, RELIGION, DIET, WOULD_COOK, WOULD_CHORE, SMOKING, DRINKING, EXERCISE, REPLY_SPEED, WANT_KIDS, BODY_TYPE, SKIN_COLOUR, EYE_COLOUR, HAIR_COLOUR, ETHNIC_BACKGROUND, EDUCATION_LEVEL, RELATIONSHIP_STATUS, LIVING_SITUATION, INTERESTS, LANGUAGES`. Each holds `Function<Profile, Set<String>> answersOf`: the person's answer as a set, with one element for single answers and enum names for interests and languages. A blank answer gives an empty set. `public Set<String> answersOf(Profile p)`.
- Produces `Search_Profile` fields, which replace the old single-value ones: `Long UserID`, `String Name`, `Integer MinAge`, `Integer MaxAge`, `Integer MinHeightCm`, `Integer MaxHeightCm`, `Integer LocationRadiusKM`, and one nullable `Boolean` for each yes/no setting: `LifestylePets`, `PhysicalHasTattoos`, `LifestyleAllergies`, `LifestyleDisability`, `MessageLeaveOnRead`, `LovelifeHadPastPartners`, `LovelifeVirginity`, `LovelifeMarriage`, `LovelifeReligionMatter`, `LovelifeAlreadyHaveChildren` (replaces `LovelifeMustBeChildless`), `LovelifeWillingToRelocate`.
- Produces `transient Map<SearchChoice, Set<String>> choices`, with `getChoices()` (loaded from `search_profile_choice` once) and `setChoices(Map<SearchChoice, Set<String>>)` (kept in memory; `saveChoices()` writes it).
- Produces `public int matchPercent(Profile candidate, Profile searcher, LocalDate today)`.
- Produces `public boolean isOwnedBy(long userId)`.
- Removes `kmToLatitude` and `kmToLongitude`.

**The rule** (from the design, section 3). Each setting that is not "I don't mind" adds 1 to the total, and adds 1 to the matched count when:
- choice: `answersOf(candidate)` shares at least one value with the ticked set (an empty ticked set means "I don't mind");
- age: the candidate's age on `today` is within `[MinAge, MaxAge]`, either end open when null (the setting counts if either end is set), and is not matched when `DateOfBirth` is null;
- height: the same, with `PhysicalHeightCm`;
- distance: both have a location, and the great-circle distance is at most `LocationRadiusKM` (haversine, Earth radius 6371 km);
- yes/no: equal to the candidate's answer. `LifestyleAllergies` and `LifestyleDisability` compare with "text is not blank", and `LovelifeHadPastPartners` compares with `LovelifePastPartners > 0`. A null candidate answer is not matched.

The result is `total == 0 ? 100 : matched * 100 / total` (whole-number division rounds down).

- [ ] **Step 1: Write the failing tests** in `SearchProfileMatchTest`. Build the profiles in memory and set `interests` and `languages` directly, so nothing touches the database:
  - `noSettingsIsHundred`: an empty search profile gives 100.
  - `sevenOfNineIsSeventySeven`: nine settings set, seven matched, gives 77.
  - `fourOfFiveIsEighty`: gives 80.
  - `blankAnswerIsNotMatched`: `SKIN_COLOUR={"OLIVE"}` against a candidate with no skin colour gives 0.
  - `choiceMatchesAnyTicked`: `RELIGION={"CHRISTIAN","HINDU"}` against a Hindu candidate gives 100.
  - `sharedInterestCountsOnce`: `INTERESTS={"MUSIC","TRAVEL","SPORT"}` against a candidate with `{MUSIC}` gives 100 (1 of 1).
  - `genderMismatchOnlyLowersScore`: `GENDER={"FEMALE"}` and `MinAge=20` against a 25-year-old man gives 50.
  - `openEndedAgeRange`: `MinAge=30`, `MaxAge=null`, against a 45-year-old gives 100.
  - `distanceWithinRadius`: `LocationRadiusKM=50`, with Port Louis (-20.1609, 57.5012) as the searcher and Curepipe (-20.3163, 57.5259) as the candidate, about 17 km apart, gives 100. With `LocationRadiusKM=10` it gives 0.
  - `searcherWithoutLocationNeverMatchesDistance`: a radius set and the searcher's `Latitude = null` gives 0.
  - `allergiesYesMeansTextFilled`: `LifestyleAllergies=true` against a candidate with `"Peanuts"` gives 100, and against `""` gives 0.
- [ ] **Step 2: Run them to see them fail.** Run `-Dtest=SearchProfileMatchTest`. Expected: compilation fails.
- [ ] **Step 3: Implement `SearchChoice`, the fields and `matchPercent`** in the two files.
- [ ] **Step 4: Run the tests.** Expected: all PASS.

### Task 5: Search profile pages: create, edit, delete (FR-2, FR-3, FR-4, US-2 to US-4)

**Files:**
- Create: `src/main/java/my/dating/app/controller/SearchProfileController.java`
- Modify: `src/main/java/my/dating/app/object/Search_Profile.java` (adds `requireOwnedBy`, `saveChoices`, `deleteWithChoices`, `applyForm`)
- Rewrite: `src/main/resources/templates/profile/search.html`
- Test: `src/test/java/my/dating/app/object/SearchProfileAccessTest.java`, `src/test/java/my/dating/app/object/SearchProfileFormTest.java`

**Interfaces:**
- Consumes: `SearchChoice`, the `Search_Profile` fields, `isOwnedBy` (Task 4).
- Produces `public static Search_Profile requireOwnedBy(long searchId, long userId)`. It throws `org.springframework.security.access.AccessDeniedException` when the profile is missing or someone else's, so the answer is 403 either way and nobody can probe which ids exist.
- Produces `public List<String> applyForm(Map<String, String> form)`. It copies the form into the fields and `choices`, and returns the error messages (empty when valid). Each choice is posted as one comma-joined parameter named after the `SearchChoice` (the existing tag-picker's hidden input). A yes/no is posted as `"true"`, `"false"` or `""` (I don't mind).
- Routes: `GET /profile/search` (empty form), `GET /profile/search/{id}`, `POST /post/profile/search` (hidden `ID`: empty to create, set to update), `POST /post/profile/search/{id}/delete`. After a save it redirects to `/matches?searchId={id}`, and after a delete to `/matches`.

- [ ] **Step 1: Write the failing tests.**
  - `SearchProfileAccessTest.otherMembersProfileIsRefused`: a profile with `UserID=1`, with `isOwnedBy(2)` false and `isOwnedBy(1)` true. Test the lookup part of `requireOwnedBy` through a package-private `static void checkOwner(Search_Profile sp, long userId)`: it throws `AccessDeniedException` for `null` and for another owner.
  - `SearchProfileFormTest`:
    - `nameRequired`: `Name=""` gives `["Give this search a name, for example \"Weekend in Curepipe\"."]`.
    - `nameTooLong`: 61 characters gives `["Keep the name to 60 characters or fewer."]`.
    - `minAboveMax`: `MinAge=40`, `MaxAge=30` gives `["The lowest age must be at or below the highest age."]`.
    - `ageOutOfRange`: `MinAge=17` gives `["Ages go from 18 to 99."]`.
    - `radiusTooSmall`: `LocationRadiusKM=0` gives `["The distance must be at least 1 km, or left empty."]`.
    - `blankFieldsMeanDontMind`: a form with `Name="A"` and every other key `""` or missing gives no errors, no choices and every yes/no null.
    - `unknownChoiceValueIgnored`: `SKIN_COLOUR="OLIVE,NOPE"` keeps `{"OLIVE"}`.
- [ ] **Step 2: Run them to see them fail.**
- [ ] **Step 3: Implement** `checkOwner`, `requireOwnedBy`, `applyForm`, `saveChoices` (delete the rows, then insert one per value) and `deleteWithChoices`. `saveChoices` and `deleteWithChoices` run in one `@Transactional` controller call (NFR-14). Then write the controller. Every action resolves the member with `Account_User.getByAuthentication(principal)` and calls `requireOwnedBy` for any `{id}`. A new profile gets `ID = nextId()` and `UserID` = the member. When there are errors, show the form again with the member's input kept and the messages above the save button.
- [ ] **Step 4: Rewrite `search.html`.** Keep the header and footer. Add the name field, and a tag-picker for each `SearchChoice` grouped like the profile form (Essentials, Physical, Lifestyle, Messaging, Love life). Age, height and distance each get a min and max (or radius) number input. Each yes/no gets a three-way `<select>` (I don't mind, Yes, No). Add a Delete button with `confirm("Delete this search? This cannot be undone.")`. Drop the map: the distance is measured from the member's own profile location, and the page says so under the radius field.
- [ ] **Step 5: Run the tests.** Run `-Dtest=SearchProfileAccessTest,SearchProfileFormTest`. Expected: PASS. Move US-2 to US-4 on the board as the work goes, keeping at most 2 In progress.

### Task 6: The matches page ranked by % (FR-5, US-5)

**Files:**
- Create: `src/main/java/my/dating/app/service/MatchRanking.java`
- Modify: `src/main/java/my/dating/app/controller/MatchController.java` (gets `/matches`), `MainController.java` (loses `/matches`), `object/Profile.java` (`Profile_View` gets `Instant JoinedAt`; `search()` goes), `templates/matches.html`
- Test: `src/test/java/my/dating/app/service/MatchRankingTest.java`

**Interfaces:**
- Consumes: `Search_Profile.matchPercent`, `requireOwnedBy`.
- Produces `public record Match(Profile.Profile_View profile, int percent)`, nested in `MatchRanking`.
- Produces `public static List<Match> rank(Search_Profile search, Profile searcher, List<Profile.Profile_View> candidates, LocalDate today)`. It drops candidates that are not finished and the searcher themselves, then sorts by percent (highest first) and then by `JoinedAt` (newest first).
- Produces `public static List<Match> page(List<Match> ranked, int page)`, with a page size of 20. A `page` below 1 is treated as 1.
- Produces `public List<Profile.Profile_View> loadCandidates()`. It makes three queries: every `profile_view` row, then every `profile_to_interest` row, then every `profile_to_language` row. The last two are grouped by `UserID` and set on each profile's `interests` and `languages`. No query runs once per member (NFR-10).

- [ ] **Step 1: Write the failing tests** in `MatchRankingTest`:
  - `highestFirst`: candidates scoring 50, 100 and 75 come back as 100, 75, 50.
  - `tieNewestFirst`: two at 100, and the one with the later `JoinedAt` comes first.
  - `selfAndUnfinishedExcluded`: the searcher's own row and a profile without a location are not in the result.
  - `pageOutOfRange`: with 25 matches, page 0 gives the first 20, page 2 gives 5, and page 3 gives an empty list.
- [ ] **Step 2: Run them to see them fail.**
- [ ] **Step 3: Implement `MatchRanking`** (a `@Service` with the two static methods and `loadCandidates`), then `MatchController.showMatchPage(searchId, page)`. Without a `searchId` it shows the picker and nothing else. With one, it calls `requireOwnedBy`, then `rank`, then `page`. It puts `matches`, `page`, `hasNext`, `SearchID` and `searchProfiles` on the model.
- [ ] **Step 4: Update `matches.html`.** Drop the padding to 10 empty cards and the fixed `'10%'`. Each card shows the real `percent + '%'`, the photo, name, age and gender, and links to `/profile/{username}`. Add Previous and Next links that keep `searchId`, an "Edit this search" link to `/profile/search/{id}`, and two empty states:
  - no search profiles: "You have no searches yet. Create one to see who matches you." with a button to `/profile/search`;
  - no one finished yet: "Nobody matches this search yet. Try ticking fewer choices."
- [ ] **Step 5: Run the tests.** Expected: PASS.

### Task 7: New-match notifications on the site and by email (FR-6, FR-7, US-6, US-7)

**Files:**
- Create: `object/Match_Notification.java` (`@Entity @Table(name = "match_notification")`), `service/MatchNotifier.java`, `controller/NotificationController.java`, `templates/notifications.html`, `static/notifications.js`, `db/V4__match_notification_unique.sql`
- Modify: `MauLabourApplication.java` (`@EnableAsync`), `controller/ProfileController.java` (publishes the event after a save), `controller/ViewAdvice.java` (unread count), `templates/fragments/header.html` (bell, CSRF meta tags, STOMP and `notifications.js` scripts), `src/main/resources/application.properties` (`app.base-url=${APP_BASE_URL:http://localhost:8080}`)
- Test: `src/test/java/my/dating/app/service/MatchNotifierTest.java`

The table name `match_notification` keeps it apart from SolarFramework's own `account_notification` (`AuthenticationAPI`). That table has no link and no unique pair, so it can't stop repeats.

**Interfaces:**
- Consumes: `matchPercent` (Task 4), `isFinished` (Task 3).
- Produces `Match_Notification` fields: `Long ID`, `long UserID` (who is told), `long SearchProfileID`, `long MatchedUserID`, `int Percent`, `String Text`, `String Link` (`/profile/{username}`), `Instant CreatedAt`, `Instant ReadAt` (null means unread). `static long countUnread(long userId)`, `static List<Match_Notification> ofUser(long userId)` (newest first), `static void markAllRead(long userId)`.
- `V4` adds `UNIQUE KEY uq_match_notification (SearchProfileID, MatchedUserID)`.
- Produces `public record ProfileSaved(long profileId)`, the event `ProfileController.saveProfile` publishes through `ApplicationEventPublisher`.
- Produces `public record NewMatch(Search_Profile search, int percent)`, and `static List<NewMatch> findNewMatches(Profile person, List<Search_Profile> searches, Map<Long, Profile> searcherProfiles, Set<Long> alreadyNotifiedSearchIds, LocalDate today)`. This is pure logic: it skips the person's own searches, a searcher whose profile is missing, and ids already notified, and keeps `percent >= 80`.
- `MatchNotifier.onProfileSaved(ProfileSaved)` is `@Async @TransactionalEventListener(fallbackExecution = true)`. If the profile is not finished, it does nothing. Otherwise it loads every search profile (one query, then all `search_profile_choice` rows in one query), the searchers' profiles (one query) and the pairs already notified for this person. For each `NewMatch` it does three things, each in its own try: write the `Match_Notification` (a duplicate-key error means another save got there first, so skip this match), push it to the owner's email with `convertAndSendToUser(email, "/queue/match", notification)`, and send the email. Mail errors are logged as `"New-match email not sent: " + e.getClass().getSimpleName()`, without the address or the text (NFR-7, NFR-21).
- Email body: `<Text>\n\nSee their profile: <app.base-url><Link>`.

- [ ] **Step 1: Write the failing tests** in `MatchNotifierTest`:
  - `eightyIsEnough`: a search scoring 80 is returned, and one scoring 79 is not.
  - `ownSearchSkipped`: the person's own search, scoring 100, is not returned.
  - `alreadyNotifiedSkipped`: an id in `alreadyNotifiedSearchIds` is not returned.
  - `textIsExact`: `Match_Notification.textFor("Weekend in Curepipe", 86)` equals `"Someone new matches your search Weekend in Curepipe at 86%"`.
  - `mailFailureKeepsSiteNotification`: call the per-match step through a package-private `void deliver(NewMatch m, Profile person, Account_User owner)`, with a `JavaMailSender` stub that throws `MailSendException`, a `SimpMessagingTemplate` stub, and a `Consumer<Match_Notification>` for the write that records its argument. The write and the push both happen, and no exception escapes. To make this testable, `MatchNotifier` takes the writer as a constructor argument, and the Spring constructor passes `Match_Notification::Write`.
- [ ] **Step 2: Run them to see them fail.**
- [ ] **Step 3: Implement** the entity, `V4`, the notifier, `@EnableAsync` and the event in `ProfileController.saveProfile`. Publish only after `profile.Update()`.
- [ ] **Step 4: The site part.**
  - `ViewAdvice` adds `@ModelAttribute("unreadNotifications") long` (0 when signed out).
  - `header.html` gets a bell `<li>` with a badge showing the count when it is above 0 (`aria-label="Notifications, N unread"`), linking to `/notifications`. It also gets `<meta name="_csrf">` and `<meta name="_csrf_header">`, then the STOMP.js 7.0.0 script (the URL `chat.html` uses) and `/notifications.js`, all only when `me != null`.
  - `notifications.js` opens its own STOMP client to `/ws-chat` with the CSRF header from the meta tags, subscribes to `/user/queue/match` and raises the badge by 1 for each message.
  - `NotificationController`: `GET /notifications` lists `ofUser`, newest first, each line linking to its `Link`, then calls `markAllRead`. The empty state reads "No notifications yet. You will be told here when someone new matches one of your searches."
- [ ] **Step 5: Run the tests.** Run `-Dtest=MatchNotifierTest`. Expected: PASS. Then run all the named test classes together: `-Dtest=ProfileSettersTest,SearchProfileMatchTest,SearchProfileAccessTest,SearchProfileFormTest,MatchRankingTest,MatchNotifierTest`. Expected: PASS.

### Task 8: Check it in the running app, and close out

**Files:** `.claude/TESTING.md`, `.claude/work/TASKLIST.md`, `.claude/work/USER-STORIES.md`, `.claude/CHANGELOG.md`, the session's `work/DONE/` entry

- [ ] **Step 1: Run the full build.** Run `.\mvnw.cmd -o clean package -DskipTests`. Expected: BUILD SUCCESS. Then run the six test classes. Expected: PASS.
- [ ] **Step 2: Try it in the running app, if MariaDB is up.** Start the app with `.\mvnw.cmd spring-boot:run`, then run the `db/` scripts. Create two sample accounts (sample data only, never real records). Fill in A's profile, then make B a search profile that A should match at 80% or more. Save A's profile again. Expected: B's bell goes up without a reload, `/notifications` lists it, an email arrives if mail is configured, and B's matches page shows A with the same %. Save A again. Expected: no second notification. Open `/profile/search/{B's id}` as A. Expected: 403.
- [ ] **Step 3: Write up.**
  - `TESTING.md` (Feature → test): FR-1 to FR-7, each with its test class, and the test log for this run.
  - `USER-STORIES.md`: a story goes to **Done** only if Step 2 ran and passed, and stays in **To verify** otherwise.
  - `TASKLIST.md`: drop the "views and procedures" item if Step 2 passed. Add `APP_BASE_URL` to the `.env.example` item. Add anything not run.
  - `CHANGELOG.md`: list the changes.
  - The `work/DONE/` entry: what was done, and what was actually checked.
