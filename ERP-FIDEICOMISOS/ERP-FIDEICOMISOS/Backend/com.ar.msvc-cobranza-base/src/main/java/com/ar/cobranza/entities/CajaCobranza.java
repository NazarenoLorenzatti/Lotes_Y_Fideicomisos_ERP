package com.ar.cobranza.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "caja_cobranza")
public class CajaCobranza implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre_caja;

    @Column(name = "caja_cobranza")
    private Boolean cajaCobranza;

    private Boolean activa = true;
   
    private Boolean retenciones = false;

    @Column(nullable = false)
    private Long idCuentaContable;

    @Column(nullable = false)
    private String nroCuentaContable;

    @Column(nullable = false)
    private String nombreCuentaContable;

    private Boolean oficial;

    @OneToMany(mappedBy = "cajaCobranza", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ImputacionPreReciboCaja> preRecibosImputados;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    public enum Tipo {
        CAJA, BANCO, RETENCIONES;
    }

}
