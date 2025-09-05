import { Component, inject, OnInit } from '@angular/core';
import { FacturacionService } from '../../../../../core/services/facturacion/facturacion.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pendientes',
  templateUrl: './pendientes.component.html',
  styleUrl: './pendientes.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class PendientesComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  private facturasService = inject(FacturacionService);
  public listaPreComprobantes: any[] = [];


  constructor() { }

  ngOnInit() {
    this.getList();
  }

  getList() {
    let body = {
      id: 1,
      descripcion: "Borrador"
    }
    this.facturasService.listarPreComprobantesPorEstado(body).subscribe({
      next: (response: any) => this.handlePreComprobanteData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });
  }

  handlePreComprobanteData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listaPreComprobantes = response.response.response[0];
    }
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }

}


