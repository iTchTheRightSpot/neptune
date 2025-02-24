import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ProductService } from './product.service';
import { ApiResponse, ApiState } from '@shared/model/shared.model';
import { Dialog } from 'primeng/dialog';
import { NewProductComponent } from './ui/new-product.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { IProductModel } from './product.model';
import { Subject, switchMap, tap } from 'rxjs';
import { Button } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { ToastEnum, ToastService } from '@shared/data-access/toast.service';

@Component({
  selector: 'app-product',
  imports: [Dialog, NewProductComponent, Button, TableModule],
  templateUrl: './product.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductComponent {
  private readonly service = inject(ProductService);
  private readonly toast = inject(ToastService);

  protected first = 0;
  protected rows = 5;
  protected readonly state = ApiState;
  protected readonly thead = ['Name', 'Quantity'];
  protected toggleNewProduct = false;

  protected readonly products = toSignal(this.service.all(), {
    initialValue: <ApiResponse<IProductModel[]>>{
      state: ApiState.LOADING,
      data: []
    }
  });

  protected readonly create = new Subject<IProductModel>();
  protected readonly createState = toSignal(
    this.create.asObservable().pipe(
      switchMap(o => this.service.create(o)),
      tap(s => {
        if (s.state === ApiState.LOADED)
          this.toast.message({
            message: 'product created',
            state: ToastEnum.SUCCESS
          });
      })
    ),
    { initialValue: <ApiResponse<any>>{ state: ApiState.LOADED } }
  );
}
