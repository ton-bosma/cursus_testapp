# Java Formatting

**Severity: WARNING** (consistency matters; the whole codebase follows this).

This codebase uses a distinctive, consistent style. Match it exactly in new Java.

## Braces — Allman / BSD

Opening brace on its **own line**, for every block: class, method, `if`, `else`,
`for`, `while`, `try`, `switch`.

```java
public boolean isValid()
{
   if (decoratedValidator == null)
   {
      return true;
   }
   return decoratedValidator.isValid();
}
```

## Indentation

- **3 spaces** per level. Not tabs, not 4.

## Spacing

- **Two blank lines** between methods (and between fields block and first method).
- One statement per line.

## Aligned field declarations

When a class has several fields, **align the names** by padding the types:

```java
private boolean          hasErrors = false;
private final StringBuilder errorMessageBuilder;
private final IValidator    decoratedValidator;
```

```java
private String eventType;
private String subjectType;
private int    key;
```

## Interface constants

`UPPER_SNAKE` with a short Javadoc each:

```java
/** The Oracle {@code SYSDATE} value. */
String SYSDATE = "SYSDATE";
```

## Imports

- No wildcard imports.
- Group: project imports, then third-party, then `java.*` (as seen in the repo).

## Encoding

- Source files are **windows-1252**. Don't introduce characters outside that
  charset in new Java files.
