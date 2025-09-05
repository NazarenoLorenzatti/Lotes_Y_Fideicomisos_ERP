import { Component, inject, OnInit } from '@angular/core';
import { Table } from 'primeng/table';
import { ContactoService } from '../../../../core/services/contactos/contacto.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-lista',
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListaComponent implements OnInit {

  public estados!: any[];
  public loading: boolean = true;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  public searchValue: any = '';
  private contactosService = inject(ContactoService);
  public contactos: any[] = [];

  constructor() { }

  ngOnInit() {
    let tipoLista = this.getTipoLista();
    this.contactosService.listarContactos().subscribe({
      next: (response: any) => this.handleContactosData(response, tipoLista),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });

    this.contactosService.listarEstados().subscribe({
      next: (response: any) => this.handleEstadosData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });
  }

  private getTipoLista(): string {
    let ret = '';
    this.route.paramMap.subscribe((params) => {
      ret = String(params.get('tipoLista'));
    });
    return ret;
  }

  handleEstadosData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.estados = response.response.response[0];
      this.loading = false;
    }
  }

  handleContactosData(response: any, tipoLista: string) {
    console.log(tipoLista, response)
    if (tipoLista != 'borrador') {
      if (response?.metadata?.[0]?.codigo === '00') {
        const rawContactos = response.response.response[0];
        this.contactos = rawContactos.filter((c: any) => c.estado?.nombreEstado.toLowerCase() !== 'borrador');
        this.loading = false;
      }
    } else {
      if (response?.metadata?.[0]?.codigo === '00') {
        const rawContactos = response.response.response[0];
        this.contactos = rawContactos.filter((c: any) => c.estado?.nombreEstado.toLowerCase() == tipoLista);
        this.loading = false;
      }
    }
  }

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  goToContacto(id: number) {
    this.router.navigate(["contactos/views/contacto", { id }]);
  }

  goToCrear() {
    this.router.navigate(["contactos/views/crear"]);
  }

  goToEditar(id: number) {
    this.router.navigate(["contactos/views/crear", { id }]);
  }

  goToProspectos(tipoLista: string) {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['contactos', { tipoLista }]);
    });
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
