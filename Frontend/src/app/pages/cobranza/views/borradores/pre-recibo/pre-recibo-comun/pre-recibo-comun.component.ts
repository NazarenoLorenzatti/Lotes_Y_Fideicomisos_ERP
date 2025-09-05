import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-pre-recibo-comun',
  templateUrl: './pre-recibo-comun.component.html',
  styleUrl: './pre-recibo-comun.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class PreReciboComunComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);

  @Input() preRecibo: any = {};

  ngOnInit(): void {
  }

  goToContacto(id: number) {
    this.router.navigate(["contactos/views/contacto", { id }]);
  }

}
