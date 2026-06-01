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
    <mat-toolbar color="primary" class="app-toolbar">
      <button mat-icon-button (click)="toggleSidenav()">
        <mat-icon>menu</mat-icon>
      </button>
      <span>{{ title }}</span>
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
    }

    .app-sidenav-container {
      flex: 1;
    }

    .active-link {
      background-color: rgba(0, 0, 0, 0.08);
      font-weight: 500;
    }
  `],
})
export class ShellComponent {
  protected readonly title = NL.app.title;

  protected readonly sidenavOpen = signal(true);

  protected readonly menuItems: IMenuItem[] = [
    { label: NL.nav.instrumenten, icon: 'piano', route: '/instrumenten' },
    { label: NL.nav.types, icon: 'category', route: '/types' },
    { label: NL.nav.inkoopbron, icon: 'storefront', route: '/inkoopbron' },
  ];

  protected toggleSidenav(): void {
    this.sidenavOpen.update((open) => !open);
  }
}
