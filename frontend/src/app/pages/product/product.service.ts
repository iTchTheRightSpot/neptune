import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiResponse, ApiState, err } from '@shared/model/shared.model';
import { DummyProducts, IProductModel } from './product.model';
import { catchError, delay, map, merge, of, startWith } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly http = inject(HttpClient);

  readonly all = () =>
    environment.production
      ? this.http.get<IProductModel[]>(`${environment.domain}product`).pipe(
          map(
            a =>
              <ApiResponse<IProductModel[]>>{ state: ApiState.LOADED, data: a }
          ),
          startWith(<ApiResponse<IProductModel[]>>{ state: ApiState.LOADING }),
          catchError(e => of(err<IProductModel[]>(e)))
        )
      : merge(
          of<ApiResponse<IProductModel[]>>({ state: ApiState.LOADING }),
          of<ApiResponse<IProductModel[]>>({
            state: ApiState.LOADED,
            data: DummyProducts(20)
          }).pipe(delay(2000))
        );

  readonly create = (p: IProductModel) =>
    environment.production
      ? this.http.post<any>(`${environment.domain}product`, p).pipe(
          map(() => <ApiResponse<any>>{ state: ApiState.LOADED }),
          startWith(<ApiResponse<any>>{ state: ApiState.LOADING }),
          catchError(e => of(err<any>(e)))
        )
      : merge(
          of<ApiResponse<any>>({ state: ApiState.LOADING }),
          of<ApiResponse<any>>({ state: ApiState.LOADED }).pipe(delay(2000))
        );
}
