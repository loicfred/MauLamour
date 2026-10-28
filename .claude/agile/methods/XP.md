# Extreme Programming (XP)
Read [`../AGILE.md`](../AGILE.md) first; it applies to every method. This file adds what is specific to XP.

A set of engineering habits for changing software safely and often: small releases, tests first, constant clean-up, and close contact with the customer. It is often combined with Scrum's planning.

## Use it when
The requirements will change, the code must stay easy to change, and quality matters more than speed of typing. It fits a team of one or two people who write a lot of code.

## The habits
| Habit | What it means here |
|---|---|
| Small releases | Something that works is always ready to show. Merge small changes often. |
| Test first | Write a failing test for behaviour that could really break, then the code that passes it. No tests for simple getters. |
| Continuous integration | Build and test on every push. A red build is fixed before anything else. |
| Refactoring | Clean up as you go. Remove duplication, split long methods, rename unclear names (`NAMING.md`). |
| Simple design | Build what today's story needs. Nothing for "maybe later". |
| Collective code ownership | Anyone (or any assistant) may change any code, so the code must be readable. |
| Coding standard | The rules in `CLAUDE.md`, followed everywhere. |
| Pair programming | Two minds on one task. With AI assistants, one writes and the other reviews. |
| On-site customer | The client is asked early and often. Notes go to `MEETINGS.md`. |
| Planning game | The customer picks stories; the developer estimates them. |
| Sustainable pace | No all-nighters. Unfinished work is written down, not rushed. |

## The rhythm
- **Iteration (1 to 2 weeks):** plan the stories, build them with tests, deliver a release.
- **Daily:** stand-up (what is done, what is next, what blocks).
- **Iteration review:** show the release to the customer. → `MEETINGS.md`.

## What the assistant does
- Writes the test first when the behaviour could really break, and runs it before saying the work is done.
- Keeps each change small and commits often, with the feature code in the message.
- Refactors code it touches, but does not widen the change beyond the story.
- Puts unfinished work in `TASKLIST.md` straight away and writes a `work/DONE/` entry at the end.

## Common mistakes
- Skipping tests "just this once".
- Building for imagined future needs.
- Letting the build stay red.
