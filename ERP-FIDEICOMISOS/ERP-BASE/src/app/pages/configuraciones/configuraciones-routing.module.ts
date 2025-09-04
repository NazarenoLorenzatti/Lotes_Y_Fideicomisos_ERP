import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

export const routes: Routes = [
    {
        path: '',
        children: [
            {
                path: 'afip',
                loadChildren: () =>
                    import('./afip/afip.module').then(m => m.AfipModule),
            },
            {
                path: 'cajas',
                loadChildren: () =>
                    import('./cajas/cajas.module').then(m => m.CajasModule),
            }
        ],
    },
];
@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ConfiguracionesRoutingModule { }
