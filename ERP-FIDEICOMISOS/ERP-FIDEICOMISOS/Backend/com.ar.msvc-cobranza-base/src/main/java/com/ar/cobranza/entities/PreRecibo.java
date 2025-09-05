package com.ar.cobranza.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pre_recibos")
public class PreRecibo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cuit_emisor;

    /* Valores enviados en el Body del request*/
    private String recibo_fecha; //Fecha de emision del comprobante
    private String fecha_desde; //Fecha de inicio periodo facturado
    private String fecha_hasta; //Fecha de fin periodo facturado
    private String fecha_vto; //Fecha de fin periodo facturado
    private Double importe_neto; // Importe neto gravado Es el subtotal antes de aplicar IVA o cualquier otro impuesto. Para facturas A: es el monto sobre el cual se aplica IVA. Para facturas B: sigue siendo obligatorio y representa el valor de los productos o servicios.
    private Double importe_total; //Importe Total de la factura
    private Double importe_iva; //  Importe total del IVA aplicado,  Para facturas A: es el IVA discriminado. Para facturas B: normalmente debe ser 0 o no incluirse AFIP permite mandarlo con valor 0, pero si tenés impNeto > 0, el objeto Iva igual es obligatorio 
    private Double importe_gravado; // Importe neto gravado subtotal antes de aplicar IVA o cualquier otro impuesto.
    private Double importe_excento = 0.0; // Importe de operaciones exentas de IVA
    private Double importe_no_gravado = 0.0; // impTotConc Importe de operaciones no gravadas (conceptos no categorizados)
    private Double importe_tributado = 0.0;
    private Integer concepto; // Concepto de facturacion, productos etc.
    private String moneda;
    private boolean isOficial;
    private String descripcionIva;

    /* Valores Deducidos en base a Comprobaciones en el servicio*/
    private double moneda_cotizacion;

    /* Valores del modulo de Contactos */
    private Long nro_documento;
    private int tipo_documento;
    private String razon_social;
    private String direccion_fiscal;
    private String localidad;
    private String email_envio;
    private Long contactoId;
    private int condicion_iva_receptor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaModificacion;

    private Integer idAfipTipoRecibo;
    private String abrebiaturaTipoRecibo;
    private String descripcionTipoRecibo;
    private Integer puntoVenta;
    private String nombrePtoVenta;
    private Integer idAfipAlicuotaIva;

    @ManyToOne
    @JoinColumn(name = "id_estado_comprobante")
    private EstadoRecibo estado;

    @OneToMany(mappedBy = "preRecibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImputacionPreReciboCaja> imputaciones;
//
//    @OneToMany
//    @JoinColumn(name = "id_caja")
//    private CajaCobranza caja;
}
