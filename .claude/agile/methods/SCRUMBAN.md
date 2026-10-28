# Scrumban
Read [`../AGILE.md`](../AGILE.md) first; it applies to every method. This file adds what is specific to Scrumban.

Scrum's planning rhythm with Kanban's board discipline. Work is planned in short cycles, but the team pulls cards one at a time and limits work in progress.

## Use it when
Planned feature work and unplanned work (fixes, requests) both matter, so a strict sprint is too rigid and pure Kanban is too loose.

## How it works
- **Planning on demand:** plan when the *Ready* column runs low, not on a fixed day. Pick the next most important stories.
- **Cadence:** a review and a retrospective at a fixed interval set in `CLAUDE.md` (Configuration), for example every two weeks.
- **Board:** Backlog → Ready → In progress → To verify → Done, with a WIP limit on *In progress*.
- **No sprint commitment:** the team promises only to finish what it started, not a fixed set of stories.
- **Estimates:** points are optional. If used, they are for the review only.

## The rhythm
1. **Daily check:** right to left across the board.
2. **Plan when needed:** when Ready is under half full, the owner tops it up from the backlog.
3. **Review and retrospective** on the fixed interval. → `MEETINGS.md`, `work/DONE/`.

## What the assistant does
- Pulls only cards in Ready or In progress. Asks before starting anything else.
- Respects the WIP limit.
- Keeps the board and `USER-STORIES.md` the same; puts unfinished work in `TASKLIST.md` straight away.
- Ends every session with a `work/DONE/` entry naming the cards that moved.

## Common mistakes
- Keeping sprint ceremonies but ignoring the WIP limit.
- Never holding the review because "there is no sprint end".
