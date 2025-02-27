import { IInventoryModel } from '@pages/inventory/inventory.model';

export enum OrderStatus {
  CONFIRMED = 'CONFIRMED',
  PENDING = 'PENDING'
}

export interface IOrderModel {
  order_id: string;
  product_id: string;
  qty: number;
  status: OrderStatus;
}

export interface IOrderDetailsModel {
  order_id: string;
  qty: number;
  status: OrderStatus;
  product: IInventoryModel;
}

export const DummyOrders = (num: number): IOrderModel[] =>
  Array.from({ length: num }, (_, i) => ({
    order_id: `${i + 1}`,
    product_id: i + '',
    qty: i + 1,
    status: i % 2 === 0 ? OrderStatus.CONFIRMED : OrderStatus.PENDING
  }));
