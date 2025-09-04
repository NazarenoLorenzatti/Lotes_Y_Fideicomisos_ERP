import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KpisComponent } from './commons/kpis/kpis.component';
import { RouterModule } from '@angular/router';
import { ListaRecibosComponent } from './commons/listarecibos/listarecibos.component';
import { CobranzaRoutingModule } from './cobranza-routing.module';
import { ViewsModule } from './views/views.module';
import { TablaComponent } from './commons/listarecibos/tabla/tabla.component';
import { SharedModule } from '../../shared/shared.module';
import { BorradoresModule } from './views/borradores/borradores.module';

@NgModule({
  declarations: [
    KpisComponent,
    ListaRecibosComponent,
    TablaComponent,
  ],
  imports: [
    CobranzaRoutingModule,
    CommonModule,
    RouterModule,
    ViewsModule,
    BorradoresModule,
    SharedModule
  ]
})
export class CobranzaModule { }
