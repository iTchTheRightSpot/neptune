import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiResponse, ApiState, err } from '@shared/model/shared.model';
import { DummyProducts, IProductModel } from './product.model';
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

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly productCache = new BehaviorSubject<
    IProductModel[] | undefined
  >(undefined);

  readonly all = () =>
    environment.production
      ? this.productCache.pipe(
          switchMap(u =>
            u !== undefined
              ? of<ApiResponse<IProductModel[]>>({
                  state: ApiState.LOADED,
                  data: u
                })
              : this.http
                  .get<IProductModel[]>(`${environment.domain}product/all`)
                  .pipe(
                    map(
                      a =>
                        <ApiResponse<IProductModel[]>>{
                          state: ApiState.LOADED,
                          data: a
                        }
                    ),
                    startWith(<ApiResponse<IProductModel[]>>{
                      state: ApiState.LOADING
                    }),
                    catchError(e => of(err<IProductModel[]>(e)))
                  )
          )
        )
      : merge(
          of<ApiResponse<IProductModel[]>>({ state: ApiState.LOADING }),
          of<ApiResponse<IProductModel[]>>({
            state: ApiState.LOADED,
            data: DummyProducts(20)
          }).pipe(delay(2000))
        );

  readonly create = (p: IProductModel) => {
    const { product_id, ...obj } = p;
    return environment.production
      ? this.http.post<any>(`${environment.domain}product`, obj).pipe(
          switchMap(() => {
            this.productCache.next(undefined);
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
