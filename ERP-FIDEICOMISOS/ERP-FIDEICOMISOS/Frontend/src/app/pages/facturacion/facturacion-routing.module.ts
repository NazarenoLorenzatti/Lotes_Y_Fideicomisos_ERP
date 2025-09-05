import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { KpisComponent } from './commons/kpis/kpis.component';
import { ListaComprobantesComponent } from './commons/lista-comprobantes/lista-comprobantes.component';

const routes: Routes = [
  {
    path: '',
    component: KpisComponent, // opcional: si hay layout para Facturacion
    children: [
      {
        path: '',
        component: ListaComprobantesComponent
      },
      {
        path: 'list',
        component: ListaComprobantesComponent
      },
      {
        path: 'views',
        loadChildren: () =>
          import('./views/views.module').then(m => m.ViewsModule),
      }
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FacturacionRoutingModule { }
