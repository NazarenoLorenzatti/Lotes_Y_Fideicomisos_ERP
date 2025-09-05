import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FacturacionService } from '../../../../../core/services/facturacion/facturacion.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-comprobante',
  templateUrl: './comprobante.component.html',
  styleUrl: './comprobante.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ComprobanteComponent implements OnInit {

  private route = inject(ActivatedRoute);
  public id!: number;
  public of!: boolean;
  public loading: boolean = true;
  private router = inject(Router);
  public comprobante: any;
  private facturasService = inject(FacturacionService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.getIDComprobante();
  }

  private getIDComprobante(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
    });

    this.route.queryParamMap.subscribe((params) => {
      this.of = params.get('of') === 'true';
    });
    this.findComprobatenById(this.id);
  }

  findComprobatenById(id: number) {
    if (this.of) {
      this.facturasService.obtenerComprobanteOficial(id).subscribe({
        next: (response: any) => this.handleComprobanteData(response),
        error: (error: any) => this.handleComprobanteError(error),
      });
    } else {
      this.facturasService.obtenerComprobanteAuxiliar(id).subscribe({
        next: (response: any) => this.handleComprobanteData(response),
        error: (error: any) => this.handleComprobanteError(error),
      });
    }
  }

  handleComprobanteData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.comprobante = response.response.response[0];
      this.loading = false;
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handleComprobanteError(error: any) {
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

  goToComprobante(id: number, of: boolean) {
    console.log(id, of)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(["facturacion/views/comprobante", id], {
        queryParams: { of: of }
      });
    });
  }

    goTo(url: string) {
    this.router.navigate([url]);
  }
}
