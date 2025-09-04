import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-pre-comprobante-common',
  templateUrl: './pre-comprobante-common.component.html',
  styleUrl: './pre-comprobante-common.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class PreComprobanteCommonComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);

  @Input() preComprobante: any = {};

  ngOnInit(): void {

  }

  goToContacto(id: number) {
    this.router.navigate(["contactos/views/contacto", { id }]);
  }
}