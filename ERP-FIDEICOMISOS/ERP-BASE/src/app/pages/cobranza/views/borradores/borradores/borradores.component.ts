import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RecibosService } from '../../../../../core/services/cobranza/recibos.service';

@Component({
  selector: 'app-borradores',
  templateUrl: './borradores.component.html',
  styleUrl: './borradores.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class BorradoresComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  private recibosService = inject(RecibosService);
  public listaPreRecibos!: any[];

  constructor() { }

  ngOnInit() {
    this.getList();
  }

  getList() {
    let body = {
      id: 1,
      descripcion: "Borrador"
    }
    this.recibosService.listarPreRecibosPorEstado(body).subscribe({
      next: (response: any) => this.handlePreComprobanteData(response),
      error: (error: any) => console.error('Error fetching Current Account data:', error),
    });
  }

  handlePreComprobanteData(response: any) {
    console.log(response)
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listaPreRecibos = response.response.response[0];

    }
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }

}



