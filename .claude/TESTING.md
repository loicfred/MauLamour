# Testing

## Plan
| What to test | How (automatic test, by hand, with users) | Who |
|---|---|---|
| Match % rule, search form checks, ranking, notifications | Unit tests, no database (`src/test/java`): run the named classes, `mvnw.cmd -o test -Dtest=...` | The developer, on every change |
| Pages that don't bind a form (search, matches, notifications) | `TemplateRenderTest`: renders them through Thymeleaf with sample values | The developer, on every change |
| Profile form, profile page, chat, sign-up and the database views | By hand in the running app, after running `db/` (`db/README.md`) | The owner |

## Feature → test
Every feature in `REQUIREMENTS.md` should have at least one test.

| Feature | Test | Automatic? |
|---|---|---|
| FR-1 | `ProfileSettersTest`, `ProfileSaveTest`, `ProfileAnswersTest`; the form itself by hand | yes, form by hand |
| FR-2 | `SearchProfileFormTest`, `TemplateRenderTest.newSearchFormRenders` | yes |
| FR-3 | `SearchProfileFormTest.savingAgainClearsWhatWasEmptied`, `SearchProfileAccessTest`, `TemplateRenderTest.savedSearchFormKeepsWhatWasSaved` | yes |
| FR-4 | `SearchProfileAccessTest` (only the owner); the delete itself by hand | partly |
| FR-5 | `SearchProfileMatchTest`, `MatchRankingTest`, `TemplateRenderTest.matchesPageShowsRealPercent` | yes |
| FR-6 | `MatchNotifierTest`, `TemplateRenderTest.notificationsPage`; the live bell by hand | yes, live bell by hand |
| FR-7 | `MatchNotifierTest.emailLinksToTheProfile`, `mailFailureKeepsSiteNotification`; a real email by hand | yes, real email by hand |

## Test log
Every time a test is run by hand, and every time a test fails.

| Date | Test | Expected | Result | Fix |
|---|---|---|---|---|
| 2026-10-28 | The 10 unit and render test classes (55 tests) | All pass | pass | Each was seen failing first, or was broken on purpose to prove it can fail |
| 2026-10-28 | After the review fixes: 12 classes (60 tests), adding `TransactionalUsageTest` and `ProfileBindingTest` | All pass | pass | Each new test seen failing first |
| 2026-10-28 | `MyDatingSiteApplicationTests.contextLoads` | The app context starts | pass | It connected to the remote `maulamour` server in `.env`, and Hibernate added the new tables and columns there |
| 2026-10-28 | `mvnw.cmd -o clean package -DskipTests` | Builds | pass | — |
| 2026-10-28 | The `db/` scripts and the app run by hand | Pages work end to end | not run | Waiting for the owner to run `db/V1`–`V4` on the server (`TASKLIST.md`) |
