import { RouterModule, Routes } from "@angular/router";
import { ContactoComponent } from "./contacto/contacto.component";
import { CrearComponent } from "./crear/crear.component";
import { NgModule } from "@angular/core";

const constRutasHijas: Routes = [
  { path: 'contacto', component: ContactoComponent },
  { path: 'crear', component: CrearComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class ViewsRoutingModule { }