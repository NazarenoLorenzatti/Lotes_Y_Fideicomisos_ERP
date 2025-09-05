import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { CrearReciboComponent } from "./crear-recibo/crear-recibo.component";

const constRutasHijas: Routes = [
  { path: '', component: CrearReciboComponent },
]
@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class CrearReciboRoutingModule { }
