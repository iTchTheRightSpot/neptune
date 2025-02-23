import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavigationComponent } from '@shared/ui/navigation.component';
import { Toast } from 'primeng/toast';
import { tap } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ToastEnum, ToastService } from '@shared/data-access/toast.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationComponent, Toast, AsyncPipe],
  providers: [MessageService],
  template: `
    <p-toast />
    @if (toast$ | async) {}
    <div class="w-full xl:max-w-7xl m-auto">
      <div class="w-full mb-3 sticky top-0 z-20">
        <app-navigation />
      </div>

      <div class="w-full pb-2 px-1">
        <router-outlet />
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  private readonly ms = inject(MessageService);
  protected readonly toast$ = inject(ToastService).toast$.pipe(
    tap(obj => {
      if (obj.state === ToastEnum.ERROR)
        this.ms.add({
          severity: 'error',
          summary: 'Error',
          detail: obj.message
        });
      else if (obj.state === ToastEnum.SUCCESS)
        this.ms.add({
          severity: 'success',
          summary: 'Success',
          detail: obj.message
        });
    })
  );
}
