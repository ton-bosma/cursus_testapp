import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NL } from '../i18n/nl.labels';

/**
 * Functional HTTP error interceptor (ADR-006).
 *
 * Policy:
 *  - Logs the error to the console (never silently swallows).
 *  - Shows a Dutch notification via MatSnackBar.
 *  - Re-throws the error so callers can handle or surface it further.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: unknown) => {
      const message = resolveMessage(error);

      // ADR-006: always log, never swallow
      console.error('[HTTP Error]', error);

      snackBar.open(message, 'Sluiten', {
        duration: 5000,
        panelClass: ['app-snack-error'],
      });

      // Re-throw so upstream subscribers / route guards can react
      return throwError(() => error);
    }),
  );
};

function resolveMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    switch (error.status) {
      case 401:
      case 403:
        return NL.errors.httpUnauthorized;
      case 404:
        return NL.errors.httpNotFound;
      case 0:
      case 500:
      case 502:
      case 503:
        return NL.errors.httpServerError;
      default:
        return NL.errors.httpGeneric;
    }
  }
  return NL.errors.httpGeneric;
}
