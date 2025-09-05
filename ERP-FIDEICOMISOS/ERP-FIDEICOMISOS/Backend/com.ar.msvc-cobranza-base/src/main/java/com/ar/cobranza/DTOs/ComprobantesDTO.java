package com.ar.cobranza.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComprobantesDTO {
    
    private Long id;
    
    private boolean saldado;
    
    private String estado;
    
    private boolean oficial;
    
    private double importe_adeudado;
    
    private Date fecha_comprobante;
}
