package com.ar.base.DTOs;

import java.util.Date;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImporteAplicadoEvent {

    private Long facturaId;

    private Long clienteId;
    
    private boolean oficial;

    private String nroComprobante;

    private Double importeAplicado;

    private Date fechaSaldado;
}
