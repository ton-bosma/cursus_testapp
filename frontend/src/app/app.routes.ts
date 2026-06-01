import { Routes } from '@angular/router';

export const appRoutes: Routes = [
  {
    path: '',
    redirectTo: 'instrumenten',
    pathMatch: 'full',
  },
  {
    path: 'instrumenten',
    loadComponent: () =>
      import('./features/instrument/instrument-list.component').then(
        (m) => m.InstrumentListComponent,
      ),
  },
  {
    path: 'types',
    loadComponent: () =>
      import('./features/instrumenttype/instrumenttype-list.component').then(
        (m) => m.InstrumenttypeListComponent,
      ),
  },
  {
    path: 'inkoopbron',
    loadComponent: () =>
      import('./features/inkoopbron/inkoopbron-list.component').then(
        (m) => m.InkoopbronListComponent,
      ),
  },
  {
    path: '**',
    redirectTo: 'instrumenten',
  },
];
