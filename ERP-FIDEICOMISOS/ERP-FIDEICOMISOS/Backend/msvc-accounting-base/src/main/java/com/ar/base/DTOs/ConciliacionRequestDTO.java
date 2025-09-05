package com.ar.base.DTOs;

import java.util.List;
import lombok.Data;


@Data
public class ConciliacionRequestDTO {

    private List<Long> movimientosIds;
    private String tipoOperacion;
    private String descripcion;
}
