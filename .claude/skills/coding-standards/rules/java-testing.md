# Java Testing

## Framework — BLOCKING

- **New tests use JUnit 5 (Jupiter)** + Mockito (`mockito-junit-jupiter`).
- The build still supports JUnit 4 + Vintage + PowerMock for **legacy** tests —
  don't write new JUnit 4 tests. Migrate a test class to JUnit 5 when you make
  substantial changes to it.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Sha256AuthenticationUtilsTest
{
   private final IAuthenticationUtils authenticationUtils = new Sha256AuthenticationUtils();

   @Test
   void rejectsBlankPassword()
   {
      assertFalse(authenticationUtils.verify(null, hash, salt),
                  "No password means not verified");
   }
}
```

## Naming & messages — BLOCKING (English) / WARNING (style)

- Test methods: **English**, describe the behavior verified — `rejectsBlankPassword`,
  not `testVerify` and not Dutch (`testAanmakenEnVerificatie`). The `test` prefix
  is optional under JUnit 5; prefer behavior-named methods.
- Assertion messages: **English**, explain what should hold.
- In JUnit 5 the message is the **last** argument: `assertTrue(cond, "message")`
  (JUnit 4 had it first — don't carry that over).

## Structure — WARNING

- One behavior per `@Test`; arrange/act/assert.
- Field-initialized fixtures are fine for cheap immutable collaborators; use
  `@BeforeEach` when setup is non-trivial.
- Mock only what you must; prefer real collaborators when cheap.

## Formatting

- Same Allman braces / 3-space indent as production code (`java-formatting.md`).
