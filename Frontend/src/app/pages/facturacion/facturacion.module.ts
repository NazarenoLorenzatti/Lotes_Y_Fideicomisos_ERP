import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FacturacionRoutingModule } from './facturacion-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { ViewsModule } from './views/views.module';
import { CommonsModule } from './commons/commons.module';

@NgModule({
  declarations: [
  ],
  imports: [
    FacturacionRoutingModule,
    CommonModule,
    SharedModule,
    ViewsModule,
    CommonsModule
  ]
})
export class FacturacionModule { }
