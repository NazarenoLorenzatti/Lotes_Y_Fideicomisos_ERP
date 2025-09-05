import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-contabilidad',
  templateUrl: './home-contabilidad.component.html',
  styleUrl: './home-contabilidad.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class HomeContabilidadComponent {

  private router = inject(Router);

  goTo(url: string) {
    this.router.navigate([url]);
  }
}
