import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { KpisComponent } from './commons/kpis/kpis.component';
import { ListaFacturasComponent } from './commons/lista-facturas/lista-facturas.component';

export const routes: Routes = [
  {
    path: '',
    component: KpisComponent,
    children: [
      {
        path: '',
        component: ListaFacturasComponent
      },
      {
        path: 'list',
        component: ListaFacturasComponent
      },
      {
        path: 'views',
        loadChildren: () =>
          import('./views/views.module').then(m => m.ViewsModule),
      },
      {
        path: 'pagos',
        loadChildren: () =>
          import('./pagos/pagos.module').then(m => m.PagosModule),
      }
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProveedoresRoutingModule { }
