import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ShellComponent } from './shared/shell/shell.component';

@Component({
  selector: 'app-root',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ShellComponent],
  template: `<app-shell />`,
})
export class AppComponent {}
