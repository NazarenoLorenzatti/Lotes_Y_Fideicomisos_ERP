import { Component, inject } from '@angular/core';
import { RecibosService } from '../../../../../core/services/cobranza/recibos.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ContactoService } from '../../../../../core/services/contactos/contacto.service';

@Component({
  selector: 'app-pre-recibo',
  templateUrl: './pre-recibo.component.html',
  styleUrl: './pre-recibo.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class PreReciboComponent {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  public preRecibo: any = {};
  public contacto!: any;
  public id!: number;
  private recibosService = inject(RecibosService);
  private messageService = inject(MessageService);
  private contactosService = inject(ContactoService);
  public loading: boolean = true;
  public visible = false;

  ngOnInit(): void {
    this.getIDPreRecibo();

  }

  private getIDPreRecibo(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
    });

    this.findPreComprobatenById(this.id);
  }

  findPreComprobatenById(id: number) {
    this.recibosService.obtenerPreRecibo(id).subscribe({
      next: (response: any) => this.handlePreReciboData(response),
      error: (error: any) => this.handlePreReciboError(error),
    });
  }

  handlePreReciboData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.preRecibo = response.response.response[0];
      this.showMessage(response?.metadata?.[0]?.info, 'Ok', 'success')
      this.loading = false;
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handlePreReciboError(error: any) {
    const errorMessage = error.error?.metadata[0]?.informacion || 'Ocurrió un error inesperado.';
    console.error('Error:', error);
    this.showMessage(errorMessage, 'Error', 'error');
  }

  editar() {
    this.contactosService.obtenerContacto(this.preRecibo.contactoId).subscribe({
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

  confirmar(id: number) {
    this.loading = true;
    this.recibosService.confirmarPreRecibo(id).subscribe({
      next: (response: any) => {
        this.handlePreReciboData(response);
        this.goTo('cobranza/views/borradores');
      },
      error: (error: any) => this.handlePreReciboError(error),
    });
  }

  confirmContraRecibo(contraRecibo: any) {
    this.loading = true;
    this.recibosService.confirmarPreReciboAsociado(contraRecibo.id, contraRecibo.id_Recibo_a_cancelar).subscribe({
      next: (response: any) =>{
        this.handlePreReciboData(response);
        this.goTo('cobranza/views/borradores');
      },
      error: (error: any) => this.handlePreReciboError(error)
    })
  }

  eliminar(id: number) {
    this.loading = true;
    this.recibosService.eliminarPreRecibos(id).subscribe({
      next: (response: any) => {
        this.handlePreReciboData(response);
        this.goTo('cobranza/views/borradores');
      },
      error: (error: any) => this.handlePreReciboError(error),
    });
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
