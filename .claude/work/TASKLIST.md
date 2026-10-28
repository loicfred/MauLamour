# Task list

Everything that is still to be done is in this file, and only here: the dates and sprints, the open work, and the steps from idea to release. `REQUIREMENTS.md` holds nothing but the list of requirements.
The project's name, people, repository, board and Agile method are in the **Configuration** table of [`../CLAUDE.md`](../CLAUDE.md). Fill that in first.

## Dates and sprints
| Date | What is due |
|---|---|
| {{YYYY-MM-DD}} | {{Requirements agreed}} |
| {{YYYY-MM-DD}} | {{First working version}} |
| {{YYYY-MM-DD}} | {{Release}} |

| Sprint | Dates | Goal | Features |
|---|---|---|---|
| 1 | {{...}} | {{Set up the project}} | {{...}} |
| 2 | | | |

## Open work
The running list of what is left. Update it in the same pass as the job it covers: add an item the moment it appears, strike it the moment it is done. Finished items are described in `work/DONE/` and removed from here, never left standing as done. Write each item so someone new can pick it up: what is missing, where, and what is blocking it. Before you stop, write down anything unfinished here.

- [ ] **Change the mail and database passwords.** Both were committed in `application.properties` and pushed to GitHub (`loicmdf/MyDatingSite`), so they are in the history even though they now live in `.env`. Change them on the Gmail account and the MariaDB server, then put the new ones in `.env`.
- [ ] **Create `.env.example`** at the project root. The assistants' permission settings block `.env*` files, so the owner creates it. Content: `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, `spring.mail.username`, `spring.mail.password`, each with an empty value, plus `maulamour.public-url` (the site's address that new-match emails link to, for example `https://maulamour.example`; it defaults to `http://localhost:8080`), plus `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` commented out. Leave those two unset until there is a Google client: an empty value would replace the `not-configured` default.
- [ ] **Drop the 9 capitalised leftover tables** (`Profile`, `Profile_Photo`, `Search_Profile`, `Chat`, `Draft`, `Message`, `Message_Reaction`, `Draft_Attachment`, `Message_Attachment`) from the new database. The first start created them before the entities had lowercase names. Check they are empty, and ask the owner before dropping them.
- [ ] **Run `db/V1` to `V4` on the `maulamour` server** (`db/README.md`, in number order). They were written on 28 Oct 2026 but never run, because the assistant has no access to the password in `.env`. The views were written again from the code: the old database wasn't running, and the procedures are no longer used. `V3` drops 11 old single-answer columns from `search_profile`, so check first that the table is empty (`SELECT COUNT(*) FROM search_profile`). Hibernate already added the new tables and columns on 28 Oct, when `contextLoads` started the app against this server.
- [ ] **Saves that take several steps are not atomic (NFR-14). This needs a SolarFramework change.** In DatabaseImpl, a `TransactionManager` bean only exists once a transaction is already bound (`DatabaseService.getJpaBeans`), and every `Write()`, `Update()` and `doUpdate` runs its own transaction. So a `@Transactional` method fails with a 500, and the app uses none (`TransactionalUsageTest`). If the server fails mid-save, a profile can keep its old interests, or a search can lose its choices. Fix in the framework: register each source's `JpaTransactionManager` at start-up, and make those writes join a bound transaction. Then put `@Transactional` back on `ProfileController.saveProfile`, `SearchProfileController.saveSearch`/`deleteSearch` and the `ChatService` writes. Watch out: with a managed entity, a search form with errors would still be written at commit, so `applyForm` would have to work on a copy.
- [ ] **Decide on `management.endpoints.web.exposure.include=*`** in `application.properties`. It exposes every Actuator endpoint, including `env` and `heapdump`, to anyone signed in. Keep only `health` unless they are needed.
- [ ] **Try in the running app**, after the scripts: sign-up, log-in, profile edit (the new questions, the interest tags, keeping the photo), the profile page, chat, and creating, editing and deleting a search. Then two sample accounts: one saves a finished profile that matches the other's search at 80% or more. The bell rises without a reload, `/notifications` lists it, the email arrives with the right link, and a second save sends nothing more. US-1 to US-7 stay in To verify until this passes.
- [ ] Fill in the last Configuration rows in `CLAUDE.md`, which only the owner can answer: who it is for, deadline, runs on, must not use, data hosted in.

---

## Steps from idea to release
Go from top to bottom and tick each box when it is done. The arrow → shows which file to write the answer in.
Items in **bold** are the most important. Try to finish them before you move on to the next step.
Items marked *(if it has one)* don't apply to every project. Skip them when they don't fit.

## Step 0 — Start
- [ ] **Fill in the Configuration table** → `CLAUDE.md` (Configuration)
- [x] Put the project name and the one-sentence description in `README.md`
- [ ] Write down the important dates → `TASKLIST.md` (Dates and sprints)
- [x] Create the code repository and make the first commit
- [x] Create the project on the task board and connect it to Claude Code → `CLAUDE.md` (Task board): no board, `USER-STORIES.md` is the board (`DECISIONS.md`)
- [x] **Choose the Agile method** (read `agile/methods/README.md`), set it in `CLAUDE.md` (Configuration), and say why → `DECISIONS.md`

## Step 1 — Understand the problem
- [ ] **What is the problem?** Who has it, and how often? → `STUDY.md` (Problem)
- [ ] **Who is it for?** Who uses it, who pays, and who else is affected → `STUDY.md` (Target audience)
- [ ] Describe 2 to 4 typical users (their goals, what annoys them, their skills) → `STUDY.md` (Typical users)
- [ ] How do people do this today, without your product? → `STUDY.md` (How it is done today)
- [ ] What else already exists, and what does it do badly? → `STUDY.md` (Similar products)
- [ ] Talk to real users: interviews, surveys, watching them work → `STUDY.md` (Research notes)
- [ ] **Goals**: what the project must achieve, with numbers and a date → `STUDY.md` (Goals)
- [ ] How you will know it worked → `STUDY.md` (Measuring success)
- [ ] Money: who pays, what it costs, the budget → `STUDY.md` (Money)
- [ ] Limits and guesses: deadline, budget, tools you must use, things you assume → `STUDY.md` (Limits and guesses)
- [ ] Personal data: what you keep, why, and for how long → `STUDY.md` (Personal data)
- [ ] Word list: words your users use that the team must understand → `STUDY.md` (Word list)
- [ ] Can it be done? Skills, time, money, legal → `STUDY.md` (Can it be done?)
- [ ] What could go wrong, and what you will do about it → `STUDY.md` (Risks)

## Step 2 — Decide what it must do
- [ ] **Who uses the system** (every type of user, plus other systems) → `USE-CASES.md` (Users)
- [ ] **What is in and what is out**, so the project does not keep growing → `CLAUDE.md` (Scope)
- [ ] **Features** (what the system does) as FR-1, FR-2... Each one must be testable → `REQUIREMENTS.md` (Features)
- [ ] **Qualities** (how well it does it: speed, security, ease of use...) as NFR-1, NFR-2... → `REQUIREMENTS.md` (Qualities)
- [ ] **Existing code, systems and data**: what it must connect to, replace or import, and in what format → `STUDY.md` (Limits and guesses)
- [ ] **Scale**: how many users and how much data now and in a year, and the busiest moment → `REQUIREMENTS.md` (Qualities)
- [ ] **Downtime and data loss**: how long it may be down, and how much recent data may be lost after a failure → `REQUIREMENTS.md` (Qualities)
- [ ] Languages, time zones and currencies it must handle, and whether it must work offline → `REQUIREMENTS.md` (Qualities)
- [ ] How it reaches users and gets updated (website, installer, app store, automatic update) → `DECISIONS.md`
- [ ] Mark each feature Must, Should, Could or Won't → `REQUIREMENTS.md`
- [ ] **List of use cases** (UC-1, UC-2...), each linked to its features → `USE-CASES.md` (List)
- [ ] Write the main use cases step by step, including what happens when something goes wrong → `USE-CASES.md` (Steps)
- [ ] **Who can do what**: a table of each user type against each thing in the system → `USE-CASES.md` (Who can do what)
- [ ] **User stories**, each one feature, each with its checks, created on the board and in the file together. Start from the common stories that apply → `USER-STORIES.md` (Common stories)
- [ ] Draw the use-case diagram
- [ ] Show the requirements to the client or sponsor, and note their answer → `MEETINGS.md`

## Step 3 — Brand
- [ ] **Name, slogan and a short description** → `BRAND.md` (Name)
- [ ] How the product talks to users: words to use and words to avoid → `BRAND.md` (Tone)
- [ ] **Colours**, checked so text is easy to read → `BRAND.md` (Colours)
- [ ] Fonts and text sizes → `BRAND.md` (Fonts)
- [ ] Logo and icons *(if it has one)* → `BRAND.md` (Logo)
- [ ] One icon set and one image style → `BRAND.md` (Icons and images)
- [ ] How to write buttons, error messages and empty pages → `BRAND.md` (Writing)

## Step 4 — Design
- [ ] **How the system is built**: the main parts and how they talk to each other → `DECISIONS.md`
- [ ] Languages and tools chosen, and why → `DECISIONS.md`
- [ ] **Stored data**: what is saved and how the pieces link together (for a database: tables, ERD, what each column means)
- [ ] Class diagram
- [ ] Sequence diagrams for the main use cases
- [ ] State diagrams for things that change status (an order, a request, a ticket...)
- [ ] **Screens** *(if it has one)*: list of pages, rough sketches, then finished mockups using the brand
- [ ] API *(if it has one)*: the addresses, what they receive, what they send back, how errors look
- [ ] Security: who is allowed to do what, and how data and passwords are kept safe
- [ ] Show the design to the client or sponsor → `MEETINGS.md`

## Step 5 — Set up
- [ ] **Coding rules** written down → `CLAUDE.md`
- [ ] Naming rules read, and the first real examples added → `NAMING.md`
- [ ] Empty project that builds
- [ ] Code formatter and checker
- [ ] Settings, passwords and environment addresses (development, test, live): an `.env.example` in the repository, the real `.env` kept out
- [ ] Database created *(if it has one)*
- [ ] Sample data for testing and for the demo
- [ ] Test tool set up, with one test passing
- [ ] Automatic build and tests on every push
- [ ] Branch and commit message rules → `CLAUDE.md`

## Step 6 — Build (repeat every sprint)
- [ ] Plan the sprint: pick the stories and tasks and estimate them → the board
- [ ] `USER-STORIES.md` and the board still match → `USER-STORIES.md`
- [ ] Build each task with its tests. Put the feature code in the commit message (FR-3)
- [ ] Check every form, and make every error message say how to fix the problem
- [ ] Clean up the code: nothing copied twice, no very long methods
- [ ] Add anything left unfinished to `TASKLIST.md` (Open work) **straight away**
- [ ] Write down any idea as soon as you have it, even a small one → `IDEAS.md`
- [ ] Note every meeting → `MEETINGS.md`
- [ ] Sprint review: show what works, compare it with the plan
- [ ] Look back: what to keep doing, stop doing, try next → `work/DONE/`
- [ ] Write a worklog entry at the end of each work session → `work/DONE/`

## Step 7 — Test
- [ ] **Test plan**: what to test, how, and who tests it → `TESTING.md` (Plan)
- [ ] Unit tests for the real logic (not for simple getters)
- [ ] Tests where parts meet (database, other services, files...)
- [ ] Full tests of the main use cases, start to finish
- [ ] Each feature (FR) linked to the test that checks it → `TESTING.md` (Feature → test)
- [ ] Quality checks (NFR): speed, security, accessibility, the devices it must run on
- [ ] **Test log**: each test run, its date, its result, and the fix → `TESTING.md` (Test log)
- [ ] Real users try it, and their feedback is written down

## Step 8 — Write the documents
- [ ] **User guide**, one part for each type of user
- [ ] How to install it
- [ ] Developer guide: how it is built, how to run and test it, how to add a feature
- [ ] API guide *(if it has one)*
- [ ] Final report *(if it has one)*

## Step 9 — Release
- [ ] New version number and a list of changes → `CHANGELOG.md`
- [ ] Database changes tried on a copy first
- [ ] Installed on a fresh machine without any manual fixes
- [ ] Backups set up, and restoring one tested
- [ ] Logs and error alerts in place
- [ ] Demo script, and a plan B if something fails

## Step 10 — Finish
- [ ] Look back at the whole project: the plan against what really happened, and lessons learned → `work/DONE/`
- [ ] Everything unfinished is either done or written in `TASKLIST.md` (Open work)
- [ ] Repository cleaned up, archived or handed over
