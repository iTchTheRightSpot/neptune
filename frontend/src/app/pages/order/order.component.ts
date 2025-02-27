import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ApiResponse, ApiState } from '@shared/model/shared.model';
import { toSignal } from '@angular/core/rxjs-interop';
import { Subject, switchMap } from 'rxjs';
import { OrderService } from './order.service';
import {
  IOrderDetailsModel,
  IOrderModel,
  OrderStatus
} from '@order/order.model';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { NewOrderComponent } from './ui/new-order/new-order.component';
import { OrderDetailsComponent } from './ui/order-details/order-details.component';
import { Badge } from 'primeng/badge';
import { InventoryService } from '@pages/inventory/inventory.service';
import { IInventoryModel } from '@pages/inventory/inventory.model';

@Component({
  selector: 'app-order',
  imports: [
    Button,
    Dialog,
    TableModule,
    NewOrderComponent,
    OrderDetailsComponent,
    Badge
  ],
  templateUrl: './order.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderComponent {
  private readonly service = inject(OrderService);
  private readonly inventoryService = inject(InventoryService);

  protected first = 0;
  protected rows = 5;
  protected readonly state = ApiState;
  protected readonly orderstatus = OrderStatus;
  protected readonly thead = ['Quantity', 'Status'];
  protected toggleNewOrder = false;
  protected toggleOrderDetails = false;

  protected readonly inventories = toSignal(
    this.inventoryService.all().pipe(),
    {
      initialValue: <ApiResponse<IInventoryModel[]>>{
        state: ApiState.LOADING,
        data: []
      }
    }
  );

  protected readonly emitOrderDetail = new Subject<IOrderModel>();
  protected readonly orderDetail = toSignal(
    this.emitOrderDetail
      .asObservable()
      .pipe(switchMap(o => this.service.orderdetail(o))),
    {
      initialValue: <ApiResponse<IOrderDetailsModel>>{ state: ApiState.LOADING }
    }
  );

  protected readonly orders = toSignal(this.service.all().pipe(), {
    initialValue: <ApiResponse<IOrderModel[]>>{
      state: ApiState.LOADING,
      data: []
    }
  });

  protected readonly create = new Subject<IOrderModel>();
  protected readonly createState = toSignal(
    this.create.asObservable().pipe(switchMap(o => this.service.create(o))),
    { initialValue: <ApiResponse<any>>{ state: ApiState.LOADED } }
  );
}
