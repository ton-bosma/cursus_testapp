import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IInstrumentType {
  id?: number;
  omschrijving: string;
  forfaitAccessoires: number;
}

/**
 * Service for instrument type reference data.
 * GET /api/instrumenttype  — retrieve whole list
 * PUT /api/instrumenttype  — replace whole list (three-pass backend)
 */
@Injectable({ providedIn: 'root' })
export class InstrumenttypeService {
  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = '/api/instrumenttype';

  getAll(): Observable<IInstrumentType[]> {
    return this._http.get<IInstrumentType[]>(this._baseUrl);
  }

  saveAll(types: IInstrumentType[]): Observable<IInstrumentType[]> {
    return this._http.put<IInstrumentType[]>(this._baseUrl, types);
  }
}
