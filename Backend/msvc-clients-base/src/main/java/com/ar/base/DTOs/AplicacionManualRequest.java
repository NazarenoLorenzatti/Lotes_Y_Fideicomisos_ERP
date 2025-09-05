package com.ar.base.DTOs;

import lombok.Data;

@Data
public class AplicacionManualRequest {
    private Long movimientoOrigenId;
    private Long movimientoDestinoId;
    private Double importe;
}
