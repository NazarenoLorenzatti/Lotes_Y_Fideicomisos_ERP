import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { HomeContabilidadComponent } from './commons/home-contabilidad/home-contabilidad.component';

export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        component: HomeContabilidadComponent
      },
      {
        path: 'home',
        component: HomeContabilidadComponent
      },
      {
        path: 'cuentas',
        loadChildren: () =>
          import('./cuentas-contables/cuentas-contables.module').then(m => m.CuentasContablesModule),
      },
      {
        path: 'asientos',
        loadChildren: () =>
          import('./asientos-contables/asientos-contables.module').then(m => m.AsientosContablesModule),
      }
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ContabilidadRoutingModule { }
