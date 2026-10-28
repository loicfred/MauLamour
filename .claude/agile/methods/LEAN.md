# Lean
Read [`../AGILE.md`](../AGILE.md) first; it applies to every method. This file adds what is specific to Lean.

Deliver value fast by cutting waste. Build the smallest thing that answers a real question, learn from real users, then decide what to build next.

## Use it when
The idea is uncertain (a new product, an unproven market) and the biggest risk is building something nobody wants.

## The principles
1. **Cut waste:** unused features, half-finished work, waiting, handoffs, rework.
2. **Amplify learning:** short experiments, and real user feedback written down.
3. **Decide late, deliver early:** do not lock in choices before you must (`DECISIONS.md`).
4. **Build quality in:** tests and checks as you go, not at the end.
5. **Respect the people:** a pace the team can keep.
6. **See the whole:** look at the whole flow from idea to user, not one part.

## The rhythm: build – measure – learn
1. **Hypothesis:** write the assumption and how it will be tested. → `IDEAS.md` or `STUDY.md` (Limits and guesses).
2. **Build the smallest version** that tests it (a minimum viable product, or even a mock-up).
3. **Measure** with the numbers chosen in `STUDY.md` (Measuring success).
4. **Learn and decide:** keep, change or drop. Write the decision in `DECISIONS.md`.
Repeat in cycles set in `CLAUDE.md` (Configuration).

## Board
Backlog → Doing → Testing with users → Done. Work in small batches; a card that cannot be finished in a few days is cut smaller.

## What the assistant does
- Asks what the smallest version is before building. Does not add features "for later".
- Records every assumption it makes as a guess in `STUDY.md`.
- Writes measured results and decisions down, not only in the chat.
- Ends every session with a `work/DONE/` entry.

## Common mistakes
- Building the whole product before showing anyone.
- Measuring nothing, so nothing is learned.
