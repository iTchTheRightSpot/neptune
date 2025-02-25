export interface IInventoryModel {
  name: string;
  product_id: string;
  qty: number;
}

export const DummyInventories = (num: number): IInventoryModel[] =>
  Array.from({ length: num }, (_, i) => ({
    name: `inventory-${i + 1}`,
    product_id: i + '',
    qty: i + 1
  }));
