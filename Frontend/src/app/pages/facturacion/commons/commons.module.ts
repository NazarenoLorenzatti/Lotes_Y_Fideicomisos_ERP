import { NgModule } from "@angular/core";
import { KpisComponent } from "./kpis/kpis.component";
import { ListComponent } from "./lista-comprobantes/list/list.component";
import { ListaComprobantesComponent } from "./lista-comprobantes/lista-comprobantes.component";
import { SharedModule } from '../../../shared/shared.module';
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

@NgModule({
    declarations: [
        KpisComponent,
        ListaComprobantesComponent,
        ListComponent
    ],
    exports: [
        ListaComprobantesComponent,
        ListComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        RouterModule,
    ]
})
export class CommonsModule { }