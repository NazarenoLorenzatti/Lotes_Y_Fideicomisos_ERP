import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BorradoresRoutingModule } from './borradores-routing.module';
import { BorradoresComponent } from './borradores/borradores.component';
import { PreReciboComunComponent } from './pre-recibo/pre-recibo-comun/pre-recibo-comun.component';
import { ListComponent } from './borradores/list/list.component';
import { SharedModule } from '../../../../shared/shared.module';
import { PreReciboComponent } from './pre-recibo/pre-recibo.component';
import { CrearModule } from '../crear/crear.module';



@NgModule({
  declarations: [
    BorradoresComponent,
    PreReciboComunComponent,
    ListComponent,
    PreReciboComponent
  ],
  exports:[
    PreReciboComunComponent
  ],
  imports: [
    CommonModule,
    BorradoresRoutingModule,
    SharedModule,
    CrearModule
  ]
})
export class BorradoresModule { }
