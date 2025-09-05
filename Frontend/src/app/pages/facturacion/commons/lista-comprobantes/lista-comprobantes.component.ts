import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FacturacionService } from '../../../../core/services/facturacion/facturacion.service';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-lista-comprobantes',
  templateUrl: './lista-comprobantes.component.html',
  styleUrl: './lista-comprobantes.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListaComprobantesComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  private facturasService = inject(FacturacionService);
  public listaCompOfi!: any[];
  public listaCompAux!: any[];

  constructor() { }

  ngOnInit() {
    this.getLists();
  }

  getLists() {
    this.facturasService.listarComprobantes().subscribe({
      next: (response: any) => this.handleComprobanteData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });

    this.facturasService.listarComprobantesAuxiliares().subscribe({
      next: (response: any) => this.handleComprobanteAuxiliarData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });
  }

  handleComprobanteData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listaCompOfi = response.response.response[0];
    }
  }

  handleComprobanteAuxiliarData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listaCompAux = response.response.response[0];
    }
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }

}
