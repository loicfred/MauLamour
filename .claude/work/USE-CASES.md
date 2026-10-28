# Use cases

A use case is one thing a user does with the system from start to finish, like "Sign up" or "Pay an invoice".

## Users
| User type | Who they are | What they want to do |
|---|---|---|
| {{Visitor}} | {{Someone not logged in}} | {{Look around, sign up}} |
| {{User}} | {{A logged-in customer}} | {{...}} |
| {{Admin}} | {{Runs the system}} | {{Manage accounts and settings}} |
| {{System}} | {{Things that happen on their own}} | {{Send reminders, make backups}} |

## List
| Code | Use case | Done by | Features |
|---|---|---|---|
| UC-1 | {{Sign up}} | {{Visitor}} | FR-1 |
| UC-2 | | | |

## Steps
Write the steps for the main use cases. Simple ones can stay in the list above.

### UC-1 — {{Sign up}}
- **Who:** {{Visitor}}
- **Before:** {{They are not logged in.}}
- **Starts when:** {{They click "Sign up".}}
- **Steps:**
  1. {{The visitor ...}}
  2. {{The system ...}}
- **If something goes wrong:**
  - 2a. {{The email is already used → the system says so and offers to log in.}}
- **After:** {{The account exists and the visitor is logged in.}}
- **Rules:** {{e.g. the password needs at least 8 characters}}

## Who can do what
**C**reate · **S**ee · **E**dit · **D**elete · **A**pprove · — nothing

| Thing ↓ / User → | {{Visitor}} | {{User}} | {{Admin}} |
|---|---|---|---|
| {{Account}} | C | S E (own only) | C S E D |
| {{...}} | | | |
