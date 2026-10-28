# Decisions

Choices already made, and why. Don't suggest them again unless the reason has changed. Add a new choice as soon as it is made, newest at the top.

## 2026-10-28 — Email through SolarFramework's mailer, as in MauCareers
- **We chose:** `org.solarframework.mu:mailer-impl` in place of `spring-boot-starter-mail`. Mail is sent with `MailRegistry.DefaultMailService.send(to, subject, body)`. Links in background emails start with `maulamour.public-url`, set in `.env`.
- **Other options:** Spring's `JavaMailSender`, with an `APP_BASE_URL` setting.
- **Why:** SolarFramework already has a way to send email, used by SolarERP and MauCareers. Decided by the owner.
- **What it means:** the mailbox is seeded from `spring.mail.*` into `config/mail/mailboxes.json` on the first run, and after that the file wins (MailerImpl README). That file holds the password, so `config/mail/` is git-ignored.

## 2026-10-28 — The match % is worked out in Java, not in the database
- **We chose:** `Search_Profile.matchPercent`, used by both the matches page and the new-match notifications. It replaces the old `MatchUsers` procedure.
- **Other options:** a SQL procedure that scores and pages in the database.
- **Why:** one rule in one place, unit-tested without a database. The design was approved by the owner on 28 Oct 2026 (`work/MATCHING-DESIGN.md`).
- **What it means:** every member is loaded in three queries for each matches page. That is fine for thousands of members. Far beyond that, move the scoring into SQL.

## 2026-10-27 — Kanban, with a limit of 2 items in progress
- **We chose:** Kanban (`agile/methods/KANBAN.md`). At most 2 stories or tasks in progress at once, and a review every two weeks.
- **Other options:** Scrum, Scrumban, XP, Lean, FDD.
- **Why:** a personal project, with one owner working alongside assistants, and no client, course or fixed deadline. Sprints would add ceremony with nobody to report to. The same choice as RemoteDesktopMobile. Decided by the owner.
- **What it means:** no sprints and no story points. Progress is the Status column in `work/USER-STORIES.md`.

## 2026-10-27 — No task board: USER-STORIES.md is the board
- **We chose:** no YouTrack or other board. The stories, and where each one is (Backlog, Ready, In progress, To verify, Done), live only in the Stories table of `work/USER-STORIES.md`. Tasks that no user asks for directly go in `work/TASKLIST.md` (Open work).
- **Other options:** a YouTrack board kept the same as `USER-STORIES.md`, as the template suggests.
- **Why:** one place to update instead of two. Decided by the owner.
- **What it means:** nothing to keep in step and no token to manage, but no charts. Lead time comes from the `work/DONE/` dates.

## 2026-10-27 — The new database starts empty; the old members are not carried over
- **We chose:** to start the new `maulamour` database empty. The rows in the old `my_dating_site` (local MariaDB, port 3309) are not copied, and `db/solarframework-migration.sql` is not run.
- **Other options:** dump the old database, rewrite the migration to copy every table (accounts, profiles, photos, chats, messages), and load it into the new server.
- **Why:** the old database held only a few rows, which is not worth a full migration. Decided by the owner.
- **What it means:** old members create their accounts again. The old database is still needed for one thing: the definitions of its views and procedures (`TASKLIST.md`).

## 2026-10-27 — Secrets and addresses in a git-ignored `.env`
- **We chose:** `application.properties` starts with `spring.config.import=optional:file:.env[.properties]`. The database address, user and password and the mail account live in `.env` at the project root, written as ordinary property lines. `.env.example` lists the keys with empty values.
- **Other options:** environment variables set by hand on each machine; a Spring profile file kept out of git.
- **Why:** the passwords were committed in `application.properties`. The owner asked for the same set-up as MauCareers.
- **What it means:** each machine needs its own `.env` before the app starts. The app must start from the project root, or it will not find `.env`. Values in `.env` win over the same keys in `application.properties`.

## {{YYYY-MM-DD}} — {{The choice in a few words}}
- **We chose:** {{...}}
- **Other options:** {{...}}
- **Why:** {{the reason, and who decided}}
- **What it means:** {{what gets easier, and what gets harder}}
