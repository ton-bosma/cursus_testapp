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

/** Allowed max-results choices for the result-size select. */
export const MAX_RESULTS_OPTIONS: ReadonlyArray<{ readonly value: number | null; readonly label: string }> = [
  { value: 10, label: '10' },
  { value: 20, label: '20' },
  { value: 50, label: '50' },
  { value: 100, label: '100' },
  { value: null, label: 'Alles' },
];
