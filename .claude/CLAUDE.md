# MauLamour

This file is for everyone who works here, people and AI assistants alike (Codex reads `AGENTS.md`, which points here). The **Configuration** table is the one place the project's settings are written. Below it are the rules for how to work. Progress, history and requirements go in the other files.

## Configuration
Fill in the values first. The assistant reads them from here, so change a setting here and nowhere else. Environment addresses (development, test, live) and secrets go in `.env`, never here.

| Setting | Value |
|---|---|
| **Project name** | MauLamour (the repository and `spring.application.name` still use the old name MyDatingSite) |
| **What it does** (one sentence) | A dating website where people fill in a detailed profile, find matches and chat with them. |
| **Who it is for** | {{...}} |
| **Owner / team** | Loïc Fred, sole developer |
| **Client or sponsor** | None: a personal project, and the owner decides. |
| **Start date · Deadline** | 2025-08-31 (first commit) · {{YYYY-MM-DD}} (the full list is in `work/TASKLIST.md`, Dates and sprints) |
| **Code repository** | https://github.com/loicmdf/MyDatingSite, branch `master` |
| **Task board** | None. `work/USER-STORIES.md` is the board (`DECISIONS.md`). |
| **Agile method** | KANBAN → instructions in `agile/methods/KANBAN.md` (choose from `agile/methods/README.md`) |
| **Cycle length** | None (Kanban has no sprints) · **WIP limit:** 2 · **Review every:** 2 weeks |
| **Language and stack** | Java 25 · Spring Boot 4.1 through the `org.solarframework.mu:solar-framework` 1.0 parent, with SolarFramework's `core`, `database-impl`, `AuthenticationImpl`, `database-editor`, `web-utils` and `mailer-impl` (email) · Spring WebSocket with STOMP, Security messaging, Validation, Actuator · MariaDB · Thymeleaf, Bootstrap 5.3 and plain JavaScript with STOMP.js 7 |
| **Writing language** | English (UK) |
| **Build · Test · Run** | Set `JAVA_HOME` to `C:\Program Files\Java\jdk-25` first (the default JDK is older). `mvnw.cmd clean package` (add `-o` to build offline from the local Maven repository) · `mvnw.cmd test` · fill in `.env` in the project root (keys in `.env.example`), run `mvnw.cmd spring-boot:run` from the project root, then open `http://localhost:8080` |
| **Runs on** | {{Devices, operating systems, browsers and their oldest supported versions}} |
| **Must use** | SolarFramework (the owner's own framework) for the database, accounts and sign-in |
| **Must not use** | {{Banned tools or licences, e.g. no GPL libraries in closed-source code}} |
| **Data hosted in** | {{Country or region the app's data must stay in, e.g. EU only}} |
| **Accepted by** | The owner, against the checks in `work/USER-STORIES.md` |
| **Commit message** | `<type>: <what changed> (FR-n)`, e.g. `feat: reset password (FR-4)` |

Always loaded with this file: @work/REQUIREMENTS.md @NAMING.md @DECISIONS.md @agile/AGILE.md @agile/methods/KANBAN.md

## Where to write things
All project documents are in this `.claude/` folder, next to this file. Only `README.md` and `AGENTS.md` stay at the root.
**Every document and instruction file is named in FULL CAPITALS** (`REQUIREMENTS.md`, `SCRUM.md`, `AGILE.md`); a folder name is lower-case (`agile/methods/`, `work/`). A new instruction file follows this rule too. **The working documents live in `work/`** (`REQUIREMENTS.md`, `TASKLIST.md`, `USER-STORIES.md`, `USE-CASES.md`, `IDEAS.md`) and **the finished-work entries in `work/DONE/`**; everything else sits beside this file. A bare file name in these documents means the file in one of those places. On Windows a rename that only changes the case needs a temporary name in between.
Write each thing in one place only, and never leave something only in a chat.
- **`CLAUDE.md`**: settings and rules. Add a new rule as soon as it is agreed.
- **`agile/AGILE.md`**: the instructions that apply to every Agile method: the shared rules, vision, roadmap, release planning, estimation and priority, and anti-patterns.
- **`agile/methods/`**: how the chosen Agile method works, one file per method (`SCRUM.md`, `KANBAN.md`...). Follow the one named in the Configuration table, on top of `AGILE.md`.
- **`work/REQUIREMENTS.md`**: the list of features (FR) and qualities (NFR), and nothing else. Update it when a requirement is added or changes.
- **`work/TASKLIST.md`**: the dates, the sprint plan and the open work. Update it while you work. Before you stop, write down anything unfinished clearly enough that someone new can continue it.
- **`work/DONE/`**: what was done. Write one file per session, kept up to date as you work, following the rules under **Worklog** below.
- **`DECISIONS.md`**: choices already made. Read it before suggesting a different way.
- **`STUDY.md`, `USE-CASES.md`, `BRAND.md`**: why, who does what, and how it looks.
- **`USER-STORIES.md`**: what each user wants, one feature per story. **It is the board:** each story's Status column shows where it is (the rules are in that file).
- **`NAMING.md`**: how to name things. Read it before naming anything, and add a real example when you rename something.
- **`MEETINGS.md`, `IDEAS.md`, `TESTING.md`, `CHANGELOG.md`**: meeting notes, ideas, tests, and changes in each version.
- **Only name files and classes that really exist.** Check before you write a name in these files.

## Worklog
A record of what was built, what broke and why, and the choices made along the way. It is where the *reason* behind things is kept: many entries exist because something failed in a way that wasn't obvious, and the entry is the only place that explanation is written down.

**Add one for every work session**, even a short one. All finished work goes here.

**Write it as you work, not at the end.** A session can stop at any moment (a usage limit), and whatever isn't written yet is lost. Create the file as soon as the first real piece of work is done, then update it after each meaningful step — a feature slice working, a bug's cause found, a build or test run — not after every single edit. Unfinished work is described as it stands. At the end, only tidy it up (the title and file name may still change).

Create a new file:

```
.claude/work/DONE/YYYY-MM-DD<letter>-<kebab-title>.md
```

- The **letter** is the order of that day's entries: `a` for the first entry of the day, `b` for the next, and so on.
- The **title** is the heading in lowercase, with every character that isn't a letter or a digit turned into a hyphen. Cut it at a whole word, at about 60 characters.

Example: `.claude/work/DONE/2026-09-27a-login-page-and-password-reset.md`

The file starts with the full date and the title, then the author:

```markdown
# 27 Sep 2026 — Login page and password reset
Author: {{Name / Claude / Codex}}
```

Then write short paragraphs, each starting with a bold label:
- **Why.** What asked for this work, or what went wrong.
- **What.** What was built or changed, and the features it covers (FR-n).
- **How.** How it was done, and any problem hit on the way, with its cause and fix.
- **Checked.** How you know it works: tests run, pages opened, results.

Anything left unfinished goes in `TASKLIST.md` (Open work), not only here. When something is finished, remove it from there and describe it here instead.

**Never read the whole log**; it grows every session. List it with `ls .claude/work/DONE/` (the file names are the table of contents, dated and in order), search it with `grep -rn "<word>" .claude/work/DONE/` (the file name in each result tells you which session), then read that one file from start to end.

## Stories and status
- There is no task board (`DECISIONS.md`). Stories and their status live in `work/USER-STORIES.md`. Work that no user asks for directly goes in `work/TASKLIST.md` (Open work).
- **Keep the Status column in step as you work, not at the end.** A story's text says only what is wanted. Its Status shows progress: Backlog, Ready, In progress, To verify, Done, or Cancelled. A cancelled story is kept, never deleted.
- At most 2 stories are In progress at once (the WIP limit above).

## Ask first
Ask the owner before you:
- add, remove or replace a dependency (a library, plugin or outside service);
- deploy or release anything;
- change the database schema, or delete data;
- start using anything that costs money.

## The app's data
- The app's data (its users' accounts and records) never leaves the app. Don't paste it into chats, web searches, outside tools or published pages. The only exception is when sending it is a feature of the app, built and tested like any other feature (FR-n).
- Develop and test with sample data, never real records.
- `src/main/resources/application.properties` holds no secrets and no machine-specific addresses. They live in the git-ignored `.env` at the project root, which `spring.config.import` reads when the app starts. Add every new key to `.env.example` with an empty value. Never commit `.env`.
- The code may be shared with outside tools when the work needs it (a search, a review, an MCP server).

## Scope
Read this section before suggesting a feature. Don't build anything listed under "We will not build".

**We will build:**
- {{...}}

**We will not build** (don't suggest these either):
- {{...}}

## Writing code
- **Edit files with the Edit and Write tools, never through `sed -i`, `perl -i` or shell redirects** (`hooks/no-shell-edits.sh`, run from `settings.json`, refuses them). The owner watches the console for the `Update(<file>)` and `Write(<file>)` cards, and Edit refuses an inexact match where `sed` duplicates or drops lines in silence. The shell stays for reading, searching, building and moving files.
- Keep code short and simple. Change only what is needed.
- **One method does one job**, and its name says what that job is (see `NAMING.md`).
- Put a method on the object whose data it uses.
- Add a short comment when the reason for a line isn't obvious. Feature codes can go in comments (`// FR-3: ...`), but never on screen.
- Check every form. Every error message says how to fix the problem.
- Write tests for things that could really break. Don't test simple getters.
- Take colours, fonts and wording from `BRAND.md`.
