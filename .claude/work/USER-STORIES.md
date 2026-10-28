# User stories

What each type of user wants, written as stories. Each story is linked to its features in `REQUIREMENTS.md`.

## This file is the board
There is no separate task board (`DECISIONS.md`, 27 Oct 2026). The Stories table below is the backlog and the board.
- **Adding a story:** give it the next code (US-n), add it to the table, and write its checks below.
- **Changing a story:** change its text and its checks in the same session.
- **Moving a story:** change its Status: Backlog → Ready → In progress → To verify → Done. At most 2 stories are In progress at once (`CLAUDE.md`, WIP limit).
- **Dropping a story:** set its Status to Cancelled and keep the row. Never delete it.
- **Before starting work:** check that the Status column says what is really happening. If it doesn't, fix that first.

## How to write one
- The format is **As a** *type of user*, **I must be able to** *do one thing*, **so that** *reason*.
- **One story is one feature.** A feature is one thing the user does that can be shown on its own in a demo and passed or failed by one test. Never group features into one story.
- **Split test:** if the sentence joins two actions with *and*, *or* or a comma list, or the "so that" would be different for each half, it is two stories. "Create an account and log in" is two stories. "Publish a vacancy and edit or close it later" is three. A screen with five buttons is five stories, not one "manage X" story.
- **One type of user per story**, taken from the Users table in `USE-CASES.md`. Use the plain word *user* only when every signed-in type may really do it. Never write "As a developer".
- **The reason is the user's own.** "…so that I can find work that suits me" is a reason. "…so that the data is stored" is not, and neither is repeating the action.
- **Say what, not how.** No table, class, endpoint or library name in a story. That belongs in the task under it.
- **A quality is not a story.** Speed, security and looks ("fast", "easy on the eyes") are non-functional requirements in `REQUIREMENTS.md`. A story may trace to one, but it names the feature that carries it.
- **Every story traces to at least one requirement**, and every requirement appears in at least one story. A story with no requirement means the requirement is missing or the story is not needed.
- Work that no user asks for directly, such as setup, refactoring or diagrams, is a **Task** on the board. A heading that groups stories (for example a whole portal) is an **Epic**. Neither is a story, and neither goes in the table below.
- Keep the story text to one sentence. Put the details in the checks below it.
- **Each story has a code, a priority (High, Medium, Low) and a status on the board.** A story that is dropped is marked *Cancelled* and kept, never deleted. When the backlog is issued again as a new version, a story added since the last version is marked **(New)**, so the history can be read from the versions.
- Write the status on the board only. The text says what is wanted, never how far along it is.

### A good story: INVEST
- **I**ndependent: it does not need another story to be built first.
- **N**egotiable: not so detailed that nothing can be discussed.
- **V**aluable: it adds value for the user, said in the user's words, not technical words.
- **E**stimable: clear enough to give an effort estimate (`agile/AGILE.md`, section 4).
- **S**mall: a sprint holds roughly 6 to 10 stories, and one story is 8 points or fewer.
- **T**estable: it can be checked with a clear yes or no.

More tips: describe an **intention**, not a feature (not "manage my profile" but "register", "upload my photo", "link my card": each has its own value); keep it short; **leave the screen design out**.

### Story fields
Title (a short name) · As a (the user) · I must be able to (one action) · so that (the benefit) · **acceptance criteria** · value and risk (low, medium, high) · effort (story points) · who raised it.
The "so that" should trace to a goal or metric in `STUDY.md`. If you cannot say what value the story gives the user and the product, do not build it.

### Acceptance criteria
The conditions that confirm a story is finished. Write each as a check you can run, in the form *When I (take this action), (this happens)*, or as Given / When / Then, and put them under the story in "Checks for each story" below. Example for "As a passenger, I must be able to see several available drivers so that I can choose the best one":
- The app shows drivers who were online in the last 20 minutes and have no ride in progress.
- It shows only the 5 drivers closest to the user.
- The user can open each driver's profile, with photo and rates.

### One grouped story, split
| Grouped (wrong) | One feature each (right) |
|---|---|
| As a customer, I want to create an account and log in so that my orders are mine. | As a customer, I must be able to register with my email and a password so that my orders belong to me. · As a customer, I must be able to log in so that I reach my own account. |
| As a manager, I want to add, edit and remove products so that the shop stays correct. | As a manager, I must be able to add a product so that customers can buy it. · …edit a product so that its details stay correct. · …remove a product so that customers stop seeing it. |

### Where this comes from
These rules follow common product backlog practice: a code, the story, a priority and a status on every row, cancelled stories kept, new stories flagged. The "must be able to" wording and the one-feature rule are the owner's own requirement for every project built from this template.

## Common stories
Most applications need these. When you plan the backlog, go through the list and copy every story that applies into **Stories** below, together with its checks, its FR in `REQUIREMENTS.md` and its item on the board. Change the user type and the numbers to fit. Skip a whole group when the app does not have that part. A story stays here, and not in the table, until it is on the board.

### Accounts *(if it has accounts)*
- **As a visitor, I must be able to create an account with my email and a password so that my data is kept for me.**
  - When I use an email that already has an account, I am told so and offered to log in.
  - When my password breaks the rules, I am told which rule, and nothing I typed is lost.
  - My email is confirmed by a link before the account can be used.
- **As a user, I must be able to log in so that I reach my own account.**
  - When the email or password is wrong, one message says so without saying which one.
  - After 5 failed tries in a row, the next try has to wait.
- **As a user, I must be able to log out so that nobody else can use my account on this device.**
  - After logging out, the Back button shows no private page.
- **As a user, I must be able to reset a forgotten password by email so that I can get back into my account.**
  - The message is the same whether or not the email has an account.
  - The link works once and expires after 1 hour.
- **As a user, I must be able to change my password so that my account stays safe.**
  - My current password is asked for first.
  - My other sessions are logged out.
- **As a user, I must be able to edit my profile details so that they stay correct.**
  - Changing my email confirms the new one before it is used.
- **As a user, I must be able to delete my account so that my personal data is removed.**
  - I am asked to confirm, and told what will be deleted.
  - My personal data is deleted or made anonymous within {{30 days}}.

### Own data *(if it keeps personal data)*
- **As a user, I must be able to download a copy of my data so that I can keep it or move it elsewhere.**
  - The file is in a common format (CSV or JSON) and holds everything the app keeps about me.

### Administration *(if it has admins)*
- **As an admin, I must be able to block an account so that a misused account stops working at once.**
  - A blocked user is logged out and cannot log in; their data is kept.
- **As an admin, I must be able to see who did each important action, and when, so that I can find out what happened.**
  - Logins, permission changes and deletions are listed with the user, the time and what changed.

### Languages *(if it has more than one)*
- **As a user, I must be able to choose the language of the app so that I can read it.**
  - My choice is remembered the next time I come back.

## Stories

| Code | Story | Features | Priority | Status |
|---|---|---|---|---|
| US-1 | As a member, I must be able to answer questions about my looks, background and home life so that people looking for someone like me can find me. | FR-1 | High | To verify |
| US-2 | As a member, I must be able to create a search profile so that I can describe someone I am looking for. | FR-2 | High | To verify |
| US-3 | As a member, I must be able to edit a search profile so that it keeps matching what I want. | FR-3 | High | To verify |
| US-4 | As a member, I must be able to delete a search profile so that a search I no longer want stops showing up. | FR-4 | High | To verify |
| US-5 | As a member, I must be able to see members ranked by how well they match one of my search profiles so that I meet the best fits first. | FR-5 | High | To verify |
| US-6 | As a member, I must be able to see a notification on the site when someone new matches one of my searches at 80% or more so that I don't miss a strong match. | FR-6 | High | To verify |
| US-7 | As a member, I must be able to get an email when someone new matches one of my searches at 80% or more so that I hear about it while I'm away from the site. | FR-7 | High | To verify |

## Checks for each story
How to tell a story is finished. Each line must be something you can test.

### US-1 — Answer the extra profile questions
- [ ] When I open my profile form, I can answer height, body type, skin colour, eye colour, hair colour, ethnic background, tattoos, highest education, living situation and relationship status.
- [ ] When I leave any of them blank and save, the profile saves and the blank answers stay blank.
- [ ] When I give a height under 100 or over 250 cm, it is not saved.
- [ ] When someone opens my profile, they see my answers.

### US-2 — Create a search profile
- [ ] When I save a search with a name and some settings, I am taken to my matches for that search.
- [ ] When I leave the name empty, I am told to give it a name and nothing I typed is lost.
- [ ] When the lowest age is above the highest, I am told to fix it.
- [ ] Any setting left on "I don't mind" doesn't count towards the %.

### US-3 — Edit a search profile
- [ ] When I open one of my searches, I see what I saved, and saving my changes updates the matches.
- [ ] When I open someone else's search by its address, I get "not allowed" and see none of it.

### US-4 — Delete a search profile
- [ ] When I press Delete, I am asked to confirm first.
- [ ] After deleting, the search is gone from my drop-down.
- [ ] I cannot delete someone else's search.

### US-5 — See members ranked by match %
- [ ] When someone matches 7 of my search's 9 settings, their card shows 77%.
- [ ] The cards go from the highest % down; equal scores show the newest member first.
- [ ] I never see myself, nor a member without a name, gender, date of birth or location.
- [ ] There are 20 cards a page, with Previous and Next.

### US-6 — Site notification of a new strong match
- [ ] When a member saves a finished profile that matches my search at 80% or more, my bell goes up without a reload.
- [ ] The notification reads "Someone new matches your search ‹name› at ‹n›%" and links to their profile.
- [ ] I am never told twice about the same person for the same search.
- [ ] My own profile never notifies me.

### US-7 — Email of a new strong match
- [ ] I get an email with the same sentence and a link to the profile.
- [ ] When mail can't be sent, the site notification still arrives.
