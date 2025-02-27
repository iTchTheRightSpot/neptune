import { Routes } from '@angular/router';

export const RootRoutes = {
  Order: '',
  Product: 'product'
};

export const routes: Routes = [
  {
    path: RootRoutes.Order,
    loadComponent: () =>
      import('./pages/order/order.component').then(m => m.OrderComponent)
  },
  {
    path: RootRoutes.Product,
    loadComponent: () =>
      import('@pages/inventory/inventory.component').then(
        m => m.InventoryComponent
      )
  },
  { path: '**', redirectTo: `/${RootRoutes.Order}` }
];
