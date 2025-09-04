import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListaComponent } from './commons/lista/lista.component';
import { ContactosRoutingModule } from './contactos-routing.module';
import { ViewsModule } from './views/views.module';
import { RouterModule } from '@angular/router';
import { KpisComponent } from './commons/kpis/kpis.component';
import { SharedModule } from '../../shared/shared.module';


@NgModule({
  declarations: [
   ListaComponent,
   KpisComponent
  ],
  imports: [
    ContactosRoutingModule,
    RouterModule,
    CommonModule,
    ViewsModule,
    SharedModule
  ]
})
export class ContactosModule { }
