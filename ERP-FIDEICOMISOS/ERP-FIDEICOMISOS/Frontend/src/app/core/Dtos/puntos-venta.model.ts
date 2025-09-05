export interface PuntosDeVenta {
    id: number;
    nroPtoVenta: number;
    nombrePtoVenta: string;
    cuit: CuitEmisor;
    facturacionRecurrente: boolean;
    habilitado: boolean;
}

export interface CuitEmisor {
    id: number;
    cuit: string;
    ingresosBrutos: string;
    inicioActividades: string;
    razonSocial: string;
    direccionFiscal: string;
    nombreFantasia: string;
}