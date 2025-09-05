import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReciboComponent } from './recibo/recibo.component';
import { ContraReciboComponent } from './contra-recibo/contra-recibo.component';
import { ViewsRoutingModule } from './views-routing.module';
import { BorradoresModule } from "./borradores/borradores.module";
import { CrearModule } from './crear/crear.module';

@NgModule({
  declarations: [
    ReciboComponent,
    ContraReciboComponent,
  ],
  imports: [
    ViewsRoutingModule,
    CommonModule,
    BorradoresModule,
    CrearModule
]
})
export class ViewsModule { }
