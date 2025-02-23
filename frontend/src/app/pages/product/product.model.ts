export interface IProductModel {
  name: string;
  qty: number;
}

export const DummyProducts = (num: number): IProductModel[] =>
  Array.from({ length: num }, (_, i) => ({ name: `Pro-${i + 1}`, qty: i + 1 }));
