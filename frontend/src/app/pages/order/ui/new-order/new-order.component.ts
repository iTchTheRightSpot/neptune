import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  model,
  output
} from '@angular/core';
import { ApiResponse, ApiState } from '@shared/model/shared.model';
import { IOrderModel } from '@order/order.model';
import { Select } from 'primeng/select';
import { IInventoryModel } from '@pages/inventory/inventory.model';
import {
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { InputNumber } from 'primeng/inputnumber';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-new-order',
  imports: [Select, ReactiveFormsModule, InputNumber, Button],
  templateUrl: './new-order.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewOrderComponent {
  products = input.required<ApiResponse<IInventoryModel[]>>();
  loading = input.required<ApiState>();
  visible = model.required<boolean>();
  readonly emitter = output<IOrderModel>();

  protected readonly state = ApiState;
  protected form = inject(FormBuilder).group({
    product: new FormControl<IInventoryModel | null>(null, [Validators.required]),
    qty: new FormControl<number | null>(null, [Validators.required]),
    status: new FormControl<boolean | null>(null, [Validators.required])
  });

  protected readonly submit = () =>
    this.form.invalid
      ? {}
      : this.emitter.emit({
          order_id: '',
          product_id: this.form.value.product?.product_id || '',
          qty: Number(this.form.value.qty!!),
          status: this.form.value.status || false
        });
}
