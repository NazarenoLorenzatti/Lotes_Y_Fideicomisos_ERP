package com.ar.compras.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detalles_facturas")
public class DetallesFacturas implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observacion;

    private Double montoNetoDetalle;

    private Double montoIvaDetalle;

    private Double montoTotalDetalle;

    private Double precioUnitario;
    
    private double iva;

    private Double cantidad;

    private String moneda;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private FacturaProveedor factura;

    @ManyToOne
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulo articulo;

}
