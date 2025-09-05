import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RecibosService } from '../../../../core/services/cobranza/recibos.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-listarecibos',
  templateUrl: './listarecibos.component.html',
  styleUrl: './listarecibos.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListaRecibosComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  private recibosService = inject(RecibosService);
  private messageService = inject(MessageService);
  public listaRecibosOfi!: any[];
  public listaRecibosAux!: any[];

  constructor() { }

  ngOnInit() {
    this.getLists();
  }

  getLists() {
    this.recibosService.listarRecibos().subscribe({
      next: (response: any) => this.handleRecibosData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });

    this.recibosService.listarRecibosAuxiliares().subscribe({
      next: (response: any) => this.handleRecibosAuxiliarData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }

  handleRecibosData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listaRecibosOfi = response.response.response[0];
    }
  }

  handleRecibosAuxiliarData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listaRecibosAux = response.response.response[0];
    }
  }

  handleResponseErrorData(error: any) {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }


  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }
}
