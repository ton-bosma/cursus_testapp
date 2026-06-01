---
name: coding-standards
description: Coding standards for this project (Java/Gradle + Angular/TypeScript). Auto-loaded at conversation start. Sub-files loaded on demand based on the work being done.
---

# Coding Standards

Single source of truth for coding rules on this project. Derived from the real
conventions in two reference repositories:

- **Java** — `nexus-h3-base/nxs-model` (`nl.mckesson.med.*`)
- **Angular/TypeScript** — `nexus-webmodule/nxs-framework-webmodule` (`@nexus-ws/framework`)

These standards describe how **new code** should be written. Existing files are
grandfathered — match them locally when editing, but bring them toward these
rules when you touch them.

## Global decisions (apply everywhere)

- **Language:** English for all new comments, Javadoc/JSDoc, and identifiers.
  (Existing Dutch comments/domain terms stay; don't mass-translate.)
- **Interfaces are `I`-prefixed** in both Java and TypeScript (`IClause`,
  `ICommand`) — this is a deliberate house convention, consistent across stacks.
- **Reuse before creating** — see `rules/reuse-first.md`.

## When to load which rule file

| Working on… | Read |
|---|---|
| Any code | `rules/reuse-first.md`, `rules/naming-conventions.md`, `rules/general-quality.md` |
| Java source | `rules/java-formatting.md`, `rules/java-architecture.md` |
| Java tests | `rules/java-testing.md` |
| Angular components/services | `rules/typescript-quality.md`, `rules/angular-patterns.md` |
| Angular tests | `rules/angular-testing.md` |
| Anything touching auth/data/secrets | `rules/security.md` |
| Placing/naming new files | `rules/file-organization.md` |
| Severity of a rule | `lint-config.md` |

## Stack snapshot

**Java:** Java 8-era, Gradle (nxs-build-plugin), JBoss/JavaEE, Oracle JDBC,
SLF4J, Apache Commons. SonarQube. Source encoding **windows-1252**.

**Angular:** Angular 14 (NgModule-based), RxJS 6, Material/CDK, Jest +
@ngneat/spectator, Storybook, schematics. Source encoding **UTF-8**, 2-space.

## Maintenance

- `/coding-interview refresh` — re-analyze the repos for drift against these rules.
- `/coding-interview extend` — add a new area (e.g. PL/SQL, CI/CD, mobile).
- Enforcement (pre-commit hook) is intentionally **not** set up — these are
  guidance docs the assistant reads, not a blocking gate.
