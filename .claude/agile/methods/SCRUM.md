# Scrum
Read [`../AGILE.md`](../AGILE.md) first; it applies to every method. This file adds what is specific to Scrum.

Work in short, fixed-length sprints. At the start of each sprint the team picks a small set of stories, and at the end it shows working software and looks back at how it worked.

## Use it when
The work can be cut into stories, someone (a client, a sponsor, a product owner) can say what matters most, and a fixed rhythm helps. It is the default for this template.

## Roles
| Role | Job | On a solo project |
|---|---|---|
| Product owner | Decides what is built and in which order (the backlog) | The client or sponsor decides what matters; the owner keeps the backlog |
| Scrum master | Keeps the method running and removes blockers | The owner |
| Developers | Build and test the stories | The owner, and AI assistants working for them |

## The rhythm
The sprint length is set in `CLAUDE.md` (Configuration). All of these happen every sprint:
1. **Sprint planning** (start): pick stories from the top of the backlog that fit the sprint. Give each a point estimate. Write the sprint goal in one sentence. → `TASKLIST.md` (Dates and sprints) and the board's Sprints field.
2. **Daily check** (every work day, or every session when working alone): what was done, what is next, what is blocking. Blockers go to `TASKLIST.md` (Open work).
3. **Sprint review** (end): show what works to the client or sponsor. Compare it with the plan. → `MEETINGS.md`.
4. **Retrospective** (end): what to keep, stop and try. → a `work/DONE/` entry.

## Where things live
- **Product backlog:** every story and task on the board, in priority order. `USER-STORIES.md` mirrors the stories.
- **Sprint backlog:** the board items whose Sprints field is the current sprint.
- **Increment:** the working software at the end of the sprint. If a story is not finished, it is not counted; it moves to the next sprint and keeps the sprint it started in.
- **Velocity:** points finished per sprint, taken from the board.

## Definition of done
The team agrees one definition and checks stories against it all through the sprint, not only at the end, so the review needs little preparation. A story is done only when it is:
- **Developed:** it works from the screen a real user would use, and the code is pushed.
- **Tested:** its checks in `USER-STORIES.md` pass, and a test covers what could really break.
- **Integrated:** it works together with the rest of the product, not on its own.
- **Documented:** the manual, the requirements and the worklog match what was built.
The board state is then Done. Write your team's version in `DECISIONS.md` if it differs. Only work that meets this is shown at the review: work not delivered has no business value.

## Sprint review: how to carry it out
Held at the end of every sprint. Its aim is to demonstrate what was built for the stories in the sprint backlog, collect feedback, and update the product. The product owner, the developers, the scrum master and any stakeholders attend. Keep it short: about 30 minutes, and never more than an hour for a one-week sprint.

**Prepare it like a performance,** even if the meeting is informal: an introduction, a body and a conclusion.
- Pick the flow of features and use **realistic data**. Use the business's words, and set the speed to the audience.
- Use equipment as close as possible to the real one (a phone on a screen, not an emulator).
- **Do not rig the demo.** Show real progress; a faked demo creates work for later sprints to catch up on what the users think already exists.
- **Avoid gold plating:** no cool but unwanted features. Build what passes the user's acceptance test.
- Show real progress and recognise the work done; it keeps morale up.

**The review should contain, in this order:**
1. Greeting and introduction of the team.
2. A short presentation of the sprint goal.
3. A quick recap of the user stories chosen for the sprint.
4. A **live demonstration** of the completed stories.
5. At least two views of the **board** (for example the sprint board and the whole backlog) to show progress.
6. The **burndown chart** and what it means (see below).
7. The **risks** met during the sprint and how they were handled.
8. What comes next: the goal of the following sprint and the stories chosen for it.
9. A request for feedback.

**Burndown chart.** It tracks progress by plotting story points still to do against time (days or weeks), rather than a Gantt chart. The points finished per unit of time are the team's **velocity**; use the real velocity to work out the time left. Interpret the shape (flat, too steep, above the ideal line) in words at the review.

**Gathering feedback.**
- Do not defend the product against suggestions. Feedback improves it.
- Comments on existing features are recorded and done before the next review.
- New stories go to the product backlog and are prioritised. Unfinished stories go back to the backlog and their priority is reviewed.
- A talk about what comes next may follow.
- Backlog updates are finished before the next sprint planning.
- Note the meeting in `MEETINGS.md`; the outcome is written only after the review has happened.

## What the assistant does
- Before work: read the sprint goal and the current sprint's items from the board. Do not start an item outside the sprint without asking.
- During work: keep the board and `USER-STORIES.md` the same; put unfinished work in `TASKLIST.md` straight away.
- At the end of a session: write the worklog entry, and say which sprint items moved.
- Never write a sprint review's outcome before the review happens.

## Board setup
Fields (YouTrack names; other boards have the same fields under similar names): State (Open, In Progress, To Verify, Done), Sprints, Type (Epic, User Story, Task, Bug), Story points. An item stays filed under the sprint it was first planned for; if it is carried over, add the new sprint, never remove the old one.

## Common mistakes
- Adding stories in the middle of a sprint without removing others.
- Counting half-done stories as done.
- Skipping the review because "nothing changed".
- Stories that are really groups of features (see `USER-STORIES.md`).
