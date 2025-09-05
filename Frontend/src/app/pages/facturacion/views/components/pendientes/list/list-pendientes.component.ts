import { Component, inject, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-list-pendientes-table',
  templateUrl: './list-pendientes.component.html',
  styleUrl: './list-pendientes.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListPendientesComponent implements OnInit, OnChanges {


  @Input() list: any = {};
  private router = inject(Router);
  public searchValue: any = '';
  public loading: boolean = true;

  ngOnInit(): void {

  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['list']) {
      this.loading = false;
    }
  }

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  goToEditar(id: number) {
    this.router.navigate(["facturacion/views/crear", { id }]);
  }

  goToPreComprobante(id: number) {
    this.router.navigate(["facturacion/views/pre-comprobante", id]);
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
