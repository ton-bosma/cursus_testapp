import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { firstValueFrom } from 'rxjs';
import { NL } from '../../core/i18n/nl.labels';
import { CurrencyDirective } from '../../shared/currency.directive';
import {
  ConfirmOverwriteDialog,
  IConfirmOverwriteData,
} from './confirm-overwrite.dialog';
import { IInkoopbron, InkoopbronService } from '../inkoopbron/inkoopbron.service';
import {
  IInstrumentType,
  InstrumenttypeService,
} from '../instrumenttype/instrumenttype.service';
import { IInstrument } from './instrument.model';
import { InstrumentService } from './instrument.service';

/** Strongly-typed reactive form shape for an instrument. */
interface IInstrumentForm {
  aanschafnr: FormControl<string | null>;
  huurnr: FormControl<number | null>;
  datumIn: FormControl<string | null>;
  idAdresIn: FormControl<number | null>;
  inkoopInstr: FormControl<number | null>;
  inkoopAcc: FormControl<number | null>;
  inkoopFactuur: FormControl<string | null>;
  idInkoopbron: FormControl<number | null>;
  idAdresTaxateur: FormControl<number | null>;
  verkoopBtw: FormControl<number | null>;
  omschrijvIn: FormControl<string | null>;
  datumUit: FormControl<string | null>;
  verkoopInstr: FormControl<number | null>;
  idAdresUit: FormControl<number | null>;
  reparaties: FormControl<string | null>;
  maat: FormControl<string | null>;
  antique: FormControl<boolean>;
  anno: FormControl<number | null>;
  idInstrType: FormControl<number | null>;
  foto: FormControl<string | null>;
  datumTaxatie: FormControl<string | null>;
}

/**
 * Instrument detail screen: three collapsible panels (In / Uit / Instrument),
 * currency-normalized amounts, save (PUT) with reload, and the two
 * "generate number" actions guarded by an overwrite confirmation.
 */
@Component({
  selector: 'app-instrument-detail',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    CurrencyDirective,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
  ],
  templateUrl: './instrument-detail.component.html',
  styleUrl: './instrument-detail.component.scss',
})
export class InstrumentDetailComponent implements OnInit {
  /** Route param `id` (bound via withComponentInputBinding). Absent → new. */
  @Input() public id?: string;

  /**
   * When true the component is rendered nested inside a selection list; the
   * delete button is only shown in this mode (per spec).
   */
  @Input() public nested = false;

  protected readonly labels = NL.instrumentDetail;

  /** Uppercase masthead meta line (local UI string; not shared i18n). */
  protected readonly metaLine = 'REGISTER · INSTRUMENT';

  /** Masthead title: "new" until a row is persisted, then "edit". */
  protected readonly title = computed(() =>
    this.currentId() === undefined ? this.labels.titelNieuw : this.labels.titelBewerken,
  );

  protected readonly types = signal<readonly IInstrumentType[]>([]);
  protected readonly bronnen = signal<readonly IInkoopbron[]>([]);

  protected readonly loading = signal(false);
  protected readonly saving = signal(false);

  /** The currently loaded/persisted instrument id (undefined for new). */
  protected readonly currentId = signal<number | undefined>(undefined);

  public readonly form: FormGroup<IInstrumentForm> = new FormGroup<IInstrumentForm>({
    aanschafnr: new FormControl<string | null>(null),
    huurnr: new FormControl<number | null>(null),
    datumIn: new FormControl<string | null>(null),
    idAdresIn: new FormControl<number | null>(null),
    inkoopInstr: new FormControl<number | null>(null),
    inkoopAcc: new FormControl<number | null>(null),
    inkoopFactuur: new FormControl<string | null>(null),
    idInkoopbron: new FormControl<number | null>(null),
    idAdresTaxateur: new FormControl<number | null>(null),
    verkoopBtw: new FormControl<number | null>(null),
    omschrijvIn: new FormControl<string | null>(null),
    datumUit: new FormControl<string | null>(null),
    verkoopInstr: new FormControl<number | null>(null),
    idAdresUit: new FormControl<number | null>(null),
    reparaties: new FormControl<string | null>(null),
    maat: new FormControl<string | null>(null),
    antique: new FormControl<boolean>(false, { nonNullable: true }),
    anno: new FormControl<number | null>(null),
    idInstrType: new FormControl<number | null>(null),
    foto: new FormControl<string | null>(null),
    datumTaxatie: new FormControl<string | null>(null),
  });

  private readonly _service = inject(InstrumentService);
  private readonly _typeService = inject(InstrumenttypeService);
  private readonly _bronService = inject(InkoopbronService);
  private readonly _dialog = inject(MatDialog);
  private readonly _snackBar = inject(MatSnackBar);

  public ngOnInit(): void {
    this._loadDropdowns();
    const parsed = this.id !== undefined ? Number(this.id) : NaN;
    if (!Number.isNaN(parsed)) {
      this._load(parsed);
    }
  }

  /** Save → PUT /api/instrument → reload the persisted row into the form. */
  protected save(): void {
    this.saving.set(true);
    const payload = this._toInstrument();
    this._service.save$(payload).subscribe({
      next: (saved) => {
        this._patchForm(saved);
        this.currentId.set(saved.id);
        this.saving.set(false);
        this._snackBar.open(this.labels.opgeslagen, 'OK', { duration: 3000 });
      },
      error: () => this.saving.set(false),
    });
  }

  /** Delete the current instrument (only reachable in nested mode). */
  protected delete(): void {
    const id = this.currentId();
    if (id === undefined) {
      return;
    }
    this._service.delete$(id).subscribe({
      next: () => {
        this._snackBar.open(this.labels.verwijderd, 'OK', { duration: 3000 });
      },
    });
  }

  /**
   * Generate an aanschafnr. Requires datum-in + inkoopbron (client-side guard).
   * If a number is already present, confirm before overwriting.
   */
  protected async generateAanschafnr(): Promise<void> {
    const { datumIn, idInkoopbron, aanschafnr } = this.form.getRawValue();
    if (!datumIn || idInkoopbron === null) {
      this._snackBar.open(this.labels.aanschafnrVoorwaarde, 'Sluiten', {
        duration: 5000,
      });
      return;
    }

    if (aanschafnr && !(await this._confirmOverwrite(this.labels.overschrijfAanschafnr))) {
      return;
    }

    this._service.generateAanschafnr$(this._toInstrument()).subscribe({
      next: (result) => this._patchForm(result),
    });
  }

  /**
   * Generate a huurnr. If a number is already present, confirm before
   * overwriting.
   */
  protected async generateHuurnr(): Promise<void> {
    const { huurnr } = this.form.getRawValue();
    if (huurnr !== null && !(await this._confirmOverwrite(this.labels.overschrijfHuurnr))) {
      return;
    }

    this._service.generateHuurnr$(this._toInstrument()).subscribe({
      next: (result) => this._patchForm(result),
    });
  }

  /** Opens the overwrite-confirmation dialog; resolves to the user's choice. */
  private _confirmOverwrite(message: string): Promise<boolean> {
    const ref = this._dialog.open<
      ConfirmOverwriteDialog,
      IConfirmOverwriteData,
      boolean
    >(ConfirmOverwriteDialog, { data: { message } });
    return firstValueFrom(ref.afterClosed()).then((result) => result === true);
  }

  private _load(id: number): void {
    this.loading.set(true);
    this._service.getById$(id).subscribe({
      next: (instrument) => {
        this._patchForm(instrument);
        this.currentId.set(instrument.id);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this._snackBar.open(this.labels.nietGevonden, 'Sluiten', {
          duration: 5000,
        });
      },
    });
  }

  private _loadDropdowns(): void {
    this._typeService.getAll().subscribe({
      next: (types) => this.types.set(types),
    });
    this._bronService.getAll().subscribe({
      next: (bronnen) => this.bronnen.set(bronnen),
    });
  }

  /** Maps the current form state to the camelCase instrument contract. */
  private _toInstrument(): IInstrument {
    const value = this.form.getRawValue();
    return {
      ...(this.currentId() !== undefined ? { id: this.currentId() } : {}),
      aanschafnr: value.aanschafnr,
      huurnr: value.huurnr,
      datumIn: value.datumIn,
      idAdresIn: value.idAdresIn,
      inkoopInstr: value.inkoopInstr,
      inkoopAcc: value.inkoopAcc,
      inkoopFactuur: value.inkoopFactuur,
      idInkoopbron: value.idInkoopbron,
      idAdresTaxateur: value.idAdresTaxateur,
      verkoopBtw: value.verkoopBtw,
      omschrijvIn: value.omschrijvIn,
      datumUit: value.datumUit,
      verkoopInstr: value.verkoopInstr,
      idAdresUit: value.idAdresUit,
      reparaties: value.reparaties,
      maat: value.maat,
      antique: value.antique,
      anno: value.anno,
      idInstrType: value.idInstrType,
      foto: value.foto,
      datumTaxatie: value.datumTaxatie,
    };
  }

  /** Patches the reactive form from a (possibly partial) instrument. */
  private _patchForm(instrument: IInstrument): void {
    this.form.patchValue({
      aanschafnr: instrument.aanschafnr,
      huurnr: instrument.huurnr,
      datumIn: instrument.datumIn,
      idAdresIn: instrument.idAdresIn,
      inkoopInstr: instrument.inkoopInstr,
      inkoopAcc: instrument.inkoopAcc,
      inkoopFactuur: instrument.inkoopFactuur,
      idInkoopbron: instrument.idInkoopbron,
      idAdresTaxateur: instrument.idAdresTaxateur,
      verkoopBtw: instrument.verkoopBtw,
      omschrijvIn: instrument.omschrijvIn,
      datumUit: instrument.datumUit,
      verkoopInstr: instrument.verkoopInstr,
      idAdresUit: instrument.idAdresUit,
      reparaties: instrument.reparaties,
      maat: instrument.maat,
      antique: instrument.antique,
      anno: instrument.anno,
      idInstrType: instrument.idInstrType,
      foto: instrument.foto,
      datumTaxatie: instrument.datumTaxatie,
    });
  }
}
