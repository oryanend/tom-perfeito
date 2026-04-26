import { Component, OnInit, inject } from '@angular/core';
import { StatusService } from '../../../core/services/StatusService/status.service';
import { Status } from '../../../shared/models/status';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-status-page',
  standalone: false,
  templateUrl: './status-page.component.html',
  styleUrl: './status-page.component.css',
})
export class StatusPageComponent implements OnInit {
  private statusService = inject(StatusService);

  status?: Status;
  latencies: number[] = [];
  isLoading = false;

  ngOnInit(): void {
    this.isLoading = true;

    this.statusService
      .getStatus()
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: (data) => {
          this.status = data;
          this.latencies = Object.values(data.dependencies.database.latency);
        },
        error: (err) => {
          console.error('Erro ao buscar status:', err);
        },
      });
  }
}
