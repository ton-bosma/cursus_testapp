import { ChangeDetectionStrategy, Component } from '@angular/core';

/**
 * Placeholder component for the Instrumenten feature.
 * Implementation is provided by a later ticket.
 */
@Component({
  selector: 'app-instrument-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<p>Instrumenten — wordt ingevuld in een later ticket.</p>`,
})
export class InstrumentListComponent {}
