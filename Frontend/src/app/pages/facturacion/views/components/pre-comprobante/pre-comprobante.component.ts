import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FacturacionService } from '../../../../../core/services/facturacion/facturacion.service';
import { MessageService } from 'primeng/api';
import { ContactoService } from '../../../../../core/services/contactos/contacto.service';

@Component({
  selector: 'app-pre-comprobante',
  templateUrl: './pre-comprobante.component.html',
  styleUrl: './pre-comprobante.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class PreComprobanteComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  public preComprobante: any = {};
  public contacto!: any;
  public id!: number;
  private facturaService = inject(FacturacionService);
  private messageService = inject(MessageService);
  private contactosService = inject(ContactoService);
  public loading: boolean = true;
  public visible = false;

  ngOnInit(): void {
    this.getIDPreComprobante();

  }

  private getIDPreComprobante(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
    });

    this.findPreComprobatenById(this.id);
  }

  findPreComprobatenById(id: number) {
    this.facturaService.obtenerPreComprobante(id).subscribe({
      next: (response: any) => this.handlePreComprobanteData(response),
      error: (error: any) => this.handlePreComprobanteError(error),
    });
  }

  confirmar(id: number) {
    this.loading = true;
    this.facturaService.confirmarPreComprobante(id).subscribe({
      next: (response: any) => {
        this.handlePreComprobanteData(response);
        this.goTo('facturacion/views/pendientes');
      },
      error: (error: any) => this.handlePreComprobanteError(error),
    });
  }

  confirmContraComprobante(contracomprobante: any) {
    this.loading = true;
    this.facturaService.confirmarPreComprobanteAsociado(contracomprobante.id, contracomprobante.id_Comprobante_a_cancelar).subscribe({
      next: (response: any) => {
        this.handlePreComprobanteData(response);
        this.goTo('facturacion');
      },
      error: (error: any) => this.handlePreComprobanteError(error)
    })
  }

  eliminar(id: number) {
    this.facturaService.eliminarPreComprobantes(id).subscribe({
      next: (response: any) => {
        this.handlePreComprobanteData(response);
        this.goTo('facturacion/views/pendientes');
      },
      error: (error: any) => this.handlePreComprobanteError(error),
    });
  }

  handlePreComprobanteData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.preComprobante = response.response.response[0];
      this.loading = false;
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, 'Advertencia', 'warn');
    }
  }

  handlePreComprobanteError(error: any) {
    const errorMessage = error.error?.metadata[0]?.informacion || 'Ocurrió un error inesperado.';
    console.error('Error:', error);
    this.showMessage(errorMessage, 'Error', 'error');
  }

  editar() {
    this.contactosService.obtenerContacto(this.preComprobante.contactoId).subscribe({
      next: (response: any) => this.handleContactoData(response),
      error: (error: any) => this.handleContactoError(error),
    });
    this.visible = true;
  }

  handleContactoData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.contacto = response.response.response[0];
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handleContactoError(error: any) {
    const errorMessage = error.error?.metadata[0]?.informacion || 'Ocurrió un error inesperado.';
    console.error('Error:', error);
    this.showMessage(errorMessage, 'Error', 'error');
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  goToContacto(id: number) {
    this.router.navigate(["contactos/views/contacto", { id }]);
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }
}
