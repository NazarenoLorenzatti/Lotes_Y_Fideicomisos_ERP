package com.ar.compras.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class FacturaProveedorEvent {

    private Long id;
    private String numeroFactura;
    private String tipoFactura;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaDeCarga;
    private Double importeTotal;
    private Double importeIva;
    private Double importeNeto;
    private Double importeGravado;
    private String descripcion;
    private Boolean oficial;
    private String nombreProveedor;
    private Long nroProveedor;
    private String cuitProveedor;
    private String estado;
    private String uuidFactura;

    private List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables;
}
