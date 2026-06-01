# Findings Backlog

## PR #19 — UI-redesign: Editorial Ledger-stijl (dashboard + app-breed) (2026-06-01)

### code-simplifier / standards-reviewer (1)
| Score | File | Finding | Suggestion |
|-------|------|---------|------------|
| 50 | frontend/src/app/features/dashboard/dashboard.component.scss:1-7,101-124 | Dashboard SCSS re-declares 6 global ledger tokens (`--paper/--paper-2/--ink/--ink-soft/--vermilion/--rule`) already on `:root` in styles.scss, and re-implements `.rule`/`.rule--double` locally with 1-2px drifts from the shared `.ledger-rule`/`.ledger-rule--double` utilities used by all other screens | Drop the local token block (inherit from `:root`) and replace `.rule`/`.rule--double` markup with the global `.ledger-rule` classes; keep only genuinely dashboard-specific overrides (larger title clamp, action button) |
