import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { Menubar } from 'primeng/menubar';
import { ModeComponent } from '@shared/ui/mode.component';
import { RootRoutes } from '@root/app.routes';

@Component({
  selector: 'app-navigation',
  imports: [ModeComponent, Menubar],
  template: `
    <div class="w-full">
      <p-menubar [model]="items">
        <ng-template #start>
          <h1 class="text-lg font-semibold">Neptune Coding Challenge</h1>
        </ng-template>
        <ng-template #end>
          <app-mode />
        </ng-template>
      </p-menubar>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavigationComponent {
  protected readonly items: MenuItem[] = [
    {
      label: 'Order',
      icon: 'pi pi-shopping-cart',
      routerLink: `/${RootRoutes.Order}`
    },
    {
      label: 'Inventory',
      icon: 'pi pi-shopping-bag',
      routerLink: `/${RootRoutes.Inventory}`
    }
  ];
}
