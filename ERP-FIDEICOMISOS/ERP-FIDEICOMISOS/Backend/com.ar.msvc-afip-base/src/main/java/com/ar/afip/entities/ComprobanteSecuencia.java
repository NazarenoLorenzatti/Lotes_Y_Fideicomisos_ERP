/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import javax.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "comprobante_secuencia", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tipo_comprobante_id", "punto_venta_id"})
})
public class ComprobanteSecuencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cuit")
    private CuitEmisor cuit;

    @ManyToOne
    @JoinColumn(name = "tipo_comprobante_id", nullable = false)
    private TipoComprobante tipoComprobante;

    @ManyToOne
    @JoinColumn(name = "punto_venta_id", nullable = false)
    private PuntosDeVenta puntoVenta;

    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero = 0;
}
