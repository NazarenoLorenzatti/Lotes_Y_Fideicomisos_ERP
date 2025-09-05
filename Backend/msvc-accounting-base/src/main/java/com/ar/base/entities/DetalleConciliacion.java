package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "detalles_conciliaciones")
public class DetalleConciliacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private ConciliacionContable conciliacion;

    private Double importe; // Puede ser parcial al del detalle

    // Movimiento contable que participa de la conciliación
    @ManyToOne
    @JoinColumn(name = "movimiento_contable_id")
    private MovimientoContable movimiento;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Estado {
        ACTIVA, ANULADA, REALIZADA;
    }
}
