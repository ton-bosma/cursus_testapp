# Angular Testing

## Stack — WARNING

- **Jest** (`jest`, `@types/jest`, `setup-jest.ts`) + **@ngneat/spectator**.
- Not Karma/Jasmine-runner. New specs use Jest + Spectator idioms.

## Conventions — WARNING

- Spec file co-located: `nxs-thing.component.spec.ts` next to the component.
- Use Spectator's `createComponentFactory` / `createServiceFactory` to reduce
  TestBed boilerplate.
- **English** test descriptions: `it('emits toolbarAction when the menu button is clicked', ...)`.
- One behavior per `it`; arrange/act/assert.
- `test:ci` runs with `--coverage --passWithNoTests`; new components/services
  should ship with at least their core behavior covered.

## Typing in tests — WARNING

- Apply the same **no-`any`** rule (`typescript-quality.md`). Type your spies,
  fixtures, and mock data. Spectator's generics give you typed access — use them.

```ts
import { createComponentFactory, Spectator } from '@ngneat/spectator/jest';

describe('NxsThingComponent', () => {
  let spectator: Spectator<NxsThingComponent>;
  const createComponent = createComponentFactory(NxsThingComponent);

  beforeEach(() => spectator = createComponent());

  it('renders the title input', () => {
    spectator.setInput('title', 'Hello');
    spectator.detectChanges();
    expect(spectator.query('h1')).toHaveText('Hello');
  });
});
```

## Stories — INFO

- New library components get a `*.stories.ts` (Storybook 6) alongside the spec,
  matching the existing component folders.
