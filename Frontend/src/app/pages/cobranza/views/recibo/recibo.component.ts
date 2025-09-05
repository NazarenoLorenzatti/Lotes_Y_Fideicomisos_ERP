import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RecibosService } from '../../../../core/services/cobranza/recibos.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-recibo',
  templateUrl: './recibo.component.html',
  styleUrl: './recibo.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ReciboComponent implements OnInit {

  private route = inject(ActivatedRoute);
  public id!: number;
  public of!: boolean;
  public loading: boolean = true;
  private router = inject(Router);
  public recibo: any;
  private recibosService = inject(RecibosService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.getIDRecibo();
  }

  private getIDRecibo(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
    });

    this.route.queryParamMap.subscribe((params) => {
      this.of = params.get('of') === 'true';
    });
    this.findReciboById(this.id);
  }

  findReciboById(id: number) {
    if (this.of) {
      this.recibosService.buscarRecibo(id).subscribe({
        next: (response: any) => this.handleReciboData(response),
        error: (error: any) => this.handleReciboError(error),
      });
    } else {
      this.recibosService.buscarReciboAux(id).subscribe({
        next: (response: any) => this.handleReciboData(response),
        error: (error: any) => this.handleReciboError(error),
      });
    }
  }

  contraRecibo(id: number) {
    this.recibosService.contraRecibo(id).subscribe({
      next: (response: any) => {
        this.handleReciboData(response)
        this.goTo('/cobranza')
      },
      error: (error: any) => this.handleReciboError(error),
    });
  }

  handleReciboData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.recibo = response.response.response[0];
      this.loading = false;
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handleReciboError(error: any) {
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

  goToRecibo(id: number, of: boolean) {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(["cobranza/views/recibo", id], {
        queryParams: { of: of }
      });
    });
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }
}
