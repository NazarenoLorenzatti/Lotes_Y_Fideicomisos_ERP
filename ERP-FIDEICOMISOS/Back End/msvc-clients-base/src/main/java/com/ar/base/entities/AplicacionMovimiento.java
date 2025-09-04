package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "aplicaciones_movimientos")
@Data
public class AplicacionMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movimiento_origen_id") // Recibo o NC
    @JsonIgnore
    private MovimientoCuentaCorriente movimientoOrigen;

    @ManyToOne
    @JoinColumn(name = "movimiento_destino_id") // Factura o ND
    @JsonIgnore
    private MovimientoCuentaCorriente movimientoDestino;

    private Date fecha_aplicacion;

    private Double importeAplicado;
}
