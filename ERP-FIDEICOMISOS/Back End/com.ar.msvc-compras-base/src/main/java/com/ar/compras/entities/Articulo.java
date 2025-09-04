package com.ar.compras.entities;

import com.ar.compras.entities.CajaPagos.Tipo;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "articulos")
public class Articulo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreArticulo;
    
    @Enumerated(EnumType.STRING)
    private Tipo tipoEgreso;

    @Column(nullable = false)
    private Long idCuentaContable;

    @Column(nullable = false)
    private String nroCuentaContable;

    @Column(nullable = false)
    private String nombreCuentaContable;

    private Boolean oficial;

    private Boolean archivar;

    public enum Tipo {
        SERVICIO, PRODUCTO;
    }

}
