/*export interface Item {
    id: number;
    articulo: string;
    codigo: string;
    tipo: string; // Producto o Servicio
    cantidad: number;
    precioUnitarioSinIva: number;
    precioUnitarioConIva: number;
    idAfipAlicuotaIva: number;
    descripcionAlicuota: string;
    iva: number;
    monto_iva: number;
    importeGravado: number;
    importeNoGravado: number;
    bonificacion: number;

}*/

export interface Item {
    cantidad: number;
    importeTotalIva: number;
    importeTotalSinIva: number;
    importeTotalConIva: number;
    ImporteNetoNoGravado: number;
    ImporteNetoNoExcento: number;
    OtrosTributos: number;
    idAfipAlicuotaIva: number;
    descripcionAlicuota: string;
    articulo: Articulo;
    bonificacion: number;
}

export interface Articulo {
    id: number;
    codigo: string;
    descripcion: string;
    desIva: string;
    idAlicuotaAfip: number;
    tipo: string;
    iva: number;
    monto_iva: number;
    precioUnitario: number;
    precioUnitarioConIva: number;
    importeGravado: number;
    importeNoGravado: number;
    idCuentaContable: number;
    nroCuentaContable: string;
    nombreCuentaContable: string;
    idCuentaContableAux: number;
    nroCuentaContableAux: string;
    nombreCuentaContableAux: string;
    archivar: boolean;
}