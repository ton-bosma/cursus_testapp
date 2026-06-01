# Reuse Before Creating

**Severity: WARNING** (BLOCKING when you duplicate something that already exists).

Both codebases are libraries built around shared base types. The expectation is
**extend the existing abstraction, don't fork a parallel one**.

## Before writing a new class/component/util

1. Search for an existing base class, interface, or component that covers it.
   - Java: `Abstract*` base classes, `I*` interfaces, `*Helper`/`*Utils` classes,
     the validation/query/marshalling frameworks in `model.common.*`.
   - Angular: existing `Nxs*` components, services in `common/services`, shared
     `models/interfaces`.
2. If something is close, **extend or compose it** (decorator, template method,
   subclass, content projection) rather than copying.
3. Only create a new top-level abstraction when nothing fits — and say why.

## Right

```java
// Reuse the validation framework's template method.
public class OrderValidator extends EntityValidator<Order>
{
   @Override
   protected void validateEntity()
   {
      if (getEntity().getLines().isEmpty())
      {
         addError("Order must have at least one line", "lines");
      }
   }
}
```

## Wrong

```java
// New hand-rolled validation that ignores the existing EntityValidator base.
public class OrderChecker
{
   public List<String> check(Order o) { ... }   // duplicate machinery
}
```

## Constants & helpers

- Don't redefine a constant that already lives on an interface/utility
  (e.g. `IClause.SYSDATE`). Import it.
- A value duplicated in 3+ places belongs in a shared constants holder, not
  copy-pasted.
