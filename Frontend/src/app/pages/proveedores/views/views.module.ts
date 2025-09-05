import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ViewsRoutingModule } from './views-routing.module';
import { ArticulosComponent } from './articulos/articulos.component';



@NgModule({
  declarations: [
    ArticulosComponent
  ],
  imports: [
    ViewsRoutingModule,
    CommonModule
  ]
})
export class ViewsModule { }
