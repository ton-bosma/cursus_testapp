# TypeScript Quality

## Strict typing — BLOCKING

- **New code is strictly typed. No `any`.** Use precise types; reach for
  `unknown` + narrowing at genuine boundaries, never `any`.
- This is a deliberate upgrade from the existing code (which uses `any` freely
  and a non-strict `tsconfig`). Existing `any` is grandfathered; do not add more.

```ts
// Wrong (new code)
writeValue(obj: any): void { this.value = obj; }

// Right
writeValue(value: string | null): void { this.value = value; }
```

- Where a value is truly dynamic, type it `unknown` and narrow:

```ts
function parse(input: unknown): IAuthorizationSetting {
  if (typeof input !== 'object' || input === null) { throw new Error('bad input'); }
  // narrow further...
}
```

## Formatting (matches `.editorconfig` + `tslint.json`) — WARNING

- **2-space** indent, **single quotes**, **semicolons always**, max line **140**.
- `prefer-const`, no `var`, `interface-over-type-literal`.
- `===`/`!==` everywhere, except `== null` / `!= null` for the combined
  null+undefined check.
- No `rxjs/Rx` barrel imports; import from `rxjs` and `rxjs/operators` (RxJS 6).

## Member ordering — WARNING

Follow the enforced order: `static-field` → `instance-field` →
`static-method` → `instance-method`. Group `@Input()`/`@Output()` fields together
near the top.

## Interfaces & types — WARNING

- `I`-prefixed interfaces (`ICommand`). Prefer interfaces over type-literal
  aliases for object shapes.
- Index-signature maps get a named interface:
  `interface IScreenAuthorizationMap { [key: string]: IAuthorizationSetting; }`.

## Lint tooling — INFO (tech debt)

- Standards here are tool-agnostic. The repo still runs **TSLint + codelyzer**,
  which is deprecated for Angular 14. Migrating to **angular-eslint** is tracked
  tech debt — note it, don't block on it.
