# General Quality

## Guard clauses over nesting — WARNING

Prefer early returns; this matches the existing style.

```java
// Right
protected boolean validateIsSingleEntity(String none, String many, List<T> list)
{
   if (list.isEmpty())  { addError(none); return false; }
   if (list.size() > 1) { addError(many); return false; }
   return true;
}
```

## Comments & docs — WARNING

- **English** for all new comments and Javadoc/JSDoc.
- Public types and public/protected methods get a doc comment describing intent,
  not a restatement of the signature.
- No `/** TODO */` placeholder doc comments in new code — either document it or
  leave it undocumented.
- Comments explain **why**, not **what** the code already says.

## Null & emptiness — WARNING

- Java: prefer `java.util.Objects` and `String.isBlank()`/`isEmpty()`; use
  `commons-lang3` `StringUtils` when it genuinely reads better. Never the v2
  `org.apache.commons.lang` package in new code.
- TypeScript: `== null` / `!= null` is the accepted null+undefined check here
  (`triple-equals` allows the null-check exception). Use `===` everywhere else.

## Dead code — WARNING

- Don't leave empty lifecycle hooks or empty constructors in **new** code
  (see `angular-patterns.md`).
- Remove commented-out code; rely on version control instead.

## Over-engineering — INFO

- Don't add an abstraction "for the future." These libraries already provide the
  base abstractions; build on them, don't pre-invent new layers.
- Match the altitude of the surrounding code — a small helper doesn't need a
  factory + interface + builder.

## Logging — WARNING

- Java: SLF4J (`org.slf4j`). No `System.out`.
- Angular: no `console.debug/info/time/trace` (TSLint `no-console`); use the
  framework's logging component/service where one exists.
