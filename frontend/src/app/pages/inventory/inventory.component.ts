import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { InventoryService } from './inventory.service';
import { ApiResponse, ApiState } from '@shared/model/shared.model';
import { Dialog } from 'primeng/dialog';
import { NewInventoryComponent } from './ui/new-inventory.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { IInventoryModel } from './inventory.model';
import { Subject, switchMap, tap } from 'rxjs';
import { Button } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { ToastEnum, ToastService } from '@shared/data-access/toast.service';

@Component({
  selector: 'app-inventory',
  imports: [Dialog, NewInventoryComponent, Button, TableModule],
  templateUrl: './inventory.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InventoryComponent {
  private readonly service = inject(InventoryService);
  private readonly toast = inject(ToastService);

  protected first = 0;
  protected rows = 5;
  protected readonly state = ApiState;
  protected readonly thead = ['Name', 'Quantity'];
  protected toggleNewProduct = false;

  protected readonly inventories = toSignal(
    this.service.all().pipe(
      tap(s => {
        if (s.state === ApiState.ERROR)
          this.toast.message({
            message: s.message || 'error occurred',
            state: ToastEnum.ERROR
          });
      })
    ),
    {
      initialValue: <ApiResponse<IInventoryModel[]>>{
        state: ApiState.LOADING,
        data: []
      }
    }
  );

  protected readonly create = new Subject<IInventoryModel>();
  protected readonly createState = toSignal(
    this.create.asObservable().pipe(
      switchMap(o => this.service.create(o)),
      tap(s => {
        if (s.state === ApiState.LOADED)
          this.toast.message({
            message: 'inventory created',
            state: ToastEnum.SUCCESS
          });
        else if (s.state === ApiState.ERROR)
          this.toast.message({
            message: s.message || 'error occurred',
            state: ToastEnum.ERROR
          });
      })
    ),
    { initialValue: <ApiResponse<any>>{ state: ApiState.LOADED } }
  );
}
