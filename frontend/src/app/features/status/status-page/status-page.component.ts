import { Component, OnInit, inject } from '@angular/core';
import { StatusService } from '../../../core/services/StatusService/status.service';
import { Status } from '../../../shared/models/status';

@Component({
  selector: 'app-status-page',
  standalone: false,
  templateUrl: './status-page.component.html',
  styleUrl: './status-page.component.css',
})
export class StatusPageComponent implements OnInit {
  status?: Status;
  latencies: number[] = [];

  private statusService = inject(StatusService);

  ngOnInit(): void {
    this.statusService.getStatus().subscribe({
      next: (data) => {
        this.status = data;
        this.latencies = Object.values(data.dependencies.database.latency);
      },
      error: (err) => {
        console.error('Erro ao buscar status', err);
      },
    });
  }
}
