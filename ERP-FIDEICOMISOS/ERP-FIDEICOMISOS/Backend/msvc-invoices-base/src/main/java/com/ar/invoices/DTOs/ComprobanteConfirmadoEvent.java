package com.ar.invoices.DTOs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private Long idComprobanteAsociado;

    private Long idEntidad;

    private String UUIDComprobanteAsociado;

    private List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables;
}
