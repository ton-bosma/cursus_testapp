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
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InstrumenttypeService, IInstrumentType } from './instrumenttype.service';
import { NL } from '../../core/i18n/nl.labels';

/** Row model — extends IInstrumentType with transient UI flags. */
interface IInstrumenttypeRow extends IInstrumentType {
  /** True when the row was added client-side and not yet persisted. */
  isNew: boolean;
  /** Three-pass delete flag: existing rows are marked, new rows are spliced. */
  markedDeleted: boolean;
}

/**
 * Inline table editor for instrument types.
 * Allows adding/deleting rows; saving sends the whole list via PUT.
 */
@Component({
  selector: 'app-instrumenttype-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './instrumenttype-list.component.html',
  styleUrl: './instrumenttype-list.component.scss',
  imports: [
    FormsModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
})
export class InstrumenttypeListComponent implements OnInit {
  protected readonly labels = NL;
  protected readonly displayedColumns = ['omschrijving', 'forfaitAccessoires', 'acties'];

  /** Uppercase meta line shown in the masthead. */
  protected readonly pageMeta = 'ONDERHOUD · TYPES';

  private readonly _service = inject(InstrumenttypeService);
  private readonly _snackBar = inject(MatSnackBar);

  private readonly _rows = signal<IInstrumenttypeRow[]>([]);

  /** Only rows not marked deleted are shown in the table. */
  protected readonly visibleRows = computed(() =>
    this._rows().filter((r) => !r.markedDeleted),
  );

  protected readonly saving = signal(false);

  ngOnInit(): void {
    this._loadAll();
  }

  protected addRow(): void {
    const newRow: IInstrumenttypeRow = {
      omschrijving: '',
      forfaitAccessoires: 0,
      isNew: true,
      markedDeleted: false,
    };
    this._rows.update((rows) => [...rows, newRow]);
  }

  protected deleteRow(row: IInstrumenttypeRow): void {
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
    const payload: IInstrumentType[] = this._rows()
      .filter((r) => !(r.isNew && r.markedDeleted))
      .map(({ omschrijving, forfaitAccessoires, id, markedDeleted }) => ({
        ...(id !== undefined ? { id } : {}),
        omschrijving,
        forfaitAccessoires,
        markedDeleted,
      }));

    this._service.saveAll(payload).subscribe({
      next: () => {
        this._snackBar.open(NL.instrumenttype.opgeslagen, 'OK', { duration: 3000 });
        this._loadAll();
      },
      error: () => {
        this._snackBar.open(NL.instrumenttype.foutOpslaan, 'Sluiten', { duration: 5000 });
        this.saving.set(false);
      },
    });
  }

  private _loadAll(): void {
    this.saving.set(false);
    this._service.getAll().subscribe({
      next: (types) => {
        const rows: IInstrumenttypeRow[] = types.map((t) => ({
          ...t,
          isNew: false,
          markedDeleted: false,
        }));
        this._rows.set(rows);
      },
    });
  }
}
