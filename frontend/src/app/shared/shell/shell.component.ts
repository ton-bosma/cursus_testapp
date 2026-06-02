import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NL } from '../../core/i18n/nl.labels';

interface IMenuItem {
  label: string;
  icon: string;
  route: string;
  exact?: boolean;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
  ],
  template: `
    <mat-toolbar class="app-toolbar">
      <button mat-icon-button (click)="toggleSidenav()" aria-label="Menu">
        <mat-icon>menu</mat-icon>
      </button>
      <span class="app-toolbar__title">
        {{ title }}<span class="app-toolbar__mark"></span>
      </span>
    </mat-toolbar>

    <mat-sidenav-container class="app-sidenav-container">
      <mat-sidenav
        class="app-sidenav"
        [opened]="sidenavOpen()"
        mode="side"
      >
        <mat-nav-list>
          @for (item of menuItems; track item.route) {
            <a
              mat-list-item
              [routerLink]="item.route"
              routerLinkActive="active-link"
              [routerLinkActiveOptions]="{ exact: item.exact ?? false }"
            >
              <mat-icon matListItemIcon>{{ item.icon }}</mat-icon>
              <span matListItemTitle>{{ item.label }}</span>
            </a>
          }
        </mat-nav-list>
      </mat-sidenav>

      <mat-sidenav-content class="app-sidenav-content">
        <router-outlet />
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    :host {
      display: flex;
      flex-direction: column;
      height: 100%;
    }

    .app-toolbar {
      flex-shrink: 0;
      background: var(--paper);
      color: var(--ink);
      border-bottom: 2px solid var(--ink);
    }

    .app-toolbar__title {
      font-family: var(--font-display);
      font-weight: 800;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      font-size: 1.4rem;
    }

    .app-toolbar__mark {
      display: inline-block;
      width: 0.42rem;
      height: 0.42rem;
      margin-left: 0.4rem;
      background: var(--vermilion);
      vertical-align: middle;
    }

    .app-sidenav-container {
      flex: 1;
    }

    .app-sidenav {
      border-right: 1.5px solid var(--ink);
    }

    .active-link {
      background: var(--paper-2);
      box-shadow: inset 4px 0 0 var(--vermilion);
    }
  `],
})
export class ShellComponent {
  protected readonly title = NL.app.title;

  protected readonly sidenavOpen = signal(true);

  protected readonly menuItems: IMenuItem[] = [
    { label: NL.nav.overzicht, icon: 'dashboard', route: '/', exact: true },
    { label: NL.nav.instrumenten, icon: 'piano', route: '/instrumenten' },
    { label: NL.nav.types, icon: 'category', route: '/types' },
    { label: NL.nav.inkoopbron, icon: 'storefront', route: '/inkoopbron' },
  ];

  protected toggleSidenav(): void {
    this.sidenavOpen.update((open) => !open);
  }
}
