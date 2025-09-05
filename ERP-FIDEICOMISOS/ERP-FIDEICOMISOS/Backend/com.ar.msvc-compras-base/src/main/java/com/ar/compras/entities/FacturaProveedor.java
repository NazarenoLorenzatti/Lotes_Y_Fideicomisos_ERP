package com.ar.compras.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "facturas_proveedores")
public class FacturaProveedor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFactura;

    @Enumerated(EnumType.STRING)
    private TipoFactura tipoFactura;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate fechaEmision;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate fechaVencimiento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaDeCarga;

    private Double importeTotal;

    private Double importeIva;

    private Double importeNeto;

    private Double importeAbonado;

    private Double saldoPendiente;

    private String descripcion;

    private Boolean oficial;

    // Informacion del Modulo Contacto
    private String nombreProveedor;

    private Long nroProveedor;

    private String cuitProveedor;

    private String regimenAfip;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private String uuid;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallesFacturas> detalles;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaOrdenDePago> pagosAplicados = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_obra")
    private Obra obra;

    public enum TipoFactura {
        FACTURA_A, FACTURA_B, FACTURA_C, FACTURA_AUX;
    }

    public enum Estado {
        BORRADOR, PENDIENTE, PAGADA, PAGO_PARCIAL, RECHAZADA;
    }
}
