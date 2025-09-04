import { RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { NgModule } from "@angular/core";

const constRutasHijas: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
]

@NgModule({
  imports: [RouterModule.forChild(constRutasHijas)],
  exports: [RouterModule],
})
export class HomeRoutingModule { }