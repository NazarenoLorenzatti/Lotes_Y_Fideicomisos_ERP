import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { ConfiguracionesAfipComponent } from "./configuraciones-afip/configuraciones-afip.component";

const constRutasHijas: Routes = [
  { path: '', component: ConfiguracionesAfipComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class AfipRoutingModule { }