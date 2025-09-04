package com.ar.afip.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ptos_de_venta")
public class PuntosDeVenta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombrePtoVenta;
    
    private Boolean habilitado;
    
    private Boolean facturacionRecurrente;
    
    @ManyToOne
    @JoinColumn(name = "cuit_emisor")
    private CuitEmisor cuit;
    
    @Column(unique = true)
    private int nroPtoVenta;
    
}
