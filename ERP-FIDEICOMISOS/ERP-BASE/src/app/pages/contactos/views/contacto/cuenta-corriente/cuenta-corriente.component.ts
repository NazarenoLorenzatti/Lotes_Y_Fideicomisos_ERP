import { Component, inject, Input, OnInit } from '@angular/core';
import { CuentaCorrienteService } from '../../../../../core/services/contactos/cuenta-corriente.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-cuenta-corriente',
  templateUrl: './cuenta-corriente.component.html',
  styleUrl: './cuenta-corriente.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class CuentaCorrienteComponent implements OnInit {


  @Input() cuentaCorriente: any = {};

  private cuentasService = inject(CuentaCorrienteService);
  private messageService = inject(MessageService);
  public aplicaciones: any = {};


  expandedRows: { [key: number]: boolean } = {};
  aplicacionesMap: { [key: number]: any[] } = {};


  ngOnInit(): void {
    console.log(this.cuentaCorriente)
  }

  onRowExpand(event: any) {
    const id = event.data.id;
    this.expandedRows[id] = true;
    //this.getAplicaciones(id);
  }

  onRowCollapse(event: any) {
    const id = event.data.id;
    delete this.expandedRows[id];
  }

  toggleRow(movimiento: any) {
    const id = movimiento.id;
    if (this.expandedRows[id]) {
      delete this.expandedRows[id];
    } else {
      this.expandedRows[id] = true;
      if (movimiento?.getAplicacionesDestino?.length != 0
        || movimiento?.getAplicacionesOrigen?.length != 0) {
        if (movimiento.tipoMovimiento == 'CREDITO') {
          this.getAplicacionesOrigen(id);
        } else {
          this.getAplicacionesDestino(id);
        }
      }
    }
  }

  isRowExpanded(movimiento: any): boolean {
    return !!this.expandedRows[movimiento.id];
  }

  getAplicacionesOrigen(id: number) {
    this.cuentasService.obtenerAplicacionesOrigen(id).subscribe({
      next: (response: any) => this.handleAplicionesData(response, id),
      error: (error: any) => this.handleError(error),
    });
  }

  getAplicacionesDestino(id: number) {
    this.cuentasService.obtenerAplicacionesDestino(id).subscribe({
      next: (response: any) => this.handleAplicionesData(response, id),
      error: (error: any) => this.handleError(error),
    });
  }

  handleAplicionesData(response: any, idMov: number) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.aplicacionesMap[idMov] = response.response.response[0];
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'warn');
    }
  }

  handleError(error: any) {
    const errorMessage = error.error?.metadata[0]?.informacion || 'Ocurrió un error inesperado.';
    console.error('Error:', error);
    this.showMessage(errorMessage, 'Error', 'error');
  }

  // Muestra un mensaje
  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  get saldoOficial(): number {
    if (!this.cuentaCorriente?.movimientos) return 0;

    return this.cuentaCorriente.movimientos
      .filter((m: any) => m.oficial)
      .reduce((acc: number, m: any) => {
        return acc + (m.tipoMovimiento === 'CREDITO' ? m.importe : -m.importe);
      }, 0);
  }

  get saldoAuxiliar(): number {
    if (!this.cuentaCorriente?.movimientos) return 0;

    return this.cuentaCorriente.movimientos
      .filter((m: any) => !m.oficial)
      .reduce((acc: number, m: any) => {
        return acc + (m.tipoMovimiento === 'CREDITO' ? m.importe : -m.importe);
      }, 0);
  }

}
