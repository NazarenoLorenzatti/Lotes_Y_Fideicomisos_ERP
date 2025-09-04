import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { ComprobanteComponent } from "./components/comprobante/comprobante.component";
import { PendientesComponent } from "./components/pendientes/pendientes.component";
import { PreComprobanteComponent } from "./components/pre-comprobante/pre-comprobante.component";

/*const constRutasHijas: Routes = [
  { path: 'comprobante/:id', component: ComprobanteComponent },
  { path: 'pendientes', component: PendientesComponent },
]*/


const constRutasHijas: Routes = [
  {
    path: '',
    //component: KpisComponent, // opcional: si hay layout para Facturacion
    children: [
            {
        path: 'pre-comprobante/:id',
        component: PreComprobanteComponent
      },
      {
        path: 'comprobante/:id',
        component: ComprobanteComponent
      },
      {
        path: 'pendientes',
        component: PendientesComponent
      },
      {
        path: 'crear',
        loadChildren: () =>
          import('./components/crear/crear-factura.module').then(m => m.CrearFacturaModule),
      }
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class ViewsRoutingModule { }