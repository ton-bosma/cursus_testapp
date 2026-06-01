# Naming Conventions

**Severity: BLOCKING** for prefix/suffix conventions (they're load-bearing house
rules); WARNING for descriptive-name quality.

## Cross-stack

- **Interfaces are `I`-prefixed**: `IClause`, `IValidator`, `ICommand`,
  `IAuthorizationSetting`. This holds in **both** Java and TypeScript.
- **English identifiers.** Existing Dutch domain terms (`orgBeperking`,
  `patcliId`) stay as-is — they're domain vocabulary — but new generic names are
  English.
- No abbreviations beyond established domain ones. `errorMessageBuilder`, not `emb`.

## Java

| Kind | Convention | Example |
|---|---|---|
| Class | `PascalCase` | `EntityValidator` |
| Abstract base | `Abstract` prefix | `AbstractValidator` |
| Interface | `I` prefix | `IValidator` |
| Constant | `UPPER_SNAKE` | `LIST_PREFIX`, `TRUNCATED_SYSDATE` |
| Field / method / param | `camelCase` | `decoratedValidator` |
| Generic type param | descriptive, not single-letter when it has meaning | `<TEntity extends EntityBean>` |
| Test method | `test<WhatIsVerified>` (English) | `testRejectsBlankPassword` |

## Angular / TypeScript

| Kind | Convention | Example |
|---|---|---|
| Component class | `Nxs` prefix + `Component` suffix | `NxsToolbarComponent` |
| Service class | `Service` suffix | `AuthorizationService` |
| Directive | `Directive` suffix | — |
| Selector | `nxs-` prefix, kebab-case | `nxs-toolbar` |
| Interface | `I` prefix | `ICommand` |
| Enum | `PascalCase` type + `PascalCase` members | `ToolbarType.LeftMenu` |
| Observable field/getter | **`$` suffix** | `hasFocus$`, `isActive$` |
| Private backing field | `_` prefix | `_hasFocus$`, `_leftActionList` |
| File | kebab-case + role suffix | `nxs-toolbar.component.ts`, `authorization.service.ts` |

> Legacy dual selectors like `'app-toolbar, nxs-toolbar'` exist for back-compat.
> New components use the `nxs-` selector only.
