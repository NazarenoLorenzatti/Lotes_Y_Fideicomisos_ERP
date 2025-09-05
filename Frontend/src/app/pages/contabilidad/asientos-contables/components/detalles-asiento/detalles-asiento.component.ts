import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AsientosService } from '../../../../../core/services/contabilidad/asientos.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-detalles-asiento',
  templateUrl: './detalles-asiento.component.html',
  styleUrl: './detalles-asiento.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class DetallesAsientoComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  public searchValue: any = '';
  private asientosServices = inject(AsientosService);
  private messageService = inject(MessageService);
  public asiento!: any;
  private id!: number;
  public conciliacion!: any;

  ngOnInit(): void {
    this.getIdAsiento();
  }

  private getIdAsiento(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
      this.findAsiento(this.id);
    });
  }

  findAsiento(id: number) {
    this.asientosServices.getAsiento(this.id).subscribe({
      next: (response: any) => this.handleResponseOkData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }

  revertirAsiento(id: number) {
    let asientoReversion = {
      referenciaExterna: "UUID-REVB-00001-00000001",
      comprobanteId: 20001,
      tipoOperacion: "NOTA_CREDITO"
    }
    this.asientosServices.revertirAsiento(asientoReversion, id).subscribe({
      next: (response: any) => this.handleResponseOkData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }

  handleResponseOkData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.asiento = response.response.response[0];
      this.conciliacion = this.asiento.conciliacion;
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
