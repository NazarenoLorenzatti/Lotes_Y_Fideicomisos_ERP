package com.ar.base.DTOs;

import com.ar.base.entities.AsientoContable;
import lombok.Data;

@Data
public class ResultadoTransferenciaDTO {

    private AsientoContable asientoSalida;
    private AsientoContable asientoEntrada;
}
