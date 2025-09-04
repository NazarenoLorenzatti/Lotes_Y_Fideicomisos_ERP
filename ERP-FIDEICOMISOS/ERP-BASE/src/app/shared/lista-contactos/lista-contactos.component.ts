import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { Table } from 'primeng/table';
import { ContactoService } from '../../core/services/contactos/contacto.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-tabla-contactos',
  templateUrl: './lista-contactos.component.html',
  styleUrl: './lista-contactos.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListaContactosComponent implements OnInit {

  @Output() contactoSeleccionado = new EventEmitter<any>();

  public estados!: any[];
  public loading: boolean = true;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  public searchValue: any = '';
  private contactosService = inject(ContactoService);
  public contactos: any[] = [];
  public contacto!: any;
  public visible: boolean = false;

  showDialog() {
    this.visible = true;
  }

  constructor() { }

  ngOnInit() {
    this.contactosService.listarContactos().subscribe({
      next: (response: any) => this.handleContactosData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });

    this.contactosService.listarEstados().subscribe({
      next: (response: any) => this.handleEstadosData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });
  }

  handleEstadosData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.estados = response.response.response[0];
      this.loading = false;
    }
  }

  handleContactosData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      const rawContactos = response.response.response[0];
      this.contactos = rawContactos.filter((c: any) => c.estado?.nombreEstado.toLowerCase() !== 'borrador');
      this.loading = false;
    }
  }

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  selectContacto(contacto: any) {
    this.contactoSeleccionado.emit(contacto);
  }

  goTo(url: string) {
    this.router.navigate([url]);
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
