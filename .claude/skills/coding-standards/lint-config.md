# Severity Levels

Every rule in this standard carries a severity. Use these consistently when
reporting (e.g. during `/lint`) and when deciding whether to push back on a diff.

| Level | Meaning | Action |
|---|---|---|
| **BLOCKING** | Correctness, security, or a hard house convention. | Must fix before the change is acceptable. |
| **WARNING** | Quality/consistency issue; defensible exceptions exist. | Flag it, fix unless there's a stated reason. |
| **INFO** | Preference / nice-to-have. | Mention once; don't block. |

## Quick severity index

**BLOCKING**
- Secrets in source, missing auth checks, SQL built by string concat with user input (`security.md`)
- `any` in **new** TypeScript code (`typescript-quality.md`)
- New Java tests not on JUnit 5 (`java-testing.md`)
- `org.apache.commons.lang` (v2) imports in new Java code — use `lang3` or `java.util` (`java-architecture.md`)
- Wrong interface prefix / class suffix conventions (`naming-conventions.md`)

**WARNING**
- New Angular component without `OnPush` and no stated reason (`angular-patterns.md`)
- Empty `constructor(){}` / `ngOnInit(){}` left in new code (`angular-patterns.md`)
- Missing Javadoc/JSDoc on new public API (`general-quality.md`)
- Deep nesting where a guard clause would read better (`general-quality.md`)

**INFO**
- TSLint → angular-eslint migration (tracked tech debt, `typescript-quality.md`)
- Commons-lang v2 in *existing untouched* files

## Grandfathering rule

A standard applies to **new code and code you are already editing**. Do not open
unrelated files just to "fix" them, and never bundle a standards sweep into a
feature/bugfix commit — keep refactors in their own commit.
