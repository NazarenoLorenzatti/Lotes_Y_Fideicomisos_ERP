import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CajasCobranzaComponent } from './cajas-cobranza/cajas-cobranza.component';
import { CajasPagosComponent } from './cajas-pagos/cajas-pagos.component';
import { CajasRoutingModule } from './cajas-routing.module';
import { InicioComponent } from './inicio/inicio.component';

@NgModule({
  declarations: [
    CajasCobranzaComponent,
    CajasPagosComponent,
    InicioComponent
  ],
  imports: [
    CommonModule,
    CajasRoutingModule
  ]
})
export class CajasModule { }
