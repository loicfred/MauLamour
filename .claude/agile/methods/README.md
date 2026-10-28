# Agile methods

One file for each way of working. The project picks **one** in `CLAUDE.md` (Configuration → Agile method), and the assistant follows that file. To change method, change the line in `CLAUDE.md`, move the `@agile/methods/<METHOD>.md` import to match, and record the change in `DECISIONS.md`.
What applies to every method (vision, roadmap, release planning, estimation, anti-patterns and the rules all methods share) is in [`../AGILE.md`](../AGILE.md). A method file only adds its own rhythm, roles and assistant rules.

| File | In one line | Rhythm |
|---|---|---|
| [`SCRUM.md`](SCRUM.md) | Fixed sprints, planning, review and retrospective | Sprint (1 to 4 weeks) |
| [`KANBAN.md`](KANBAN.md) | A board with a limit on work in progress; no sprints | Continuous |
| [`SCRUMBAN.md`](SCRUMBAN.md) | Scrum's reviews with Kanban's board; plan when the board runs low | Continuous, with a fixed review |
| [`XP.md`](XP.md) | Engineering habits: test first, small releases, refactoring | Iteration (1 to 2 weeks) |
| [`LEAN.md`](LEAN.md) | Build the smallest thing, measure, learn | Build – measure – learn cycles |
| [`FDD.md`](FDD.md) | Model first, then deliver small features one by one | Feature batches |

## Choosing
- A course or client that expects sprints and reviews → **Scrum**.
- Work that arrives unevenly, or a support-style project → **Kanban** or **Scrumban**.
- Code quality is the main risk → add the habits of **XP** to whichever you choose.
- The idea itself is unproven → **Lean**.
- A big system with many features → **FDD**.

## Every method shares these rules
- The board (named in `../../CLAUDE.md`) and `USER-STORIES.md` always match.
- Stories follow `USER-STORIES.md`: one feature each.
- Unfinished work is written in `TASKLIST.md` straight away.
- Every session ends with a `work/DONE/` entry.
