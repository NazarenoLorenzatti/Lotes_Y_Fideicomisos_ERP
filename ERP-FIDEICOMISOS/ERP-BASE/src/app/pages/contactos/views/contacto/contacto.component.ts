import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ContactoService } from '../../../../core/services/contactos/contacto.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-contacto',
  templateUrl: './contacto.component.html',
  styleUrl: './contacto.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ContactoComponent implements OnInit {

  private route = inject(ActivatedRoute);
  public id!: number;
  private contactosService = inject(ContactoService);
  public contacto: any;
  public loading = true;
  private messageService = inject(MessageService);
  private router = inject(Router);

  ngOnInit(): void {
    this.getIDContacto();
  }

  private getIDContacto(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
      this.findContactoById(this.id);
    });
  }

  findContactoById(id: number) {
    this.contactosService.obtenerContacto(id).subscribe({
      next: (response: any) => this.handleContactoData(response),
      error: (error: any) => this.handleContactoError(error),
    });
  }

  handleContactoData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.contacto = response.response.response[0];
      this.loading = false;
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handleContactoError(error: any) {
    const errorMessage = error.error?.metadata[0]?.informacion || 'Ocurrió un error inesperado.';
    console.error('Error:', error);
    this.showMessage(errorMessage, 'Error', 'error');
  }

  confirm(id: number) {
    this.contactosService.confirmarContacto(id).subscribe({
      next: (response: any) => this.handleContactoData(response),
      error: (error: any) => this.handleContactoError(error),
    })
  }

  // Muestra un mensaje
  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  goToProspectos(tipoLista: string) {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['contactos', { tipoLista }]);
    });
  }



  goTo(url: string) {
    this.router.navigate([url]);
  }
}
