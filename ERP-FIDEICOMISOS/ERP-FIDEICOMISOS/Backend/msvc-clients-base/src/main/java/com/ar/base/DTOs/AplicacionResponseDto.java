package com.ar.base.DTOs;

import lombok.Data;

@Data
public class AplicacionResponseDto {

    public Long idComprobanteOrigen;
    public String numeroComprobanteOrigen;
    public Double importeComprobanteOrigen;
    public String tipoMovComprobanteOrigen;

    public Long idComprobanteDestino;
    public String numeroComprobanteDestino;
    public Double importeComprobanteDestino;
    public String tipoMovComprobanteDestino;
}
