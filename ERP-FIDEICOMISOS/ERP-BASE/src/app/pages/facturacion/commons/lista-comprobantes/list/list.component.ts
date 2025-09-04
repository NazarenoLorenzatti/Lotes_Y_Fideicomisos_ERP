import { Component, inject, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { FacturacionService } from '../../../../../core/services/facturacion/facturacion.service';

@Component({
  selector: 'app-list-table',
  templateUrl: './list.component.html',
  styleUrl: './list.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListComponent implements OnInit, OnChanges {


  @Input() list: any = {};
  private router = inject(Router);
  public searchValue: any = '';
  public loading: boolean = true;
  private messageService = inject(MessageService);
  private facturaService = inject(FacturacionService);

  ngOnInit(): void {
    console.log(this.list)
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['list']) {
      this.loading = false;
    }
  }

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  contraComprobante(comprobante: any) {
    let preComprobante = {
      contactoId: comprobante.preComprobante.contactoId,
      cuit_emisor: comprobante.preComprobante.cuit_emisor,
      cbte_fecha: this.formatFecha(new Date()),
      fecha_desde: comprobante.preComprobante.fecha_desde,
      fecha_hasta: comprobante.preComprobante.fecha_hasta,
      fecha_vto: this.formatFecha(new Date()),
      importe_neto: comprobante.preComprobante.importe_neto,
      importe_iva: comprobante.preComprobante.importe_iva,
      importe_gravado: comprobante.preComprobante.importe_gravado,
      importe_total: comprobante.preComprobante.importe_total,
      concepto: comprobante.preComprobante.concepto,
      moneda: comprobante.preComprobante.moneda,
      moneda_cotizacion: comprobante.preComprobante.moneda_cotizacion,
      oficial: comprobante.preComprobante.oficial,
      descripcionIva: comprobante.preComprobante.descripcionIva,
      puntoVenta: comprobante.preComprobante.puntoVenta,
      nombrePtoVenta: comprobante.preComprobante.nombrePtoVenta,
      id_Comprobante_a_cancelar: comprobante.id,
      items: []
    }
    this.setIdAfipComprobante(comprobante.preComprobante, preComprobante);
    this.savedPreContraComprobante(preComprobante);
  }

  savedPreContraComprobante(preComprobante: any) {
    this.facturaService.crearPreComprobante(preComprobante, preComprobante.contactoId).subscribe({
      next: (response: any) => this.handleResponseOKData(response),
      error: (error: any) => this.handleResponseERRORData(error)
    })
  }

  
  handleResponseOKData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.showMessage(response.response?.metadata?.[0]?.informacion, response?.metadata?.[0]?.respuesta, 'success')
      this.goToPreComprobante(response.response?.response?.[0]?.id);
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, response?.metadata?.[0]?.respuesta, 'warn')
    }
  }

  handleResponseERRORData(error: any) {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error?.metadata?.[0]?.informacion, error?.metadata?.[0]?.respuesta, 'danger')
  }

  setIdAfipComprobante(preComprobante: any, body: any) {
    switch (preComprobante.idAfipTipoComprobante) {
      case 6: // Factura B
        body.idAfipTipoComprobante = 8;
        body.descripcionTipoComprobante = "Nota de Crédito B";
        body.abrebiaturaTipoComprobante = "CB";
        break;
      case 1: // Factura A
        body.idAfipTipoComprobante = 3;
        body.descripcionTipoComprobante = "Nota de Crédito A";
        body.abrebiaturaTipoComprobante = "CA";
        break;
      case 11: // Factura C
        body.idAfipTipoComprobante = 13;
        body.descripcionTipoComprobante = "Nota de Crédito C";
        body.abrebiaturaTipoComprobante = "CC";
        break;
      case 51: // Factura M
        body.idAfipTipoComprobante = 53;
        body.descripcionTipoComprobante = "Nota de Crédito M";
        body.abrebiaturaTipoComprobante = "CM";
        break;
    }
  }

  formatFecha(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${year}${month}${day}`;
  }

  goToEditar(id: number) {
    this.router.navigate(["facturacion/views/crear", { id }]);
  }

  goToComprobante(id: number, of: boolean) {
    this.router.navigate(["facturacion/views/comprobante", id], {
      queryParams: { of: of }
    });
  }

   goToPreComprobante(id: number) {
    this.router.navigate(["facturacion/views/pre-comprobante", id ]);
  }


  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  getSeverity(status: string) {
    switch (status.toLowerCase()) {
      case 'unqualified':
        return 'danger';

      case 'qualified':
        return 'success';

      case 'new':
        return 'info';

      case 'negotiation':
        return 'warning';

      case 'renewal':
        return "";

      default:
        return "";
    }
  }
}
