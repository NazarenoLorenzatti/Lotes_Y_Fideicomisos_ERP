package com.ar.compras.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class MinutaDePagoEvent {

    private Long id;

    private String numero;
    private LocalDateTime fechaEfectiva;

    private Double importe;
    private Double importeNeto;
    private Double importeIva;
    private Double importeGravado;

    private String medioDePago;
    private String uuidOrdenPagoAsociada;
    private String cajaNombre;
    private Long cajaId;
    private String cajaCuentaContable;
    private String uuidMinuta;
    private Boolean oficial;
    private Long nroProveedor;
    private Map<String, String> facturasPagadas;

    private List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables;
}
