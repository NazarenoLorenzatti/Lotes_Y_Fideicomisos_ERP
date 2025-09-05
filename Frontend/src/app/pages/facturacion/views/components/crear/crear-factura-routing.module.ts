import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { CrearFacturaComponent } from "./crear-factura/crear-factura.component";

const constRutasHijas: Routes = [
  { path: '', component: CrearFacturaComponent  },
]
@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class CrearFacturaRoutingModule { }
