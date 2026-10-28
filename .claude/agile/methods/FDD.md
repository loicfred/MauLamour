# Feature-Driven Development (FDD)
Read [`../AGILE.md`](../AGILE.md) first; it applies to every method. This file adds what is specific to FDD.

Plan and build by small, client-visible features. First a model of the whole system, then a list of features, then a short design-and-build cycle for each feature.

## Use it when
The system is large, has many features, and a shared model of it matters (an ERP with many modules, for example).

## The five steps
1. **Develop an overall model:** the main classes and how they connect. → the design documents and diagrams.
2. **Build a features list:** each feature is a small action, written `<action> the <result> for a <person>` (for example "calculate the total of an invoice"). Group them into feature sets. → `REQUIREMENTS.md` (Features).
3. **Plan by feature:** order the feature sets and give each an owner and a date. → `TASKLIST.md` (Dates and sprints).
4. **Design by feature:** a short design and review for the features of the next batch.
5. **Build by feature:** code, test, review, then add to the main build.

A feature should take no more than about two weeks; if it is longer, split it.

## Roles
| Role | On a solo project |
|---|---|
| Chief programmer | The owner, who owns design and code quality |
| Class owners | The owner, and AI assistants working for them |
| Domain expert | The client or sponsor |

## Tracking
Report progress as the share of features at each stage: domain walkthrough, design, design review, code, code review, promoted to the build. Use the board's status field for these stages.

## What the assistant does
- Works on one feature at a time and names it with its feature code (`FR-n`).
- Reviews its own design before coding and its code before saying done.
- Keeps the model, the feature list and the board the same.
- Puts unfinished work in `TASKLIST.md` straight away and writes a `work/DONE/` entry at the end.

## Common mistakes
- Features so large they cannot be finished in two weeks.
- A model that is drawn once and never updated.
