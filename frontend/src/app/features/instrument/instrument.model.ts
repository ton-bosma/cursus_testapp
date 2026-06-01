/**
 * Domain model for the Instrument feature.
 *
 * Mirrors the backend search contract: `GET /api/instrument?q=&archief=&max=`
 * returns a list of instrument rows.
 */

/** A single row in the instrument search result table. */
export interface IInstrumentRow {
  /** Technical identifier, used for detail navigation. */
  id: number;
  /** Rental number (huurnummer); displayed left-padded to 4 digits. */
  huurnr: number;
  /** Acquisition number (aanschafnummer). */
  aanschafnr: string;
  /** Instrument type description. */
  type: string;
  /** Size (maat), e.g. for string instruments. */
  maat: string;
  /** Purchase source / address (inkoop). */
  inkoopAdres: string;
  /** Sale destination / address (verkoop). */
  verkoopAdres: string;
  /** Purchase date (aanschafdatum), ISO-8601 string or null. */
  aanschafdatum: string | null;
  /** Sale date (verkoopdatum), ISO-8601 string or null. */
  verkoopdatum: string | null;
}

/** Query parameters for the instrument search endpoint. */
export interface IInstrumentSearchParams {
  /** Free-text search term. */
  q: string;
  /** Include archived (sold/returned) instruments when true. */
  archief: boolean;
  /** Maximum number of rows to return; `null` means "Alles" (no limit). */
  max: number | null;
}

/**
 * Full instrument detail contract (camelCase).
 *
 * Mirrors the backend contract for `GET /api/instrument/{id}` and
 * `PUT /api/instrument`. Numeric/optional fields are nullable to model an
 * empty (new) instrument; `id` is absent for a not-yet-persisted instrument.
 */
export interface IInstrument {
  /** Technical identifier; absent for a new (unsaved) instrument. */
  id?: number;
  /** Acquisition number (aanschafnummer). */
  aanschafnr: string | null;
  /** Rental number (huurnummer). */
  huurnr: number | null;
  /** Purchase date (datum in), ISO-8601 string or null. */
  datumIn: string | null;
  /** Address-in reference. */
  idAdresIn: number | null;
  /** Purchase price of the instrument (inkoop instrument). */
  inkoopInstr: number | null;
  /** Purchase price of accessories (inkoop accessoires). */
  inkoopAcc: number | null;
  /** Purchase invoice number (inkoopfactuur). */
  inkoopFactuur: string | null;
  /** Purchase source reference (inkoopbron). */
  idInkoopbron: number | null;
  /** Appraiser address reference (taxateur). */
  idAdresTaxateur: number | null;
  /** Sale VAT (verkoop-BTW). */
  verkoopBtw: number | null;
  /** Free-text description recorded at purchase (omschrijving in). */
  omschrijvIn: string | null;
  /** Sale date (datum uit), ISO-8601 string or null. */
  datumUit: string | null;
  /** Sale price of the instrument (verkoop instrument). */
  verkoopInstr: number | null;
  /** Address-out reference (koper). */
  idAdresUit: number | null;
  /** Repairs notes (reparaties). */
  reparaties: string | null;
  /** Size (maat). */
  maat: string | null;
  /** Whether the instrument is antique. */
  antique: boolean;
  /** Year of construction (anno). */
  anno: number | null;
  /** Instrument type reference. */
  idInstrType: number | null;
  /** Photo reference / filename. */
  foto: string | null;
  /** Appraisal date (datum taxatie), ISO-8601 string or null. */
  datumTaxatie: string | null;
}

/** Allowed max-results choices for the result-size select. */
export const MAX_RESULTS_OPTIONS: ReadonlyArray<{ readonly value: number | null; readonly label: string }> = [
  { value: 10, label: '10' },
  { value: 20, label: '20' },
  { value: 50, label: '50' },
  { value: 100, label: '100' },
  { value: null, label: 'Alles' },
];
