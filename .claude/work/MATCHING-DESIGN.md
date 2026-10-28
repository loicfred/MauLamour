# Design: profile questions, search profiles, match % and new-match notifications

Written 28 Oct 2026 with the owner. This is the description to build from. When it is approved, its features go into `REQUIREMENTS.md` (FR) and `USER-STORIES.md` (stories, starting in Backlog), and the plan comes after that.
Lines marked **(Claude's call)** were not discussed with the owner. Each one can be changed without changing the rest.

## Why
MauLamour is for people who want to find a partner by describing who they are and who they are looking for. **There is no swiping** (the owner's words). A member fills in a detailed profile, then describes the person they want in one or more *search profiles*, and the site ranks other members by how well they fit. Today none of this works end to end:
- The search profile page (`templates/profile/search.html`) is a copy of the profile form. It posts to the profile save (`/post/profile/edit`), nothing saves a `Search_Profile`, and the `search_profile` table has no column naming its owner.
- The matches page (`templates/matches.html`) shows a fixed "10%" and calls the `MatchUsers` procedure, which does not exist in the new database.
- `profile_view`, `FetchUserdata`, `chatlist_item`, `message_view` and `Message_Reaction_View` are also missing (`TASKLIST.md`), so the profile, matches and chat pages cannot load.

## Goals
1. **More profile questions**, including physical ones such as skin colour (the owner's example).
2. **Search profiles.** After their profile, a member creates search profiles, each describing someone they are looking for. They can have as many as they like, and can edit and delete them.
3. **Matches ranked by %.** On the matches page the member picks one of their search profiles and sees other members from the highest match % down.
4. **New-match notifications.** When a new person appears who matches one of a member's search profiles at **80% or more**, the member is told **both on the site and by email**.
5. **The database pieces that are missing** are created from scripts kept in the repository, so a fresh database works.

## Not part of this
- **No swiping**, likes or "hot or not" screens.
- No requirement that the match goes both ways: the % says how well *the other person* fits *my* search, not whether I fit theirs.
- Carrying over the old members (`DECISIONS.md`, 27 Oct 2026).
- Dropping the 9 capitalised leftover tables; that stays in `TASKLIST.md`.

## It would be a failure if
- A member's email inbox fills with repeats of the same person.
- Saving a profile becomes slow because notifications are being worked out.
- Someone can see, edit or delete another member's search profile.
- A member is told about themselves, or sees themselves in their own results.

## 1. New profile questions
Added to the profile form (`profile/edit.html`) and shown on the profile page (`profile/view.html`). Each list is a new enum in `object/enums/`, as the existing questions are. **(Claude's call: which questions, and the wording of every list.)** The owner asked for "more questions, even skin colour" and left the rest open.

| Question | Answer | Choices |
|---|---|---|
| How tall are you? | number, in cm | 100 to 250 |
| Body type | one choice | Slim, Athletic, Average, Curvy, Muscular, Plus size |
| Skin colour | one choice | Very fair, Fair, Medium, Olive, Brown, Dark brown, Very dark |
| Eye colour | one choice | Brown, Black, Hazel, Green, Blue, Grey, Other |
| Hair colour | one choice | Black, Brown, Blonde, Red, Grey or white, Dyed, No hair |
| Ethnic background | one choice | African, Arab, Chinese, East Asian, European, Indian, Latin American, Mixed, Other |
| Highest education | one choice | None, Secondary school, Vocational diploma, Bachelor's degree, Master's degree, Doctorate. Kept next to the existing free-text "What studies did you do?" |
| Relationship status | one choice | Single, Separated, Divorced, Widowed |
| Do you have tattoos? | yes / no | |
| Who do you live with? | one choice | Alone, With family, With flatmates, With my children |

Every one of these questions is optional. A question left blank never stops a profile from counting as finished.

**A finished profile** is one with a name, gender, date of birth and location. **(Claude's call.)** Only finished profiles appear in search results and set off notifications.

While adding these, fix the two answers whose form doesn't match their stored type: "Do you have any pets?" is a text box but stored as yes/no, so it becomes a checkbox.

## 2. Search profiles
- **Pages:** create (`/profile/search`, the "Add Search" button already on the matches page), edit (`/profile/search/{id}`) and delete (it asks first, NFR-22). The member's search profiles stay in the drop-down on the matches page.
- **Each search profile has a name** (required, so the drop-down can show it) and a setting for each profile question. **Any setting can be left on "I don't mind".**
- **Kinds of setting:**

| Kind | Used for | How the member sets it |
|---|---|---|
| Any of a list | gender, personality, religion, diet, cooking, chores, smoking, drinking, exercise, reply speed, wants kids, body type, skin colour, eye colour, hair colour, ethnic background, highest education, relationship status, living situation | ticks one or more choices |
| Shares at least one | interests, languages | ticks one or more |
| Range | age, height | a minimum and a maximum; either end may be empty |
| Distance | location | a radius in km around **the member's own profile location** **(Claude's call)** |
| Yes / no | pets, tattoos, allergies or illness, disability, leaves people on read, has had partners, virgin, wants marriage, religion matters, already has children, willing to relocate | Yes, No, or I don't mind |

  For allergies and disability the profile holds free text, so "Yes" means the text is filled in.
- **Ownership:** every search profile belongs to one member (a new `UserID` column). Every page and action checks on the server that the signed-in member owns it (NFR-3).

## 3. Match %
The owner's rule: **every setting counts the same, and the % is the settings the other person matches divided by all the settings.**
- Only settings that are not "I don't mind" are counted. **(Claude's call, shown to the owner and not objected to.)** If a search profile has no settings at all, everyone scores 100%.
- If the other person left that question blank, the setting counts as **not matched**. **(Claude's call.)**
- Gender and age count like every other setting. They don't hide anyone: a person of another gender can still appear, lower down. **(Claude's call, shown to the owner and not objected to.)**
- "Shares at least one" counts as one setting, however many interests or languages are ticked. **(Claude's call.)**
- Distance needs both members to have a location; if either is missing, it is not matched.
- The % is rounded down to a whole number: 7 out of 9 is 77%, not 78%, so 80% always means at least 80%.
- **Results:** finished profiles only, never the member themselves, highest % first. Equal scores show the most recently joined first. 20 a page, with Next and Previous (NFR-10). Each card shows the photo, name, age, gender and the real %, and links to the profile.
- **Where the score is worked out (Claude's call):** in Java, in one method that takes a search profile and a profile and returns the %. The matches page and the notifications both use it, so the rule exists in one place and can be unit-tested without a database. The candidates are loaded in a few bulk queries (profiles, then all their interests and languages), never one query per member. This replaces `CALL MatchUsers`. It is fine for thousands of members; if the site grows far beyond that, move the scoring into SQL.

## 4. New-match notifications
- **When:** a member's profile becomes finished for the first time, and each time a finished profile is saved again. **(Claude's call that edits count too.)** Creating or editing a *search profile* does not send notifications; the member sees current matches on the matches page instead.
- **Who is told:** the owner of every search profile, other than the person's own, that the person matches at 80% or more.
- **Never twice:** each member is told only once about the same person for the same search profile, even if the person's score later drops below 80% and rises again. **(Claude's call.)**
- **On the site:** a bell in the header with the number of unread notifications, a notifications page listing them newest first, and live updates through the WebSocket connection the chat already uses. Opening the notifications page marks them as read. The text reads: *"Someone new matches your search ‹search name› at 86%"*, and it links to that person's profile.
- **By email:** the same sentence and link, sent to the member's account email by the mail sender the app already has (`spring-boot-starter-mail`; no new library). One email for each notification. **(Claude's call; if emails pile up, sending a daily summary instead is a later idea.)**
- **Doesn't slow the save:** the check and the emails run in the background after the profile is saved. If an email fails, the site notification is still kept and the failure is logged without the address or the message text (NFR-7, NFR-21).

## 5. Database changes
Changing the schema needs the owner's approval (`CLAUDE.md`, Ask first). **Approving this design approves the changes below, and nothing else.**
- **New columns on `profile`:** one for each new question in section 1.
- **`search_profile`:** a new `UserID` column (the owner), the new settings (minimum and maximum height), and Yes / No / I don't mind as a nullable yes/no column. Columns that no longer fit are removed: the single-value text columns that become "any of a list", plus `Gender`.
- **New table `search_profile_choice`**: (search profile, setting, value), one row for each choice ticked in an "any of a list" or "shares at least one" setting.
- **New table `notification`**: id, member, text, link, when it was created, when it was read, the search profile and the matched member. The pair (search profile, matched member) is unique, which is what keeps notifications from repeating.
- **Tables the code uses but nothing creates:** `profile_to_interest`, `profile_to_language`.
- **Views and procedures that are missing:** `profile_view`, `FetchUserdata`, `chatlist_item`, `message_view`, `Message_Reaction_View`. Copy their definitions from the old `my_dating_site` database if it can still be started. If not, write them again from the columns the Java code reads. `MatchUsers` is no longer needed (section 3).
- Hibernate adds tables and columns for the entities by itself. Everything it can't create (the views, the procedure, the non-entity tables, removed columns and unique keys) goes in **numbered scripts in `db/`** (NFR-19), run in order on an empty database.

## Left to the builder
- Class, method and page layout, following `NAMING.md` and the look of the existing pages.
- How the form shows the "any of" choices; the existing tag-picker in `search.html` is a good start.
- Unit tests for the scoring method that cover every kind of setting, "I don't mind", a blank answer and the 80% boundary. Tests that a member cannot open, edit or delete another member's search profile. A test that the same person is never notified twice.
- Fixing the null checks in the setters (`if (!val.isEmpty())` fails when nothing is sent) on the forms being touched.
