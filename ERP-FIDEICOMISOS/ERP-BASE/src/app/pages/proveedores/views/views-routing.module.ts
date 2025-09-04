import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { ArticulosComponent } from "./articulos/articulos.component";


const constRutasHijas: Routes = [
  { path: 'articulos', component: ArticulosComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class ViewsRoutingModule { }