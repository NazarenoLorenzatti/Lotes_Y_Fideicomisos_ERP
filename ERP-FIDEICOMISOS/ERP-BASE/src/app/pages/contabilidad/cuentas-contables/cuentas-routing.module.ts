import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { CrearComponent } from "./components/crear/crear.component";
import { ListarComponent } from "./components/listar/listar.component";
import { DetallesComponent } from "./components/detalles/detalles.component";

const constRutasHijas: Routes = [
  { path: '', component: ListarComponent },
  { path: 'crear', component: CrearComponent },
  { path: 'detalles', component: DetallesComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class CuentasRoutingModule { }