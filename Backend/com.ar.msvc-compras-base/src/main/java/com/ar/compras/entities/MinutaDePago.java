package com.ar.compras.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "minutas_pago")
public class MinutaDePago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero; // Se completa SOlo

    @Enumerated(EnumType.STRING)
    private Estado estado; // Se completa SOlo

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaEfectiva; // Se completa SOlo

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaCreacion; // Se completa SOlo

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaRechazo; // Se completa SOlo

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaPagada; // Se completa SOlo

    private Double importe; // Se completa Solo

    @Enumerated(EnumType.STRING)
    private MedioPago medioDePago;
    
    private String uuid; // Se completa solo

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private OrdenDePago orden;

    @ManyToOne
    @JoinColumn(name = "caja_id")
    private CajaPagos caja;

    public enum Estado {
        BORRADOR, PENDIENTE, PAGADA, RECHAZADO;
    }

    public enum MedioPago {
        CHEQUE, EFECTIVO, TRANSFERENCIA, TARJETA_CREDITO, TARJETA_DEBITO;
    }
}
