# Java Architecture & Idioms

## Interfaces & base classes — BLOCKING for prefix, WARNING for pattern

- Interfaces are **`I`-prefixed** (`IValidator`, `IClause`).
- Provide an `Abstract*` base implementing the interface when there's shared
  behavior; concrete classes extend it and fill in `abstract` template methods.

```java
public abstract class AbstractValidator implements IValidator
{
   protected abstract void validateImplementation();   // template method

   public void validate()
   {
      init();
      validateImplementation();
   }
}
```

## Patterns in active use

- **Template method** — public method drives the flow, `protected abstract`
  hooks fill in specifics (`validate()` → `validateImplementation()`).
- **Decorator** — wrap an `IValidator`/`IClause` to compose behavior; constructors
  take the decorated instance.
- **Generics with descriptive bounds** — `<TEntity extends EntityBean>`.

## Commons / utilities — BLOCKING

- New code: **`commons-lang3`** (`org.apache.commons.lang3.StringUtils`) or
  `java.util.Objects` / built-in `String` methods.
- **Never** import the v2 `org.apache.commons.lang.*` in new code — even though
  21 existing files do. Migrate those imports opportunistically when you edit the
  file.

## Exceptions & control flow — WARNING

- Guard clauses / early returns (see `general-quality.md`).
- Don't swallow exceptions silently; log via SLF4J and rethrow or handle
  meaningfully.

## POJOs / beans

- Plain fields + getters/setters, aligned field block, constructors that set the
  required fields. Implement `Serializable` where the existing siblings do
  (e.g. event/subscription types).

## Build

- **Gradle only** (never Maven). Module builds via `nxs-build-plugin`.
- SonarQube is wired in; keep new code clean of new issues.
