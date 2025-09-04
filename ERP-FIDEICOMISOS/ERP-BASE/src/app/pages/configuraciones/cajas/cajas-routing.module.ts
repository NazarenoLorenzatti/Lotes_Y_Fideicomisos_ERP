import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { CajasCobranzaComponent } from "./cajas-cobranza/cajas-cobranza.component";
import { CajasPagosComponent } from "./cajas-pagos/cajas-pagos.component";
import { InicioComponent } from "./inicio/inicio.component";

const constRutasHijas: Routes = [
    { path: '', component: InicioComponent },
    { path: 'cajas-cobranza', component: CajasCobranzaComponent },
    { path: 'cajas-pagos', component: CajasPagosComponent },
]

@NgModule({
    imports: [RouterModule.forChild(constRutasHijas)],
    exports: [RouterModule],
})
export class CajasRoutingModule { }