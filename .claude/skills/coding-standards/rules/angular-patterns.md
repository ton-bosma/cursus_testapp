# Angular Patterns

Angular 14, **NgModule-based** (not standalone). Component library `@nexus-ws/framework`.

## Component anatomy — WARNING

- `Nxs`-prefixed class, `Component` suffix, `nxs-` selector. New components use a
  single `nxs-` selector (no legacy `app-*` dual selector).
- Separate `templateUrl` + `styleUrls` files; co-located in the component folder
  with `.spec.ts` and `.stories.ts`.
- Implement lifecycle interfaces you actually use (`OnInit`, `OnChanges`) —
  `use-life-cycle-interface` is enforced.

## Change detection — WARNING (recommend OnPush)

- **Prefer `ChangeDetectionStrategy.OnPush`** for new components. It forces
  explicit, observable/immutable-driven data flow that's easier to reason about.
- Default change detection is acceptable **only with a stated reason** (e.g. a
  component that mutates inputs in place to match existing siblings).

```ts
@Component({
  selector: 'nxs-thing',
  templateUrl: './nxs-thing.component.html',
  styleUrls: ['./nxs-thing.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NxsThingComponent { }
```

## Inputs / outputs — BLOCKING (decorators) / WARNING (style)

- Use `@Input()` / `@Output()` **decorators** (enforced); never the
  inputs/outputs array. No input/output renaming.
- `@Output()` is an `EventEmitter`. Coerce boolean inputs with
  `coerceBooleanProperty` (CDK), as existing components do.
- Setter-backed inputs use a `_`-prefixed private field + getter/setter.

## Observables / RxJS — WARNING

- **`$` suffix** on observable fields/getters (`hasFocus$`, `isActive$`).
- Expose subjects as observables: `hasFocus$ = this._hasFocus$.asObservable();`.
- Prefer the `async` pipe in templates over manual `subscribe`. If you do
  subscribe in a component, unsubscribe (`takeUntil`, or `take(1)` for one-shots).
- RxJS 6 import paths (`rxjs`, `rxjs/operators`).

## No dead scaffolding — WARNING

- Remove empty `constructor() {}` and empty `ngOnInit() {}` that schematics
  generate but you don't use.

## DI — WARNING

- Constructor injection with `private` params. `@Injectable()` on services.

## Templates — WARNING

- `curly` is enforced — always brace control-flow in TS; in templates prefer
  structural directives cleanly (`*ngIf`, `*ngFor` with `trackBy` on lists).
