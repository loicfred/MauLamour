# Requirements

The list of functional (FR) and non-functional (NFR) requirements, and nothing else. What is still to do and the plan are in `TASKLIST.md`; the users are in `USE-CASES.md`; the scope, what is in and what is out, is in `CLAUDE.md`. Update this file when a requirement is added or changes.

## Features (FR)
What the system does. Each line must be something you can test.
Priority: **M**ust have, **S**hould have, **C**ould have, **W**on't have this time.

| Code | User | The system lets them... | Priority | Use case |
|---|---|---|---|---|
| FR-1 | Member | answer the extra profile questions: height, body type, skin colour, eye colour, hair colour, ethnic background, tattoos, highest education, living situation and relationship status | M | — |
| FR-2 | Member | create a search profile describing someone they are looking for | M | — |
| FR-3 | Member | edit one of their search profiles | M | — |
| FR-4 | Member | delete one of their search profiles | M | — |
| FR-5 | Member | see other members ranked by match %, highest first, for one of their search profiles | M | — |
| FR-6 | Member | get a notification on the site when someone new matches one of their search profiles at 80% or more | M | — |
| FR-7 | Member | get the same new-match notification by email | M | — |

## Qualities (NFR)
How well the system does it. The qualities below apply to every application and are in force from the first line of code. Change a number to fit the project. Remove a line only if it cannot apply (for example, no network or no stored data), and write why in `DECISIONS.md`. Lines in `{{...}}` are for this project to fill in. Add the project's own qualities at the end.
*(if it has one)* means the line applies only when the app has that part.

| Code | Type | Requirement | How we check it |
|---|---|---|---|
| NFR-1 | Security | Passwords are stored only as a slow, salted hash (Argon2, bcrypt or scrypt), never as plain text or reversible encryption. | Look at the stored data; code review |
| NFR-2 | Security | All input from users, files and other systems is checked on the server before use. Database queries take values as parameters, never joined into the query text. | Tests with wrong, empty, too long and malicious input |
| NFR-3 | Security | Each action checks on the server that the signed-in user may do it, following "Who can do what" in `USE-CASES.md`. Hiding a button is not a check. | A test for each action as each type of user |
| NFR-4 | Security | Secrets (passwords, keys, tokens) are never in the code or the repository; they are read from `.env` or a secrets store. | A secret scan of the repository |
| NFR-5 | Security | Data sent over a network uses HTTPS or TLS only. *(if it has one)* | Try plain HTTP; check the certificate |
| NFR-6 | Security | No dependency has a known high or critical vulnerability. | A dependency scan on every build |
| NFR-7 | Security | Error pages, messages and logs never show passwords, tokens, personal data or stack traces to users. | Force errors; read the screen and the logs |
| NFR-8 | Privacy | Personal data is kept only for the reasons and time written in `STUDY.md` (Personal data), and a user can see, export and delete their own. *(if it keeps personal data)* | Walk through each step as a user |
| NFR-9 | Performance | A common action answers in under 1 second and a screen opens in under 2 seconds, on the oldest device in "Runs on" (`CLAUDE.md`). | Timed with sample data at full size |
| NFR-10 | Performance | No database query runs once per row in a loop. A list longer than 100 items is paged or loaded as the user scrolls. | Code review; read the query log |
| NFR-11 | Performance | Files, connections and memory are released when no longer needed; memory use does not keep growing. | Run under load for an hour and watch memory |
| NFR-12 | Capacity | {{Handles 1,000 users and 1 million records}} | {{Load test with sample data}} |
| NFR-13 | Reliability | An error in one action never stops the whole app. The user sees what went wrong and what to do next. | Tests that force errors (no network, full disk, bad data) |
| NFR-14 | Reliability | Changes that belong together are saved together or not at all. | A test that fails halfway through a save |
| NFR-15 | Reliability | Data is backed up automatically, and restoring a backup has been tested before each release. *(if it stores data)* | Restore a backup to a copy and open it |
| NFR-16 | Reliability | {{Down no more than 1 hour a month; at most 1 day of data lost after a failure}} | {{Uptime log; a restore from backup tested}} |
| NFR-17 | Maintainability | The code follows `CLAUDE.md` (Writing code) and `NAMING.md`, and the formatter and checker pass with no warnings. | The automatic build |
| NFR-18 | Maintainability | Every feature (FR) has at least one automated test, and all tests pass before a change is merged. | The automatic build; `TESTING.md` (Feature → test) |
| NFR-19 | Maintainability | Dependencies are fixed to exact versions, and every database change is a numbered migration script, never made by hand. | Read the build file and the migrations folder |
| NFR-20 | Maintainability | A new developer can build, test and run the project from `README.md` in under 30 minutes. | Try it on a clean machine |
| NFR-21 | Observability | Every error is logged with the time, where it happened and enough detail to repeat it, but no personal data or secrets. | Force an error and read the log |
| NFR-22 | Usability | Every error message says how to fix the problem. Anything slower than 1 second shows that it is working. Deleting or other actions that cannot be reversed ask first or can be undone. | Read every message; try each slow and destructive action |
| NFR-23 | Accessibility | Meets WCAG 2.2 level AA: usable with a keyboard alone and a screen reader, text contrast at least 4.5:1, nothing relies on colour alone. *(if it has a screen)* | An automatic accessibility check, then a keyboard-only walk-through |
| NFR-24 | Compatibility | Works on everything listed in "Runs on" (`CLAUDE.md`). | Test the main use cases on each one |
| NFR-25 | | | |
