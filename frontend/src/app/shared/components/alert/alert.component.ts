import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css',
  standalone: true,
  imports: [NgClass],
})
export class AlertComponent {
  @Input() type: 'success' | 'warning' | 'error' | null = null;
  @Input() message = '';
}
