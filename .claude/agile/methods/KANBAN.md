# Kanban
Read [`../AGILE.md`](../AGILE.md) first; it applies to every method. This file adds what is specific to Kanban.

Work flows continuously across a board. There are no sprints. The team limits how much is in progress at once, so work is finished before more is started.

## Use it when
Work arrives unevenly (support, fixes, small requests), priorities change often, or a fixed sprint rhythm would only get in the way.

## Roles
No fixed roles. Someone owns the priority order of the backlog (the client or the owner), and everyone moves their own cards.

## The board
Columns, left to right: **Backlog → Ready → In progress → To verify → Done**. Rename them in the board settings if the team needs different steps.
- **Work-in-progress (WIP) limit:** the most cards allowed in *In progress* at one time. The limit is set in `CLAUDE.md` (Configuration). When the column is full, finish or unblock a card before pulling a new one.
- **Pull, don't push:** take the next card from Ready only when there is room.
- Every card is one story, task or bug. Stories follow `USER-STORIES.md`.

## The rhythm
- **Daily check:** look at the board from right to left. What is stuck? What can be finished today?
- **Replenishment (weekly or when Ready runs low):** the owner moves the most important backlog cards to Ready.
- **Review (every two weeks or so):** show what reached Done. → `MEETINGS.md`.
- **Retrospective (monthly):** look at the numbers below and change one thing. → a `work/DONE/` entry.

## Numbers worth watching
- **Lead time:** days from a card entering Backlog to Done.
- **Cycle time:** days from In progress to Done.
- **Throughput:** cards finished per week.
Read them from the board; do not estimate points unless the team wants to.

## What the assistant does
- Before work: take only cards in Ready or In progress. Ask before starting an item that is not on the board.
- Never push the WIP limit. If it is full, finish or report a blocker.
- Keep the board and `USER-STORIES.md` the same. Put unfinished work in `TASKLIST.md` straight away.
- End every session with a `work/DONE/` entry that names the cards that moved.

## Common mistakes
- A WIP limit nobody enforces.
- Cards that sit in *To verify* for weeks.
- Adding columns for every small step until the board is unreadable.
