export interface IProductModel {
  name: string;
  product_id: string;
  qty: number;
}

export const DummyProducts = (num: number): IProductModel[] =>
  Array.from({ length: num }, (_, i) => ({
    name: `Pro-${i + 1}`,
    product_id: i + '',
    qty: i + 1
  }));
