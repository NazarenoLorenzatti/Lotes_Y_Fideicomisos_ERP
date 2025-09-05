package com.ar.compras.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ordenes_pago")
public class OrdenDePago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observaciones;

    private Double importe;

    private Boolean oficial;

    @Enumerated(EnumType.STRING)
    private Estado estado; // Se genera Solo

    private String uuid; // Se genera Solo

    private String numero; // Se genera Solo

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fecha; // Se genera Solo

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaOrdenDePago> facturasAplicadas = new ArrayList<>();

    @OneToOne(mappedBy = "orden")
    private MinutaDePago minutaDePago;

    public enum Estado {
        PENDIENTE, APROBADA, PAGADA, RECHAZADA;
    }
}
