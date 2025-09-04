package com.ar.invoices.entities;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "item_facturado")
public class ItemFacturado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cantidad;

    private double importeTotalIva;

    private double importeTotalSinIva;

    private double importeTotalConIva;
    
     private double importeNetoNoGravado;
     
     private double ImporteNetoNoExcento;
     
     private double OtrosTributos;
    
    private double bonificacion;
    
    private Integer idAfipAlicuotaIva;
    private String descripcionAlicuota;

    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;
}
