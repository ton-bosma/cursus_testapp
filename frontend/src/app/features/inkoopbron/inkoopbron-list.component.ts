import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  computed,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InkoopbronService, IInkoopbron } from './inkoopbron.service';
import { NL } from '../../core/i18n/nl.labels';

/** Row model — extends IInkoopbron with transient UI flags. */
interface IInkoopbronRow extends IInkoopbron {
  /** True when the row was added client-side and not yet persisted. */
  isNew: boolean;
  /** Three-pass delete flag: existing rows are marked, new rows are spliced. */
  markedDeleted: boolean;
}

/**
 * Inline table editor for inkoopbronnen.
 * Allows adding/deleting rows; saving sends the whole list via PUT.
 */
@Component({
  selector: 'app-inkoopbron-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './inkoopbron-list.component.html',
  styleUrl: './inkoopbron-list.component.scss',
  imports: [
    FormsModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
  ],
})
export class InkoopbronListComponent implements OnInit {
  protected readonly labels = NL;
  protected readonly displayedColumns = ['omschrijving', 'rapporteren', 'acties'];

  private readonly _service = inject(InkoopbronService);
  private readonly _snackBar = inject(MatSnackBar);

  private readonly _rows = signal<IInkoopbronRow[]>([]);

  /** Only rows not marked deleted are shown in the table. */
  protected readonly visibleRows = computed(() =>
    this._rows().filter((r) => !r.markedDeleted),
  );

  protected readonly saving = signal(false);

  ngOnInit(): void {
    this._loadAll();
  }

  protected addRow(): void {
    const newRow: IInkoopbronRow = {
      omschrijving: '',
      rapporteren: false,
      isNew: true,
      markedDeleted: false,
    };
    this._rows.update((rows) => [...rows, newRow]);
  }

  protected deleteRow(row: IInkoopbronRow): void {
    if (row.isNew) {
      // Unsaved new row — remove immediately, no server call.
      this._rows.update((rows) => rows.filter((r) => r !== row));
    } else {
      // Existing row — mark for three-pass delete on next save.
      row.markedDeleted = true;
      this._rows.update((rows) => [...rows]); // trigger signal update
    }
  }

  protected save(): void {
    this.saving.set(true);

    // Build payload: exclude new rows that are also marked deleted,
    // include all others (backend three-pass handles the rest).
    const payload: IInkoopbron[] = this._rows()
      .filter((r) => !(r.isNew && r.markedDeleted))
      .map(({ omschrijving, rapporteren, id, markedDeleted }) => ({
        ...(id !== undefined ? { id } : {}),
        omschrijving,
        rapporteren,
        markedDeleted,
      }));

    this._service.saveAll(payload).subscribe({
      next: () => {
        this._snackBar.open(NL.inkoopbron.opgeslagen, 'OK', { duration: 3000 });
        this._loadAll();
      },
      error: () => {
        this._snackBar.open(NL.inkoopbron.foutOpslaan, 'Sluiten', { duration: 5000 });
        this.saving.set(false);
      },
    });
  }

  private _loadAll(): void {
    this.saving.set(false);
    this._service.getAll().subscribe({
      next: (bronnen) => {
        const rows: IInkoopbronRow[] = bronnen.map((b) => ({
          ...b,
          isNew: false,
          markedDeleted: false,
        }));
        this._rows.set(rows);
      },
    });
  }
}
