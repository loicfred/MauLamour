# Agile: instructions for every method

These apply whichever method the project uses (`CLAUDE.md`, Configuration → Agile method). The method's own file in `methods/` (`SCRUM.md`, `KANBAN.md`, `SCRUMBAN.md`, `XP.md`, `LEAN.md`, `FDD.md`) adds the rhythm, the roles and what the assistant does on top of this. `methods/README.md` helps choose one.

## Rules every method shares
- The board (named in `CLAUDE.md`) and `USER-STORIES.md` always match. Change both in the same session.
- **A user story is one feature**, written `As a <actor>, I must be able to <one feature> so that <benefit>`. A group of features is never one story: if it joins two actions with "and" or "or", split it. The full rules, INVEST and acceptance criteria are in `work/USER-STORIES.md`.
- Unfinished work is written in `TASKLIST.md` straight away, never only in the chat.
- Every session ends with a `work/DONE/` entry.
- Welcome change, but record it: a changed plan and its reason go in `DECISIONS.md`.
- Never write the outcome of a review or a test before it happens.
- A story is done only when it is **developed, tested, integrated and documented** (the full definition is in `methods/SCRUM.md`). Whatever the method, only work that is done is shown in a review or demo, and a demo is never rigged.

## 1. Product vision
The vision says where the product is going and why, in a few lines everyone can remember. Write it before the roadmap. The Product Vision Board (Roman Pichler) is a one-page way to do it.

| Box | Question |
|---|---|
| **Vision** | What is the purpose of the product? What positive change will it bring? |
| **Target group** | Which market or type of user is it for? Who are the customers and users? |
| **Needs** | What problem does it solve? What benefit does it give? |
| **Product** | What is it? What makes it stand out? Is it feasible to build? |
| **Business goals** | How does it help the organisation? How will we measure success? |

Fill it from left to right: target group and needs first, then the product. Test each box with real users.
- The one-sentence vision goes in `CLAUDE.md` (Configuration → What it does) and `README.md`.
- The target group, needs and goals come from `STUDY.md`; link to it, do not write them twice.
- Check: can a new team member say what the product is for after one minute? Does every feature in `REQUIREMENTS.md` serve the vision or a goal?

## 2. Product roadmap
The roadmap shows how the product grows towards the vision. It is a plan of **goals**, not a list of features with dates. It lives in `TASKLIST.md` (Dates and sprints) or on the board. Source: Roman Pichler, *10 Tips for Creating an Agile Product Roadmap* (romanpichler.com/blog/10-tips-creating-agile-product-roadmap/).

One row (the GO roadmap): **date or period · name · goal · features (3 to 5 per goal) · metric**.

1. **Focus on goals and outcomes.** A goal is a benefit, not a feature.
2. **Do the prep work.** Have the product strategy first (the vision above, and `STUDY.md`).
3. **Tell a coherent story.** Order the goals so each builds on the one before.
4. **Keep it simple.** Coarse features only; no epics or stories on the roadmap.
5. **Get strong buy-in.** Build it with the stakeholders, for example in a workshop.
6. **Have the courage to say no.** Keep only the most crucial features; the rest goes to `IDEAS.md`.
7. **Know when to show dates.** Dates are for the internal roadmap. Show customers the order only (now, next, later).
8. **Make it measurable.** Every goal has a target and a metric.
9. **Work out cost top-down.** Estimate the people and time for the whole roadmap; do not add up detailed estimates.
10. **Review and adjust regularly.** Every 4 weeks to 3 months, depending on how mature the product is.

The assistant does not add features to the roadmap on its own; it proposes them in `IDEAS.md`.

## 3. Release planning
After the vision and the roadmap, break the requirements into stories, group them into releases, and plan the sprints.

### The levels of requirement
Use only the levels you need. The sizes are guides; add detail step by step, not all at once.

| Level | What it is | Size guide |
|---|---|---|
| **Theme** | A logical group of features at the highest level | — |
| **Feature** | A part of the product that gives the customer a new capability | Under 144 story points |
| **Epic** | A medium-sized requirement cut from a feature; may hold several actions | Under 34 points once planned into a release |
| **User story** | One action or one integration, small enough to start building | 8 points or fewer in a sprint |
| **Task** | A step needed to build a story | Estimated in hours; about a day |

On the board a theme or epic is an `Epic`, a story is a `User Story`, and a task is a `Task`.

### Creating stories
1. List the **end users** and, for each, the pain or need you are solving.
2. List the **actions** they may want to take.
3. Ask what **value** each action brings to the user and to the product, and whether anyone would pay for it.
4. Write **one story per action** with its acceptance criteria (`USER-STORIES.md`).
5. Estimate value, risk and effort, then order the backlog (section 4).

### Releases and sprint planning
- Sort the ordered stories into increments, each a version the customer can use. Each release serves one roadmap goal; write its goal in one sentence.
- **Sprint planning:** set the sprint's **goal**; choose the stories that support it (write new ones if needed); build the **sprint backlog** (stories in priority order with effort, value and risk, then the tasks for each with an hours estimate under a day).
- Plan **one sprint at a time**; do not plan later sprints in detail.
- Work on one story at a time until it is done, with everyone on it (swarming).
- If it does not all fit, take stories out. Time is fixed; scope moves.

## 4. Value, risk, effort and priority
### Value and risk
Rate every story on both, low, medium or high, with one sentence saying why. Value is the benefit of having the feature (the return on investment, if it can be counted). Risk is the harm to the project if the feature fails or is missing (its cost, if it can be counted).
- **High value and high risk go first.** High value raises customer satisfaction; risky work should fail early and cheaply, not late.
- The result is the first version of the product backlog.

### Prioritising
- **100 dollar method:** each stakeholder splits 100 dollars across the features; add the amounts up.
- **Value-risk method:** score each feature out of 5 for its benefit, and out of 5 for the penalty if it is missing or broken; the sum is the priority, and the higher sum goes first.
- **Kano method:** sort each feature as *must-be* (expected; missing it upsets users), *performance* (the better, the happier), *delighter* (unexpected and loved) or *indifferent* (no effect on satisfaction; **do not build it**).

### Effort
Traditional estimates look exact but are not. Agile uses **relative** estimates in **story points** on the Fibonacci scale.

| Size | Points |
|---|---|
| Extra small | 1 |
| Small | 2 |
| Medium | 3 |
| Large | 5 |
| Extra large | 8 |
| Bigger (probably an epic; split it) | 13, 20, 50, 100 |

The team agrees what a feature of each size looks like, so a 5 always means the same thing.
- **Estimation poker:** agree one **anchor story** worth 5 points. Take a high-priority story; everyone shows an estimate at the same moment; if they differ, discuss the assumptions and repeat; if there is still no agreement, the scrum master decides. With many stories, group them by category and estimate the category. Working alone, estimate against the anchor and again a day later.
- **T-shirt sizes:** agree an M story, then place the others as XS, S, M, L or XL.
- **Bucket estimating:** buckets of 1, 2, 3, 4, 5, 8, 13, 20, 50, 100, 200. Place one story in 8, place each next story relative to those already placed, move the first if needed, then sanity-check.

## 5. Anti-patterns to avoid
This list is general, written from the project's own rules and common experience. **It has not been checked against the DZone article "How to Avoid 8 Common Agile Anti-Patterns Hurting Your Team": that page could not be read (access error 403). Paste the article to align the list with it.**

| Anti-pattern | What it looks like | Instead |
|---|---|---|
| **Stories that are really groups** | "Create an account and log in" as one story | One action per story |
| **Half-done work counted as done** | "90% finished" in the review | Done means the checks pass and the board says Done |
| **Planning every sprint up front** | A detailed plan for sprints far away | Plan one sprint at a time; keep the roadmap coarse |
| **Skipping review and retrospective** | "Nothing changed this sprint" | Hold both; record them in `MEETINGS.md` and `work/DONE/` |
| **Adding work mid-sprint without removing any** | The sprint grows and nothing is dropped | Time is fixed: take something out, or start it next sprint |
| **A roadmap of features and dates** | Promises with dates, no goals | Goals and metrics; dates only on the internal roadmap |
| **Estimates used as promises** | Points read as hours, pressure to "go faster" | Points are relative, for planning only |
| **Board and documents out of step** | The board says one thing, `USER-STORIES.md` another | Change both in the same session |
