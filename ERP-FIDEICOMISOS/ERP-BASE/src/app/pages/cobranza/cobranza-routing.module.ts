import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { KpisComponent } from './commons/kpis/kpis.component';
import { ListaRecibosComponent } from './commons/listarecibos/listarecibos.component';


export const routes: Routes = [
  {
    path: '',
    component: KpisComponent,
    children: [
      {
        path: '',
        component: ListaRecibosComponent
      },
      {
        path: 'list',
        component: ListaRecibosComponent
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
export class CobranzaRoutingModule { }
