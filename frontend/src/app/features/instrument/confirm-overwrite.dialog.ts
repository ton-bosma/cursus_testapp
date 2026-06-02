import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { NL } from '../../core/i18n/nl.labels';

/** Data passed into the overwrite-confirmation dialog. */
export interface IConfirmOverwriteData {
  /** The body message explaining what will be overwritten. */
  message: string;
}

/**
 * Simple yes/no confirmation dialog used before overwriting an already-filled
 * generated number (aanschafnr / huurnr). Resolves to `true` on confirm.
 */
@Component({
  selector: 'app-confirm-overwrite-dialog',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ labels.overschrijfTitel }}</h2>
    <mat-dialog-content>{{ data.message }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">{{ labels.btnNee }}</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="true">
        {{ labels.btnJa }}
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmOverwriteDialog {
  protected readonly labels = NL.instrumentDetail;
  protected readonly data = inject<IConfirmOverwriteData>(MAT_DIALOG_DATA);
}
