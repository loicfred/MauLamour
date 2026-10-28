# Naming

How to name things in this project. A good name tells the reader what something is **without opening it**. Long is fine, unclear is not.

Each time you rename something for one of these reasons, add a real before/after example to that rule's table. Real examples from this project teach better than made-up ones.

## A method is named after the job it does
Say what it does, not how it does it: `sendInvoiceReminder()`, not `process()`. Never use vague names like `handle()`, `doIt()`, `data()` or `id()`.

| Before | After | Where |
|---|---|---|
| | | |

## A variable says what it holds
Read on its own, without its type, the name should still make sense: `selectedPlugins`, not `selected`.

| Before | After | Where |
|---|---|---|
| | | |

## A yes/no value asks a question
Start it with `is`, `has` or `can`: `canWriteInto`, `isExpired`, `hasAccess`.

| Before | After | Where |
|---|---|---|
| | | |

## Use the word the language already uses
If the language or library already has a name for an idea, use it instead of inventing another one: `isNotEmpty`, not `holdsAnything`.

| Before | After | Where |
|---|---|---|
| | | |

## A method that creates something says so
`buildFrom(account)` or `createFor(user)`, not `ownedBy(account)`, which sounds like a question.

| Before | After | Where |
|---|---|---|
| | | |

## One idea, one name
If two things turn out to be the same idea, give them one name. If they are two forms of one idea, give them the same first word (`MessageLine`, `MessageBlock`).

| Before | After | Where |
|---|---|---|
| | | |

## Don't repeat what the context already says
Inside a list called `plugins`, each item is a `Plugin`, not a `PluginEntry`.

| Before | After | Where |
|---|---|---|
| | | |

## A file is named after what it holds
The file that defines `Installer` is `installer.*`, not `runner.*`.

| Before | After | Where |
|---|---|---|
| | | |

## Label steps everywhere they happen
When code follows fixed steps (pages of a wizard, steps of an install), mark each one with the same label, like `// Step 2 - Copy the files`, both where the step is called and where it is written. When a step is added, renumber every label in the same change.
