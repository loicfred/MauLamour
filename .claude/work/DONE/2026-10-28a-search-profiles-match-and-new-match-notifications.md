# 28 Oct 2026 — Search profiles, match % and new-match notifications
Author: Claude

**Why.** The owner asked to "complete the dating site": missing tables and missing features. In conversation they narrowed it to four things:
- more profile questions (for example skin colour);
- several search profiles per member, each describing someone they are looking for;
- a matches page ranked by match %, where every setting counts the same and the % is the settings matched divided by all the settings;
- a notification on the site and by email when someone new matches a search at 80% or more.

No swiping. Partway through they asked for gaps to be found and fixed, not listed. They also asked for mail to go through SolarFramework, the way SolarERP and MauCareers do it.

**What.** `work/MATCHING-DESIGN.md` (approved by the owner) and `work/MATCHING-PLAN.md`, then the build. It covers FR-1 to FR-7 and US-1 to US-7, which are now in To verify.
- **Profile (FR-1):** 8 new enums and the new `Profile` fields; `isFinished()` (name, gender, birth date, location); `getAnswers()` with `enums.Displayable`. `edit.html` gets a Physical section and the other new questions. `view.html` shows every answer.
- **Search profiles (FR-2 to FR-4):** `SearchChoice` (21 "any of" choices) and a rewritten `Search_Profile`, with `YES_NO_SETTINGS`, `matchPercent`, `applyForm` (each error message says how to fix it), `requireOwnedBy`, `saveChoices` and `loadChoicesFor`. `SearchProfileController` and a new `profile/search.html` handle create, edit and delete; delete asks first, and a member can keep at most 20 searches.
- **Matches (FR-5):** `MatchRanking` (rank, then pages of 20; the candidates load in three queries). `MatchController` owns `/matches`, and `matches.html` shows the real %.
- **Notifications (FR-6, FR-7):** the `Match_Notification` entity and `MatchNotifier`, which runs `@Async` after the profile save commits and sends once per person and search. There's a bell in the header, `notifications.js` (live over the existing `/ws-chat`), and `/notifications`. Email goes through `mailer-impl`'s `DefaultMailService`, with the link base from `maulamour.public-url`.
- **Database:** `db/V1` (choice tables), `V2` (the four views, written again from the code), `V3` (drops the old single-answer `search_profile` columns and makes the age and distance columns nullable), `V4` (unique pair and index), and `db/README.md`.

**How.** Each piece was built test-first, and the ledger in `.superpowers/sdd/MATCHING-PLAN/progress.md` holds every ruling. Problems found on the way, with their causes:
- **View classes read the plain tables.** `DatabaseObject.getTableName` walks up to the parent's `@Table`, so `Profile_View` and the other view classes read `profile`, `chat` and so on. Fixed with an `@Table` on each view class.
- **Forged forms could change another member's interests.** The profile save trusted the posted `ID`, and `setInterests`/`setLanguages` wrote to the database while Spring was still binding the form, before any ownership check. The `ID` now comes from the signed-in account, the setters only keep values in memory, and the save happens in one transaction.
- **Saving erased the photo.** `Update()` writes every column, so a save without a new photo set the avatar to null. The stored photo is now kept.
- **Interests couldn't be cleared.** Unticking every tag posts nothing, so the setter was never called. The controller now passes an empty set.
- **The tag picker never worked by itself.** It looked for `interests-tag`, but the tags were `interest-tag`, and two copy-pasted scripts did the real work. It is now one shared `static/tag-picker.js` with keyboard-usable buttons.
- **Smaller fixes:** "What studies did you do?" was missing from the form. Non-image uploads were stored as the avatar. An unknown profile address threw a null error (now 404). `matches.html` linked avatars by ID instead of username. The `search_profile` age and distance columns were NOT NULL, so "I don't mind" couldn't be saved.
- **Mail password kept out of git.** `mailer-impl` writes the mailbox, password included, to `config/mail/mailboxes.json`, so `config/mail/` is git-ignored.
- **The test touched the shared server.** `.env` points at a remote MariaDB server, not a local one. When `contextLoads` started the app, Hibernate added this work's tables and columns to that `maulamour` database. That is additive and within the approved design, but it wasn't intended from a test.

**Review.** A fresh reviewer read the whole change. Each finding was checked against the SolarFramework source first, then fixed test-first where a test can run without the database.
- **Critical, every save would fail with a 500.** `@Transactional` can never start in this app. DatabaseImpl registers its `TransactionManager` only from inside a transaction that is already bound, so Spring finds none. That would have broken the profile save, the search save and delete, and all 8 `ChatService` writes from the 27 Oct move. The annotation is removed everywhere (`TransactionalUsageTest`). Multi-step saves are therefore not atomic, which needs a framework change (`TASKLIST.md`).
- **Stale pages.** View classes have no `@Entity`, so a write never cleared their cache, for up to an hour. After saving, the form showed the old values, the notifier missed newly finished profiles, and chat lists and reaction counts lagged. `ViewCache` now clears them after each write.
- **Caches not cleared by raw updates.** `doUpdate` without a class clears no cache, so the bell kept its number after reading. Every raw update now names its entity class.
- **Photos never shown.** Reads replace blob columns with NULL. `profile_view.HasAvatar` tells pages whether there is a photo, `Profile.fetchAvatar()` serves the bytes, and the photo cache is cleared when a new one is uploaded.
- **Smaller fixes:** emptying "previous partners" gave a 400 (now an `Integer`); a forged form could set `Avatar` or `ID` (an `@InitBinder` refuses both); Previous past the last page led nowhere; the live push sent the whole entity (now only the text and link).

**Checked.**
- `mvnw.cmd -o clean package -DskipTests` builds.
- 12 test classes, 60 tests, all pass. Each test was seen failing first, or the code was broken on purpose to prove the test can fail (the 80% threshold, a template expression, a re-added `@Transactional`, an emptied binder rule).
- `TemplateRenderTest` renders the search, matches and notifications pages without a database.
- `contextLoads` passes against the remote server.
- **Not checked:** the `db/` scripts were not run, because the password in `.env` isn't readable here and `V3` drops columns on a shared server. The app was not tried by hand, and `edit.html` and `view.html` have not been rendered. Both are in `TASKLIST.md`.
