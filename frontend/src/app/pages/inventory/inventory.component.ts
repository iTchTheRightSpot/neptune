import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { InventoryService } from './inventory.service';
import { ApiResponse, ApiState } from '@shared/model/shared.model';
import { Dialog } from 'primeng/dialog';
import { NewInventoryComponent } from './ui/new-inventory.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { IInventoryModel } from './inventory.model';
import { Subject, switchMap } from 'rxjs';
import { Button } from 'primeng/button';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-inventory',
  imports: [Dialog, NewInventoryComponent, Button, TableModule],
  templateUrl: './inventory.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InventoryComponent {
  private readonly service = inject(InventoryService);

  protected first = 0;
  protected rows = 5;
  protected readonly state = ApiState;
  protected readonly thead = ['Name', 'Quantity'];
  protected toggleNewProduct = false;

  protected readonly inventories = toSignal(this.service.all().pipe(), {
    initialValue: <ApiResponse<IInventoryModel[]>>{
      state: ApiState.LOADING,
      data: []
    }
  });

  protected readonly create = new Subject<IInventoryModel>();
  protected readonly createState = toSignal(
    this.create.asObservable().pipe(switchMap(o => this.service.create(o))),
    { initialValue: <ApiResponse<any>>{ state: ApiState.LOADED } }
  );
}
