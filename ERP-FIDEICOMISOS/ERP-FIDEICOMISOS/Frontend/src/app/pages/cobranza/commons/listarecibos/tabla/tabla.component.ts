import { Component, inject, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { MessageService } from 'primeng/api';
import { RecibosService } from '../../../../../core/services/cobranza/recibos.service';
import { Router } from '@angular/router';
import { Table } from 'primeng/table';
import { AsientosService } from '../../../../../core/services/contabilidad/asientos.service';

@Component({
  selector: 'app-tabla-recibos',
  templateUrl: './tabla.component.html',
  styleUrl: './tabla.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class TablaComponent implements OnInit, OnChanges {

  public searchValue: any = '';
  public loading: boolean = true;
  private router = inject(Router);
  private messageService = inject(MessageService);
  private recibosService = inject(RecibosService);
  private asientosService = inject(AsientosService);
  public showAlert: boolean = false;
  public idAsiento!: number;

  @Input() list: any = {};

  ngOnInit(): void {
    console.log(this.list)
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['list']) {
      this.loading = false;
    }
  }

  contraRecibo(recibo: any) {
    this.findAsientoContable(recibo);
  }


  findAsientoContable(recibo: any) {
    let referenciaExterna = "UUID-" + recibo.numero_recibo;
    this.asientosService.getAsientoByReferencia(referenciaExterna).subscribe({
      next: (response: any) => {
        this.idAsiento = response.response.response[0].id;
        if (response.response.response[0].conciliacion) {
          this.showAlert = true;
        } else {
          this.buildContraRecibo(recibo);
        }
      },
      error: (error: any) => this.handleResponseErrorData(error)
    })
  }

  buildContraRecibo(recibo: any) {
    this.recibosService.contraRecibo(recibo.preRecibo.id).subscribe({
      next: (response: any) => this.handleRecibosData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }


  handleRecibosData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.showMessage(response.response?.metadata[0]?.informacion, response.response?.metadata?.[0]?.respuesta, 'success')
    }
  }

  handleResponseErrorData(error: any) {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
  }

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  goToAsiento(id: number) {
    this.router.navigate(["contabilidad/asientos/detalles/", { id }]);
  }

  goToEditar(id: number) {
    this.router.navigate(["cobranza/views/crear", { id }]);
  }

  goToRecibo(id: number, of: boolean) {
    this.router.navigate(["cobranza/views/recibo", id], {
      queryParams: { of: of }
    });
  }

  goToPreRecibo(id: number) {
    this.router.navigate(["/cobranza/views/borradores", id]);
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }
}
