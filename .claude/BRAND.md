# Brand

How the product looks and talks. The code copies its colours, fonts and wording from here. If something new is shown on screen, add it here first.

## Name
- **Name:** {{...}}. Why this name, and how to write it (capitals, spaces).
- **Slogan:** {{five to eight words}}
- **Short description:** {{two sentences for the target audience}}
- **Three words that describe it:** {{e.g. calm · clear · friendly}}

## Tone
| Do | Don't |
|---|---|
| {{Simple words, short sentences}} | {{Technical words, internal codes}} |
| {{Talk to the user as "you"}} | {{Vague messages like "an error occurred"}} |

Words to use: {{...}} · Words to avoid: {{...}}

## Colours
Give each colour a name once, then use only the name in the code.

| Name | Light mode | Dark mode | Used for |
|---|---|---|---|
| `--color-primary` | {{#......}} | {{#......}} | Main buttons, links |
| `--color-secondary` | | | Highlights |
| `--color-bg` | | | Page background |
| `--color-surface` | | | Cards, panels |
| `--color-text` | | | Normal text |
| `--color-muted` | | | Less important text |
| `--color-success` | | | "Saved", "Done" |
| `--color-warning` | | | "Careful" |
| `--color-danger` | | | Errors, delete buttons |

Text must be easy to read against its background. Check the contrast with a free contrast checker.

## Fonts
| Used for | Font | Size / thickness |
|---|---|---|
| Titles | {{...}} | {{...}} |
| Normal text | {{...}} | {{16px / normal}} |
| Code and numbers | {{...}} | {{...}} |

## Logo
- Files: {{where the logo files are}}. Icon sizes: {{16, 32, 180, 512 px}}
- Keep space around it. Never stretch it, change its colours or add effects.

## Icons and images
- Icon set: {{e.g. Bootstrap Icons}}. Use only one set.
- Image style: {{photos, drawings...}}

## Writing
- **Buttons** say what will happen: "Save changes", not "OK".
- **Error messages** say what went wrong and how to fix it: "This email is already used. Log in instead?"
- **Empty pages** say what will appear there and how to start: "No invoices yet. Create your first one."
- Internal codes (FR-1, sprint names, ticket numbers) never appear on screen.
