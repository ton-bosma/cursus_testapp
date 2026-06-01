import { ChangeDetectionStrategy, Component } from '@angular/core';

/**
 * Placeholder component for the Inkoopbron feature.
 * Implementation is provided by a later ticket.
 */
@Component({
  selector: 'app-inkoopbron-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<p>Inkoopbron — wordt ingevuld in een later ticket.</p>`,
})
export class InkoopbronListComponent {}
