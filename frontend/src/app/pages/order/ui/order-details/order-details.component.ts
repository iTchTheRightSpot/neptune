import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ApiResponse, ApiState } from '@shared/model/shared.model';
import { IOrderDetailsModel } from '@order/order.model';
import { InputNumber } from 'primeng/inputnumber';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Divider } from 'primeng/divider';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { InputText } from 'primeng/inputtext';
import { Message } from 'primeng/message';
import { ProgressSpinner } from 'primeng/progressspinner';

@Component({
  selector: 'app-order-details',
  imports: [
    InputNumber,
    ReactiveFormsModule,
    Divider,
    FormsModule,
    IconField,
    InputIcon,
    InputText,
    Message,
    ProgressSpinner
  ],
  templateUrl: './order-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDetailsComponent {
  obj = input.required<ApiResponse<IOrderDetailsModel>>();
  protected readonly state = ApiState;
}
