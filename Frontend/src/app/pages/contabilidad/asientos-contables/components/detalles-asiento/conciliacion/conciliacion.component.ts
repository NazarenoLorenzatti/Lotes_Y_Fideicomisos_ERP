import { Component, inject, Input, OnInit } from '@angular/core';
import { AsientosService } from '../../../../../../core/services/contabilidad/asientos.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-conciliacion',
  templateUrl: './conciliacion.component.html',
  styleUrl: './conciliacion.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ConciliacionComponent implements OnInit {

  @Input() conciliacion: any = {};
  private asientosServices = inject(AsientosService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private messageService = inject(MessageService);
  public loading: boolean = false;

  ngOnInit(): void {
    console.log(this.conciliacion)
  }

  desconciliar(id: number) {
    this.loading = true;
    this.asientosServices.anularConciliaciones(id).subscribe({
      next: (response: any) => this.handleResponseOkData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }

  handleResponseOkData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      console.log(response);
      this.conciliacion = response.response[0];
      this.loading = false;
    } else {
       this.showMessage(response?.metadata?.[0]?.informacion, response?.metadata?.[0]?.respuesta, 'warn')
    }
  }

  handleResponseErrorData(error: any) {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error?.metadata?.[0]?.informacion, error?.metadata?.[0]?.respuesta, 'danger')
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }
}
