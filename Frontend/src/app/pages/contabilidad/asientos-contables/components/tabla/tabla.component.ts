import { Component, inject, OnInit } from '@angular/core';
import { AsientosService } from '../../../../../core/services/contabilidad/asientos.service';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tabla',
  templateUrl: './tabla.component.html',
  styleUrl: './tabla.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class TablaComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  private asientosServices = inject(AsientosService);
  private messageService = inject(MessageService);
  public listAsientosOficiales!: any[];
  public listAsientosAuxiliares!: any[];

  ngOnInit(): void {
    this.obtenerListaAsientos();
  }

  obtenerListaAsientos() {
    this.asientosServices.listarAsientos().subscribe({
      next: (response: any) => this.handleResponseOkData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }

  handleResponseOkData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      const rawAsientos = response.response.response[0];
      this.listAsientosOficiales = rawAsientos.filter((a: any) => a.oficial);
      this.listAsientosAuxiliares = rawAsientos.filter((a: any) => !a.oficial);
       console.log(this.listAsientosOficiales)
      this.loading = false;
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
