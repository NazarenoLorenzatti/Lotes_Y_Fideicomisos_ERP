import { Component, inject, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-list-recibos-borrador',
  templateUrl: './list.component.html',
  styleUrl: './list.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListComponent implements OnInit, OnChanges {


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
    this.router.navigate(["cobranza/views/crear", { id }]);
  }

  goToPreRecibo(id: number) {
    this.router.navigate(["cobranza/views/pre-recibo", id]);
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