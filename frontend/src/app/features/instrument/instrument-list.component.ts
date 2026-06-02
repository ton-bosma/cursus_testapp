import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { debounceTime, distinctUntilChanged, map, of, startWith, switchMap } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NL } from '../../core/i18n/nl.labels';
import { IInstrumentRow, IInstrumentSearchParams, MAX_RESULTS_OPTIONS } from './instrument.model';
import { InstrumentService } from './instrument.service';

/** Debounce interval (ms) for the live search input. */
const SEARCH_DEBOUNCE_MS = 300;

@Component({
  selector: 'app-instrument-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
  ],
  templateUrl: './instrument-list.component.html',
  styleUrl: './instrument-list.component.scss',
})
export class InstrumentListComponent {
  protected readonly labels = NL.instrument;
  protected readonly maxOptions = MAX_RESULTS_OPTIONS;

  protected readonly displayedColumns: readonly string[] = [
    'huurnr',
    'aanschafnr',
    'type',
    'maat',
    'inkoopAdres',
    'verkoopAdres',
    'aanschafdatum',
    'verkoopdatum',
  ];

  /** User-controlled search inputs (signals). */
  protected readonly query = signal('');
  protected readonly toonArchief = signal(false);
  protected readonly maxResults = signal<number | null>(MAX_RESULTS_OPTIONS[0].value);

  private readonly _service = inject(InstrumentService);
  private readonly _router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly searchFailed = signal(false);

  /** Combined search params; recomputed whenever any input signal changes. */
  private readonly _params = computed<IInstrumentSearchParams>(() => ({
    q: this.query().trim(),
    archief: this.toonArchief(),
    max: this.maxResults(),
  }));

  /**
   * Debounced live search: the params signal is converted to an observable,
   * debounced, de-duplicated, and mapped through the service via switchMap so
   * that rapid typing produces a single in-flight call.
   */
  private readonly _rows = toSignal(
    toObservable(this._params).pipe(
      debounceTime(SEARCH_DEBOUNCE_MS),
      distinctUntilChanged(
        (a, b) => a.q === b.q && a.archief === b.archief && a.max === b.max,
      ),
      switchMap((params) => {
        this.loading.set(true);
        return this._service.search$(params).pipe(
          map((rows) => {
            this.loading.set(false);
            this.searchFailed.set(false);
            return rows;
          }),
          catchError(() => {
            this.loading.set(false);
            this.searchFailed.set(true);
            return of<IInstrumentRow[]>([]);
          }),
        );
      }),
      startWith<IInstrumentRow[]>([]),
    ),
    { initialValue: [] as IInstrumentRow[] },
  );

  protected readonly rows = computed(() => this._rows());

  /** Left-pads the rental number to 4 digits, per spec. Returns '' for null. */
  protected formatHuurnr(huurnr: number | null): string {
    return huurnr == null ? '' : String(huurnr).padStart(4, '0');
  }

  protected onQueryInput(value: string): void {
    this.query.set(value);
  }

  protected onArchiefChange(checked: boolean): void {
    this.toonArchief.set(checked);
  }

  protected onMaxChange(value: number | null): void {
    this.maxResults.set(value);
  }

  /** Row click → navigate to the detail screen. */
  protected openDetail(row: IInstrumentRow): void {
    void this._router.navigate(['/instrumenten', row.id]);
  }

  /** "+" button → open an empty detail screen. */
  protected openNew(): void {
    void this._router.navigate(['/instrumenten', 'nieuw']);
  }
}
