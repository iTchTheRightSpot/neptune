import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import {
  BehaviorSubject,
  catchError,
  delay,
  map,
  merge,
  of,
  startWith,
  switchMap
} from 'rxjs';
import { ApiResponse, ApiState, err } from '@shared/model/shared.model';
import { DummyOrders, IOrderDetailsModel, IOrderModel } from './order.model';
import { DummyProducts, IProductModel } from '@product/product.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly ordersCache = new BehaviorSubject<IOrderModel[] | undefined>(
    undefined
  );
  private readonly orderDetailCache: {
    key: string;
    data: IOrderDetailsModel;
  }[] = [];

  readonly orderdetail = (o: IOrderModel) => {
    const find = this.orderDetailCache.find(obj => obj.key === o.product_id);
    if (find)
      return of<ApiResponse<IOrderDetailsModel>>({
        state: ApiState.LOADED,
        data: find.data
      });

    return environment.production
      ? this.http
          .get<IProductModel>(`${environment.domain}product/${o.product_id}`)
          .pipe(
            map(p => {
              const d: IOrderDetailsModel = {
                order_id: o.order_id,
                status: o.status,
                qty: o.qty,
                product: p
              };
              this.orderDetailCache.push({ key: o.product_id, data: d });
              return { state: ApiState.LOADED, data: d };
            }),
            startWith(<ApiResponse<IOrderDetailsModel>>{
              state: ApiState.LOADING
            }),
            catchError(e => of(err<IOrderDetailsModel>(e)))
          )
      : merge(
          of<ApiResponse<IOrderDetailsModel>>({ state: ApiState.LOADING }),
          of<ApiResponse<IOrderDetailsModel>>({
            state: ApiState.LOADED,
            data: {
              order_id: o.order_id,
              status: o.status,
              qty: o.qty,
              product: DummyProducts(1)[0]
            }
          }).pipe(delay(2000))
        );
  };

  readonly all = () =>
    environment.production
      ? this.ordersCache.pipe(
          switchMap(e =>
            e !== undefined
              ? of<ApiResponse<IOrderModel[]>>({
                  state: ApiState.LOADED,
                  data: e
                })
              : this.http.get<IOrderModel[]>(`${environment.domain}order`).pipe(
                  map(
                    a =>
                      <ApiResponse<IOrderModel[]>>{
                        state: ApiState.LOADED,
                        data: a
                      }
                  ),
                  startWith(<ApiResponse<IOrderModel[]>>{
                    state: ApiState.LOADING
                  }),
                  catchError(e => of(err<IOrderModel[]>(e)))
                )
          )
        )
      : merge(
          of<ApiResponse<IOrderModel[]>>({ state: ApiState.LOADING }),
          of<ApiResponse<IOrderModel[]>>({
            state: ApiState.LOADED,
            data: DummyOrders(20)
          }).pipe(delay(2000))
        );

  readonly create = (p: IOrderModel) => {
    const { order_id, ...obj } = p;
    return environment.production
      ? this.http.post<any>(`${environment.domain}order`, obj).pipe(
          switchMap(() => {
            this.ordersCache.next(undefined);
            return this.all().pipe(
              map(() => <ApiResponse<any>>{ state: ApiState.LOADED })
            );
          }),
          startWith(<ApiResponse<any>>{ state: ApiState.LOADING }),
          catchError(e => of(err<any>(e)))
        )
      : merge(
          of<ApiResponse<any>>({ state: ApiState.LOADING }),
          of<ApiResponse<any>>({ state: ApiState.LOADED }).pipe(delay(2000))
        );
  };
}
