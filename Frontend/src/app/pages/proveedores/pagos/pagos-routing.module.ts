import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { FacturaComponent } from "./factura/factura.component";
import { OrdenPagoComponent } from "./orden-pago/orden-pago.component";
import { MinutaPagoComponent } from "./minuta-pago/minuta-pago.component";
import { InicioComponent } from "./inicio/inicio.component";

const constRutasHijas: Routes = [
  { path: '', component: InicioComponent },
  { path: 'factura', component: FacturaComponent },
  { path: 'minuta', component: MinutaPagoComponent },
  { path: 'orden', component: OrdenPagoComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class PagosRoutingModule { }