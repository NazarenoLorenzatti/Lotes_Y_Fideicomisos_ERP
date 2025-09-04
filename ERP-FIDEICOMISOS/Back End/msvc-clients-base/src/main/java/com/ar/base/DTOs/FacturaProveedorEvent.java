package com.ar.base.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
