# Study

Why the project exists. Write this before the requirements: every feature in `work/REQUIREMENTS.md` should come from a problem or a goal written here.
This file is made to hold a lot: a full study, a literature review and a feasibility study all fit in it. Number every source as [1], [2]... in the **Sources** list at the end and cite the number in the text, so any claim can be traced. Keep a section even when the answer is short; delete a section only if it truly does not apply.

## What the project is
{{A paragraph or two: what it is, for whom, what it does, and which parts are in the first version and which are optional.}}

### Aim and objectives
The aim: {{one sentence}}
1. {{Objective 1, a thing you will do or produce}}
2. {{...}}

## People involved
| Who | Role | What they want | Contact |
|---|---|---|---|
| {{...}} | {{Owner / client / sponsor / regulator / user}} | {{...}} | {{...}} |

## Problem
### How it is done today, in the real world
{{Who does what now, which systems and laws are involved, how long it takes. Use facts and cite them [n].}}

### What is missing
- **{{Gap 1}}**: {{what is missing, and the evidence [n]}}
- **{{Gap 2}}**: {{...}}

### Problem statement
{{Two or three sentences: what goes wrong, for whom, and how often. What does it cost them in time or money?}}

### What software can and cannot fix
{{What is outside the project's reach (laws, other people's systems), and what the project does address.}}

## Target audience
- **Main users** (use it every day): {{...}}
- **Other users** (use it sometimes): {{...}}
- **Who pays or decides**: {{...}}
- **Who else is affected**: {{...}}
- Where and how they use it: {{phone or computer, at work or at home, internet quality, language, special needs}}

## Typical users
### {{Name or role}}, {{job}}
- **Wants to:** {{...}}
- **Is annoyed by:** {{...}}
- **Skills:** {{...}} · **Device:** {{...}}
- **In their words:** "{{...}}"

## How it is done today
1. {{Step of the current process, without your product.}}
2. {{...}}

## Similar products
| Product | Good at | Bad at | Price |
|---|---|---|---|
| {{...}} | | | |

**What yours does that they don't:** {{...}}

## Research notes
| Date | How (reading, interview, survey, watching...) | With whom or what | What we learned |
|---|---|---|---|
| {{...}} | | | |

## Goals
Each goal needs a number and a date, e.g. "Cut sign-up time to under 2 minutes by June".
1. {{...}}

## Measuring success
| What | Today | Target | How we measure it |
|---|---|---|---|
| {{...}} | | | |

## Feasibility
Can it be done? Answer each of the five questions with reasons.

### Operational: will people accept it?
{{Who resists change and why. Use the PIECES table to list what is wrong today and how the product answers it.}}

| PIECES | Today | The system |
|---|---|---|
| Performance | | |
| Information | | |
| Economy | | |
| Control | | |
| Efficiency | | |
| Service | | |

### Technical: can it be built?
{{The technology, whether it is mature and affordable, and what is deliberately left out (for example an outside API).}}

### Schedule: is there time?
{{Start date, deadline, and the order of work.}}

### Economic: is it worth it?
{{Costs, benefits and the payback. A cost-benefit table over 3 years is enough.}}

| | Year 0 | Year 1 | Year 2 | Year 3 |
|---|---|---|---|---|
| Costs | | | | |
| Benefits | | | | |
| **Net** | | | | |

### Legal: is it allowed?
{{The laws that apply, what they require, and how the project meets them.}}

## Method in brief
{{The Agile method and why (the reasoning is in `DECISIONS.md`, the rules in `agile/`), who plays which role, how testing is done, and the main components the work is split into.}}

## Money
- **Who pays, and for what:** {{subscription, one payment, free, saves the company money...}}
- **Costs:** {{hosting, licences, domain name, people}}
- **Budget:** {{...}}

## Limits and guesses
- **Guesses** (we think these are true but have not checked): {{e.g. users have a smartphone}}
- **Limits** (we cannot change these): {{deadline, budget, tools the client wants, team size}}
- **Outside things we depend on**: {{a payment service, another system, a supplier}}
- **Existing code, systems and data**: {{what it must connect to, replace or import, and in what format}}

## Personal data
| Data kept | Why | Kept for | Who can see it |
|---|---|---|---|
| {{Email}} | {{To log in}} | {{Until the account is deleted}} | {{The user, admins}} |

Law that applies: {{GDPR, Data Protection Act...}}

## Word list
Words from the users' world that everyone on the team must understand the same way.
| Word | Meaning |
|---|---|
| {{...}} | {{...}} |

## Can it be done?
The short answer, after the feasibility study.
| Question | Answer |
|---|---|
| Do we have the skills and tools? | |
| Is it worth the money? | |
| Will people actually use it? | |
| Can it be done in time? | |
| Is it legal and fair? | |

## Risks
| What could go wrong | Probability | Size of loss | Exposure | What we will do |
|---|---|---|---|---|
| {{...}} | {{20%}} | {{Low / Medium / High}} | {{Low / Medium / High}} | {{...}} |

Also note positive risks (things that could go better than planned) and review the list every sprint.

## Sources
Numbered, in the form the report needs (author, year, title, link, date accessed).
1. {{...}}
