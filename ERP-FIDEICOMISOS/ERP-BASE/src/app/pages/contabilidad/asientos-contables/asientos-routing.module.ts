import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { CrearComponent } from "./components/crear/crear.component";
import { DetallesAsientoComponent } from "./components/detalles-asiento/detalles-asiento.component";
import { TablaComponent } from "./components/tabla/tabla.component";



const constRutasHijas: Routes = [
  { path: '', component: TablaComponent },
  { path: 'crear', component: CrearComponent },
  { path: 'detalles/:id', component: DetallesAsientoComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class AsientosRoutingModule { }