# File Organization

**Severity: WARNING**

Both repos use **domain-driven** grouping, not technical-layer grouping.

## Java

- Package root: `nl.mckesson.med.{model,service}.common.<domain>`.
- Group by **domain/feature** (`validation`, `query`, `marshalling`, `event`,
  `encryption`), not by type (`impl`, `dto`, `util`).
- One top-level public type per file (Java enforces this).
- Inner helper enums/interfaces live on the interface they belong to
  (e.g. `Comparison` enum inside `IClause`) when tightly coupled.
- Source encoding is **windows-1252** — do not convert existing files to UTF-8;
  avoid introducing non-Latin1 characters in new Java source.

## Angular

- Library layout under `projects/nexus-ws/framework/<area>/`:
  - `components/<nxs-thing>/` — `*.component.ts`, `.html`, `.scss`, `.spec.ts`,
    and a `.stories.ts` together in one folder.
  - `services/` — feature services.
  - `models/` — `interfaces.ts`, enums, types.
- **One component per folder**; co-locate template, styles, spec, and story.
- Keep `templateUrl` + `styleUrls` as **separate files** (not inline templates).
- Shared interfaces/enums go in the area's `models/`, imported via the
  `@nexus-ws/framework` public API barrel — don't reach into deep relative paths
  across areas.

## General

- A new file's location is decided by **what domain it serves**, then its role.
- Don't create `utils`/`helpers` dumping grounds; put a helper next to the domain
  it supports, or reuse an existing `*Helper`.
