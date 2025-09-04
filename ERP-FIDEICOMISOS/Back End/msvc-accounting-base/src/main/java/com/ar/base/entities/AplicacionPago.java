package com.ar.base.entities;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;


@Entity
@Data
@Table(name = "aplicaciones_pagos")
public class AplicacionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CuentaPorCobrar cuentaPorCobrar; // puede ser null si aplica a proveedor

    @ManyToOne
    private CuentaPorPagar cuentaPorPagar; // puede ser null si aplica a cliente

    private Date fechaAplicacion;

    private Double importe;

    private String referenciaExterna; // ID del comprobante que generó el pago
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    private AsientoContable asiento;
}
