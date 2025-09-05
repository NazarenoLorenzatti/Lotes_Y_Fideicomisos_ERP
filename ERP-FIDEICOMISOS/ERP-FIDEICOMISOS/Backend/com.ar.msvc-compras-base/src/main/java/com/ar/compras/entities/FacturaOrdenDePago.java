package com.ar.compras.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "factura_orden_pago")
public class FacturaOrdenDePago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuidFactura;
    private String uuidOrden;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private FacturaProveedor factura;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private OrdenDePago orden;
}
