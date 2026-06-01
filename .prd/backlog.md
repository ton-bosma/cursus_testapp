# Findings Backlog

## PR #19 — UI-redesign: Editorial Ledger-stijl (dashboard + app-breed) (2026-06-01)

### code-simplifier / standards-reviewer (1)
| Score | File | Finding | Suggestion |
|-------|------|---------|------------|
| 50 | frontend/src/app/features/dashboard/dashboard.component.scss:1-7,101-124 | Dashboard SCSS re-declares 6 global ledger tokens (`--paper/--paper-2/--ink/--ink-soft/--vermilion/--rule`) already on `:root` in styles.scss, and re-implements `.rule`/`.rule--double` locally with 1-2px drifts from the shared `.ledger-rule`/`.ledger-rule--double` utilities used by all other screens | Drop the local token block (inherit from `:root`) and replace `.rule`/`.rule--double` markup with the global `.ledger-rule` classes; keep only genuinely dashboard-specific overrides (larger title clamp, action button) |

## PR #17 — v1: Fundament + Instrumenten-domein (2026-06-01)

### silent-failure-hunter / code-quality (5)
| Score | File | Finding | Suggestion |
|-------|------|---------|------------|
| 50 | frontend .../instrument/currency.directive.ts | `_normalize` uses `replace(',', '.')` (first comma only): `1,234,56` → `1.234,56` → NaN; FormControl<number\|null> can hold a raw string if submit happens before blur | Normalize on `input` too, or coerce currency fields in `_toInstrument()` |
| 50 | frontend .../instrument/instrument-detail.component.ts (generate*) | `generateAanschafnr$`/`generateHuurnr$` subscribe has no `error` callback — only the generic interceptor snackbar fires, no targeted message | Add `error:` callbacks with a specific message |
| 50 | frontend .../inkoopbron + instrumenttype list `_loadAll` | No `error` callback; failed reload-after-save shows stale data with only a generic snackbar | Add `error:` handler distinguishing reload failure |
| 50 | frontend .../instrument/instrument-detail.component.ts `_loadDropdowns` | Two subscribes without `error` callbacks → empty type/bron dropdowns on load failure | Add `error:` handlers |
| 50 | backend .../inkoopbron + instrumenttype Repository `update`/`deleteById` | Return `void`, discard `execute()` rowcount → concurrent update-of-deleted silently lost | Return `int`, check in service, WARN/throw on 0 rows |

### type-design (2)
| Score | File | Finding | Suggestion |
|-------|------|---------|------------|
| 65 | frontend .../inkoopbron + instrumenttype service interfaces | `markedDeleted` is sent in the PUT payload but absent from `IInkoopbron`/`IInstrumentType` — load-bearing flag invisible in the type | Model an explicit `*SaveRequest` type including `markedDeleted` |
| 50 | frontend .../instrument/instrument.model.ts:13 (`anno`) | `anno` typed `number\|null` but backend `String` / DB VARCHAR; `"ca. 1890"` lies about runtime shape | Change to `string\|null` across interface/form/mapping (KNOWN follow-up per PR desc) |

### security / history / standards (5)
| Score | File | Finding | Suggestion |
|-------|------|---------|------------|
| 50 | backend/build.gradle (codegen creds) | Hardcoded `codegenJdbcPass = 'viool'` (build-time, ephemeral container) | Move to gradle property / comment as local-only |
| 50 | frontend .../core/http/error.interceptor.ts | `console.error('[HTTP Error]', error)` logs full `HttpErrorResponse` (may carry Spring stacktrace/SQL) | Log only `error.status` + `error.url` |
| 75 | backend .../instrument/InstrumentService.java:171 | `generateAanschafnr` does `inkoopbronRepository.findAll().stream().filter()` to find one row by id — full scan, no `findById`, runs outside the generate tx | Add `findById` to `IInkoopbronRepository`; single SELECT |
| 50 | backend .../instrument/InstrumentService + Controller | No `IInstrumentRepository`/`IInstrumentService` interfaces (inconsistent with the two reference domains) | Extract `I`-prefixed interfaces for consistency |
| 50 | backend .../instrument/InstrumentRepository.search | Dead `DateTimeFormatter fmt` + unused import (formatting done DB-side via `to_char`) | Remove the variable + import |

### test-coverage (1)
| Score | File | Finding | Suggestion |
|-------|------|---------|------------|
| 50 | backend tests + detail/interceptor specs | No IT for `generateAanschafnr`/`generateHuurnr` happy path; self-exclusion re-generate, NN>99 overflow, `delete()`, interceptor snackbar, confirm-then-generate ("Ja") all untested | Add missing ITs/specs — prioritize generate paths + self-exclusion + delete() |
