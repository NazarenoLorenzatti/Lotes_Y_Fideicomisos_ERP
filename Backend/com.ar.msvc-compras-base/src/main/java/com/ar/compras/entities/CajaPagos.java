package com.ar.compras.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "caja_pagos")
public class CajaPagos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre_caja;

    private Boolean caja_pago;

    private Boolean activa = true;

    @Column(nullable = false)
    private Long idCuentaContable;

    @Column(nullable = false)
    private String nroCuentaContable;

    @Column(nullable = false)
    private String nombreCuentaContable;

    private Boolean oficial;

    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<MinutaDePago> minutas;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    public enum Tipo {
        CAJA, BANCO;
    }

}
