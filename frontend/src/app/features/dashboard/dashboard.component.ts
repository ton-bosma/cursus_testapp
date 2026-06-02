import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { InstrumentService } from '../instrument/instrument.service';
import { IInstrumentRow } from '../instrument/instrument.model';
import { InstrumenttypeService } from '../instrumenttype/instrumenttype.service';
import { InkoopbronService } from '../inkoopbron/inkoopbron.service';
import { NL } from '../../core/i18n/nl.labels';

/** A single key-figure cell in the ledger strip. */
interface IKpi {
  readonly value: number;
  readonly label: string;
  readonly accent: boolean;
}

/** A recently registered instrument, as shown in the ledger entries. */
interface IRecentRow {
  readonly id: number;
  readonly type: string;
  readonly maat: string;
  readonly datum: string;
}

/**
 * Home dashboard — "Het Vioolregister".
 *
 * Editorial-ledger landing page that aggregates live counts from the existing
 * instrument / type / inkoopbron endpoints. Read-only overview; all mutations
 * happen in the feature screens it links to.
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  protected readonly nl = NL.dashboard;
  protected readonly editieJaar = new Date().getFullYear().toString();

  protected readonly loading = signal(true);
  protected readonly error = signal(false);
  protected readonly kpis = signal<readonly IKpi[]>([]);
  protected readonly recent = signal<readonly IRecentRow[]>([]);
  protected readonly bronnenAantal = signal(0);

  private readonly _instrumentService = inject(InstrumentService);
  private readonly _typeService = inject(InstrumenttypeService);
  private readonly _bronService = inject(InkoopbronService);
  private readonly _destroyRef = inject(DestroyRef);


  constructor()
  {
    this._load();
  }


  protected formatIndex(index: number): string
  {
    return String(index + 1).padStart(2, '0');
  }


  private _load(): void
  {
    forkJoin({
      instruments: this._instrumentService.search$({ q: '', archief: true, max: null }),
      types: this._typeService.getAll(),
      bronnen: this._bronService.getAll(),
    })
      .pipe(takeUntilDestroyed(this._destroyRef))
      .subscribe({
        next: (data) => {
          this._build(data.instruments, data.types.length, data.bronnen.length);
          this.loading.set(false);
        },
        error: () => {
          this.error.set(true);
          this.loading.set(false);
        },
      });
  }


  private _build(rows: readonly IInstrumentRow[], typeCount: number, bronCount: number): void
  {
    const verkocht = rows.filter((row) => row.verkoopdatum !== null).length;
    const inBezit = rows.length - verkocht;

    this.kpis.set([
      { value: rows.length, label: this.nl.kpi.totaal, accent: true },
      { value: inBezit, label: this.nl.kpi.inBezit, accent: false },
      { value: verkocht, label: this.nl.kpi.verkocht, accent: false },
      { value: typeCount, label: this.nl.kpi.types, accent: false },
    ]);

    this.bronnenAantal.set(bronCount);

    this.recent.set(
      [...rows]
        .sort((a, b) => b.id - a.id)
        .slice(0, 6)
        .map((row) => ({
          id: row.id,
          type: row.type || '—',
          maat: row.maat ?? '',
          datum: this._formatDatum(row.aanschafdatum),
        })),
    );
  }


  private _formatDatum(iso: string | null): string
  {
    if (iso === null || iso.length < 10) {
      return '—';
    }
    const [year, month, day] = iso.substring(0, 10).split('-');
    return `${day}-${month}-${year}`;
  }
}
