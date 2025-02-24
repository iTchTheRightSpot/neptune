import { HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

// responsible for formatting error from backend
export const interceptor = (req: HttpRequest<unknown>, next: HttpHandlerFn) =>
  next(req).pipe(
    catchError(err =>
      throwError(() => ({
        message: err.error ? err.error.message : err.message,
        status: err.status
      }))
    )
  );
