import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  model,
  output
} from '@angular/core';
import { ApiState } from '@shared/model/shared.model';
import { IProductModel } from '@product/product.model';
import {
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Button } from 'primeng/button';
import { InputNumber } from 'primeng/inputnumber';
import { InputText } from 'primeng/inputtext';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';

@Component({
  selector: 'app-new-product',
  imports: [
    Button,
    ReactiveFormsModule,
    InputNumber,
    InputText,
    IconField,
    InputIcon
  ],
  template: `
    <form [formGroup]="form">
      <div class="gap-2 grid grid-cols-1 md:grid-cols-2">
        <p-iconfield>
          <p-inputicon styleClass="pi pi-search" />
          <input
            class="w-full"
            type="text"
            pInputText
            placeholder="Name"
            formControlName="name"
          />
        </p-iconfield>
        <p-input-number placeholder="quantity" inputId="integeronly" formControlName="qty" />
      </div>

      <div class="mt-5 flex justify-end gap-2">
        <p-button
          label="Cancel"
          severity="secondary"
          (click)="visible.set(false)"
        />
        <p-button
          label="Create"
          (click)="submit()"
          [disabled]="form.invalid || loading() === state.LOADING"
        />
      </div>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewProductComponent {
  loading = input.required<ApiState>();
  visible = model.required<boolean>();
  readonly emitter = output<IProductModel>();

  protected readonly state = ApiState;

  protected form = inject(FormBuilder).group({
    name: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(50)
    ]),
    qty: new FormControl<number | null>(null, [Validators.required])
  });

  protected readonly submit = () =>
    this.form.invalid
      ? {}
      : this.emitter.emit({
          name: this.form.value.name || '',
          qty: Number(this.form.value.qty!!)
        });
}
