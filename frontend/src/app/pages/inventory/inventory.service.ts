import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiResponse, ApiState, err } from '@shared/model/shared.model';
import { DummyInventories, IInventoryModel } from './inventory.model';
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
import { environment } from '@env/environment';
import { ToastEnum, ToastService } from '@shared/data-access/toast.service';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly http = inject(HttpClient);
  private readonly toast = inject(ToastService);

  private readonly inventoryCache = new BehaviorSubject<
    IInventoryModel[] | undefined
  >(undefined);

  readonly all = () =>
    environment.production
      ? this.inventoryCache.pipe(
          switchMap(u =>
            u !== undefined
              ? of<ApiResponse<IInventoryModel[]>>({
                  state: ApiState.LOADED,
                  data: u
                })
              : this.http
                  .get<IInventoryModel[]>(`${environment.domain}inventory/all`)
                  .pipe(
                    map(
                      a =>
                        <ApiResponse<IInventoryModel[]>>{
                          state: ApiState.LOADED,
                          data: a
                        }
                    ),
                    startWith(<ApiResponse<IInventoryModel[]>>{
                      state: ApiState.LOADING
                    }),
                    catchError(e => {
                      this.toast.message({
                        message: e.message,
                        state: ToastEnum.ERROR
                      });
                      return of(err<IInventoryModel[]>(e));
                    })
                  )
          )
        )
      : merge(
          of<ApiResponse<IInventoryModel[]>>({ state: ApiState.LOADING }),
          of<ApiResponse<IInventoryModel[]>>({
            state: ApiState.LOADED,
            data: DummyInventories(20)
          }).pipe(delay(2000))
        );

  readonly create = (p: IInventoryModel) => {
    const { product_id, ...obj } = p;
    return environment.production
      ? this.http.post<any>(`${environment.domain}inventory`, obj).pipe(
          switchMap(() => {
            this.toast.message({
              message: 'inventory created',
              state: ToastEnum.SUCCESS
            });
            this.inventoryCache.next(undefined);
            return this.all().pipe(
              map(() => <ApiResponse<any>>{ state: ApiState.LOADED })
            );
          }),
          startWith(<ApiResponse<any>>{ state: ApiState.LOADING }),
          catchError(e => {
            this.toast.message({
              message: e.message,
              state: ToastEnum.ERROR
            });
            return of(err<any>(e));
          })
        )
      : merge(
          of<ApiResponse<any>>({ state: ApiState.LOADING }),
          of<ApiResponse<any>>({ state: ApiState.LOADED }).pipe(delay(2000))
        );
  };
}
