import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-layout',
  standalone: false,
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit{

  public items!: MenuItem[];
  private router = inject(Router);
  
  ngOnInit(): void {
      this.items = [
            {
                label: 'Cajas Cobranza',
                command: () => {
                    this.goTo('/configuraciones/cajas/cajas-cobranza');
                }
            },
            { separator: true },
            {
                label: 'Cajas Pagos',
                command: () => {
                   this.goTo('/configuraciones/cajas/cajas-pagos');
                }
            },
        ];
  }

  toggleDarkMode() {
    const element = document.querySelector('html');
    element?.classList.toggle('my-app-dark');
  }

  goTo(url : string){
    this.router.navigate([url]);
  }
}
