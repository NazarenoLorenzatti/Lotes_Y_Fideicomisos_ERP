package com.ar.cobranza.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "imputaciones_pre_recibo_caja")
@Data
public class ImputacionPreReciboCaja implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pre_recibo_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PreRecibo preRecibo;

    @ManyToOne
    @JoinColumn(name = "caja_cobranza_id")
    private CajaCobranza cajaCobranza;

    
    private Double importeImputado;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaImputacion;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate fechaRealPago;
    
    private Tipo tipo;
    
    private String nota;
    private String nroComprobante;
    private String bancoOrigen;
    
    public enum Tipo{
        EFECTIVO, CHEQUE, TRANSFERENCIA, TARJETA_C, TARJETA_D, RETENCION; 
    }
    
}
