import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { IInstrument, IInstrumentRow, IInstrumentSearchParams } from './instrument.model';

/**
 * Typed data-access service for the Instrument feature.
 *
 * Talks to the backend search contract:
 *   `GET /api/instrument?q=&archief=&max=`
 */
@Injectable({ providedIn: 'root' })
export class InstrumentService {
  private static readonly _baseUrl = '/api/instrument';

  private readonly _http = inject(HttpClient);

  /**
   * Searches instruments. The `max` parameter is omitted when `null`
   * ("Alles"), letting the backend apply its default/unbounded behaviour.
   */
  public search$(params: IInstrumentSearchParams): Observable<IInstrumentRow[]> {
    let httpParams = new HttpParams()
      .set('q', params.q)
      .set('archief', params.archief);

    if (params.max !== null) {
      httpParams = httpParams.set('max', params.max);
    }

    return this._http.get<IInstrumentRow[]>(InstrumentService._baseUrl, {
      params: httpParams,
    });
  }

  /** Loads a single instrument by id: `GET /api/instrument/{id}`. */
  public getById$(id: number): Observable<IInstrument> {
    return this._http.get<IInstrument>(`${InstrumentService._baseUrl}/${id}`);
  }

  /** Saves (creates/updates) an instrument and returns the persisted row: `PUT /api/instrument`. */
  public save$(instrument: IInstrument): Observable<IInstrument> {
    return this._http.put<IInstrument>(InstrumentService._baseUrl, instrument);
  }

  /**
   * Generates an acquisition number for the given instrument:
   * `PUT /api/instrument/aanschafnummer`. Returns the instrument with the
   * generated `aanschafnr`.
   */
  public generateAanschafnr$(instrument: IInstrument): Observable<IInstrument> {
    return this._http.put<IInstrument>(
      `${InstrumentService._baseUrl}/aanschafnummer`,
      instrument,
    );
  }

  /**
   * Generates a rental number for the given instrument:
   * `PUT /api/instrument/huurnummer`. Returns the instrument with the
   * generated `huurnr`.
   */
  public generateHuurnr$(instrument: IInstrument): Observable<IInstrument> {
    return this._http.put<IInstrument>(
      `${InstrumentService._baseUrl}/huurnummer`,
      instrument,
    );
  }

  /** Deletes an instrument by id: `DELETE /api/instrument/{id}`. */
  public delete$(id: number): Observable<void> {
    return this._http.delete<void>(`${InstrumentService._baseUrl}/${id}`);
  }
}
