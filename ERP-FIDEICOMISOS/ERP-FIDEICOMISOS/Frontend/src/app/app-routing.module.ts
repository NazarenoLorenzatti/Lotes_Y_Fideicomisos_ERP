import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutComponent } from './template/layout/layout.component';

const routes: Routes = [
    {
        path: '',
        component: LayoutComponent, // TEMPLATE
        children: [
            {
                path: '',
                loadChildren: () =>
                    import('./pages/home/home.module').then(m => m.HomeModule),
            },
            {
                path: 'facturacion',
                loadChildren: () =>
                    import('./pages/facturacion/facturacion.module').then(m => m.FacturacionModule),
            },
            {
                path: 'proveedores',
                loadChildren: () =>
                    import('./pages/proveedores/proveedores.module').then(m => m.ProveedoresModule),
            },
            {
                path: 'cobranza',
                loadChildren: () =>
                    import('./pages/cobranza/cobranza.module').then(m => m.CobranzaModule),
            },
            {
                path: 'configuraciones',
                loadChildren: () =>
                    import('./pages/configuraciones/configuraciones.module').then(m => m.ConfiguracionesModule),
            },
            {
                path: 'contactos',
                loadChildren: () =>
                    import('./pages/contactos/contactos.module').then(m => m.ContactosModule),
            },
            {
                path: 'contabilidad',
                loadChildren: () =>
                    import('./pages/contabilidad/contabilidad.module').then(m => m.ContabilidadModule),
            }
        ]
    },
    { path: '**', redirectTo: '' }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
