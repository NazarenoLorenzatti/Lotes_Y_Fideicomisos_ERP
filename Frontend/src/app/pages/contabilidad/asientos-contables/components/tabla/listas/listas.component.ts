import { Component, inject, Input, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { AsientosService } from '../../../../../../core/services/contabilidad/asientos.service';
import { Router } from '@angular/router';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-listas-asientos',
  templateUrl: './listas.component.html',
  styleUrl: './listas.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListasComponent implements OnInit {

  @Input() listAsientos: any = {};
  @Input() loading: boolean = true;
  private router = inject(Router);
  public searchValue: any = '';
  private messageService = inject(MessageService);
  

  ngOnInit(): void {
   
  }

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  handleResponseErrorData(error: any) {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error?.metadata?.[0]?.informacion, error?.metadata?.[0]?.respuesta, 'danger')
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  goToAsiento(id: number) {
    this.router.navigate(["contabilidad/asientos/detalles", id]);
  }
}
