import { RouterModule, Routes } from '@angular/router';
import { ListaComponent } from './commons/lista/lista.component';
import { NgModule } from '@angular/core';
import { KpisComponent } from './commons/kpis/kpis.component';

export const routes: Routes = [
  {
    path: '',
    component: KpisComponent,
    children: [
      {
        path: '',
        component: ListaComponent
      },
      {
        path: 'list',
        component: ListaComponent
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
export class ContactosRoutingModule { }
