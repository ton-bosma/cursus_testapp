import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { IInstrumentRow, IInstrumentSearchParams } from './instrument.model';

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
}
