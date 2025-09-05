package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "movimientos_contables")
public class MovimientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AsientoContable asiento;

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private CuentaContable cuenta;

    @ManyToOne
    @JoinColumn(name = "tipo_entidad_id")
    private TiposDeEntidades tipoEntidad;

    private boolean conciliado;

    private LocalDate fecha;

    private Double debe;
    private Double haber;

    private Long entidadId; // ID del cliente/proveedor/etc.

}
