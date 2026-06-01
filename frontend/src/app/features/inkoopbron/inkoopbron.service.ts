import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IInkoopbron {
  id?: number;
  omschrijving: string;
  rapporteren: boolean;
}

/**
 * Service for inkoopbron reference data.
 * GET /api/inkoopbron  — retrieve whole list
 * PUT /api/inkoopbron  — replace whole list (three-pass backend)
 */
@Injectable({ providedIn: 'root' })
export class InkoopbronService {
  private readonly _http = inject(HttpClient);
  private readonly _baseUrl = '/api/inkoopbron';

  getAll(): Observable<IInkoopbron[]> {
    return this._http.get<IInkoopbron[]>(this._baseUrl);
  }

  saveAll(bronnen: IInkoopbron[]): Observable<IInkoopbron[]> {
    return this._http.put<IInkoopbron[]>(this._baseUrl, bronnen);
  }
}
