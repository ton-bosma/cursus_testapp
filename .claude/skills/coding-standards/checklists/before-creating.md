# Before Creating — Pre-Coding Guard

Run through this before writing a new class, component, or service.

- [ ] **Does it already exist?** Searched for an `Abstract*` base / `I*` interface
      / existing `Nxs*` component / service that covers this. (`reuse-first.md`)
- [ ] **Can I extend/compose instead of create?** Decorator, subclass, template
      method, content projection.
- [ ] **Right location?** Domain-driven package/folder, co-located files for
      Angular components. (`file-organization.md`)
- [ ] **Naming** follows the prefix/suffix rules — `I*` interface, `Abstract*`
      base, `Nxs*Component`, `nxs-` selector, `$` observables. (`naming-conventions.md`)
- [ ] **Stack decisions** for new code are settled:
      - Java: JUnit 5 for tests, `lang3`/`Objects` (not commons-lang v2)
      - TS: strict, **no `any`**, prefer **OnPush**
- [ ] **Security surface?** If it touches auth, SQL, tenants, secrets, or external
      input — read `security.md` first.
- [ ] **Am I about to over-build?** Match the altitude of surrounding code; don't
      add speculative abstraction. (`general-quality.md`)
