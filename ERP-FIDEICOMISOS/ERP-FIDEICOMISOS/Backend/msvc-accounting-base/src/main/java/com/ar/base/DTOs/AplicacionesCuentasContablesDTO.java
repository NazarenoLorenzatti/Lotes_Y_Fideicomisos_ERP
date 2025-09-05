package com.ar.base.DTOs;

import lombok.Data;

@Data
public class AplicacionesCuentasContablesDTO {
    private Long idCuenta;
    private String nombreCuenta;
    private String nroCuenta;
    private TipoMov tipoMov;
    private Double importeAplicado;
    public enum TipoMov {
        DEBE, HABER;
    }
}
