import { RouterModule, Routes } from "@angular/router";
import { BorradoresComponent } from "./borradores/borradores.component";
import { NgModule } from "@angular/core";
import { PreReciboComponent } from "./pre-recibo/pre-recibo.component";

const constRutasHijas: Routes = [
  { path: '', component: BorradoresComponent },
  { path: 'pre-recibo/:id', component: PreReciboComponent }
]


@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class BorradoresRoutingModule { }