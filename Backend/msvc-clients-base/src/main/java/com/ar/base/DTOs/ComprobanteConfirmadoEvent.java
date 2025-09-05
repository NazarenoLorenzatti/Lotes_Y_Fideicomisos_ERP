package com.ar.base.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComprobanteConfirmadoEvent {

    private Long idComprobante;

    private String nroComprobante; 
    
    private boolean isOficial;
    
    private Long contactoId;
    
    private Double importeTotal;

    private Double importeIva;

    private Double importeNeto;

    private Double importe_gravado;
    
    private String cbteFecha;
    
    private String tipoComprobante;
    
    private String estado;
    
    private boolean cancelado = false;

}