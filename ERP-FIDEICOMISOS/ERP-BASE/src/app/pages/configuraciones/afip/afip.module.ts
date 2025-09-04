import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfiguracionesAfipComponent } from './configuraciones-afip/configuraciones-afip.component';
import { AfipRoutingModule } from './afip-routing.module';



@NgModule({
  declarations: [
    ConfiguracionesAfipComponent
  ],
  imports: [
    CommonModule,
    AfipRoutingModule
  ]
})
export class AfipModule { }
