#!/bin/bash
# PreToolUse(Bash|PowerShell): refuse to let the shell rewrite a file in place.
#
# Taken from SolarERP, where the prose rule was read and broken anyway (sed duplicated a declaration,
# dropped a line and deleted a section). A hook does not compete for attention the way prose does.
# Edit refuses a non-exact match where sed writes nonsense in silence, and the user watches for the
# Update(<file>) card, which the shell never shows.
#
# Reads are untouched: sed without -i, grep, cat, find, git, mvnw all still run.

payload=$(cat)

# permissionDecision "deny" stops the call and hands the reason back as the tool result.
refuse() {
    printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"%s"}}\n' "$1"
    exit 0
}

# sed -i in any spelling or flag order: -i, -i.bak, -ri, -n -i, --in-place
if grep -Eq '\bsed\b[^|;&]*(-[A-Za-z]*i[A-Za-z]*([[:space:]]|\.|$)|--in-place)' <<<"$payload"; then
    refuse "Blocked: sed -i rewrites the file in place. Use the Edit tool - it refuses a non-exact match, where sed silently duplicates or drops lines, and the user watches for the Update(<file>) card. A multi-file rename is that many Edit calls, not one sed."
fi

# perl -i / -pi / -i -pe
if grep -Eq '\bperl\b[^|;&]*-[A-Za-z]*i' <<<"$payload"; then
    refuse "Blocked: perl -i rewrites the file in place. Use the Edit tool instead."
fi

# > file, >> file, tee file, and PowerShell's Set-Content/Add-Content/Out-File - only into a file this
# project keeps source or prose in, so redirects to /dev/null, $null, a log or the scratchpad are left alone.
SOURCE='(java|html|css|js|json|xml|properties|md|yml|yaml|sql|sh|cmd)'
if grep -Eiq '((>>?|[[:space:]]tee([[:space:]]+-a)?)[[:space:]]*|(Set-Content|Add-Content|Out-File)[^|;]*)[^[:space:]<>|&"]*\.'"$SOURCE"'([[:space:]]|"|$)' <<<"$payload"; then
    refuse "Blocked: this writes shell output into a source or documentation file. Use the Write tool for a new file, Edit for a change to an existing one - a worklog entry is a new file under .claude/work/DONE/, so Write it."
fi

exit 0
