import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { ReciboComponent } from "./recibo/recibo.component";
import { ContraReciboComponent } from "./contra-recibo/contra-recibo.component";
import { CrearReciboComponent } from "./crear/crear-recibo/crear-recibo.component";
import { BorradoresComponent } from "./borradores/borradores/borradores.component";

/*const constRutasHijas: Routes = [
  { path: 'contra-recibo', component: ContraReciboComponent },
  { path: 'recibo', component: ReciboComponent },
  { path: 'crear', component: CrearReciboComponent },
  { path: 'borradores', component: BorradoresComponent }
]*/

const constRutasHijas: Routes = [
  {
    path: '',
    //component: KpisComponent, // opcional: si hay layout para Facturacion
    children: [
      {
        path: 'crear',
        component: CrearReciboComponent
      },
      {
        path: 'recibo/:id',
        component: ReciboComponent
      },
      {
        path: 'contra-recibo',
        component: ContraReciboComponent
      },
      {
        path: 'borradores',
        loadChildren: () =>
          import('./borradores/borradores.module').then(m => m.BorradoresModule),
      }
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class ViewsRoutingModule { }