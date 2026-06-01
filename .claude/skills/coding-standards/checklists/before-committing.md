# Before Committing — Post-Write Checklist

## Correctness & security (BLOCKING)

- [ ] No secrets, keys, or tokens in the diff; nothing sensitive logged.
- [ ] SQL uses the bind-clause framework; no string-concatenated user input.
- [ ] Auth/tenant checks present where the data requires them.
- [ ] External/`unknown` input is validated/narrowed before use.

## Stack rules (BLOCKING)

- [ ] **TS:** no new `any`; strict-typed. **Java:** no `org.apache.commons.lang` v2.
- [ ] **Java tests:** JUnit 5, English names + messages (message is last arg).
- [ ] Interfaces `I`-prefixed; classes/selectors follow naming rules.

## Quality (WARNING)

- [ ] **Java:** Allman braces, 3-space indent, aligned field block, 2 blank lines
      between methods, English Javadoc on new public API.
- [ ] **Angular:** new component uses `OnPush` (or has a stated reason); no empty
      `constructor(){}`/`ngOnInit(){}`; observables `$`-suffixed; `async` pipe or
      proper unsubscribe.
- [ ] Guard clauses over deep nesting; no commented-out code; no TODO doc stubs.
- [ ] Reused existing abstractions rather than duplicating.

## Hygiene

- [ ] Refactors are in their **own** commit — not mixed with feature/bugfix.
- [ ] Java source stays windows-1252-safe; Angular source UTF-8, final newline.
- [ ] Build is Gradle; no new SonarQube issues introduced.

> Enforcement is by review/assistant, not a git hook (intentionally not installed).
