import { IProductModel } from '@product/product.model';

export interface IOrderModel {
  order_id: string;
  product_id: string;
  qty: number;
  status: boolean;
}

export interface IOrderDetailsModel {
  order_id: string;
  qty: number;
  status: boolean;
  product: IProductModel;
}

export const DummyOrders = (num: number): IOrderModel[] =>
  Array.from({ length: num }, (_, i) => ({
    order_id: `${i + 1}`,
    product_id: i + '',
    qty: i + 1,
    status: i % 2 === 0
  }));
