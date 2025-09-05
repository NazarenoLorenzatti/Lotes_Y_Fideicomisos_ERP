package com.ar.cobranza.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import lombok.Data;

@Data
@Entity
@Table(name = "recibos_auxiliares")
public class ReciboAuxiliar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero_recibo;

    private boolean isValid = false;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha_confirmacion;

    @OneToOne
    @JoinColumn(name = "id_pre_comprobante")
    private PreRecibo preRecibo;

    // 🔁 Relación opcional a otro comprobante (ej: Factura que se corrige)
    @ManyToOne
    @JoinColumn(name = "id_comprobante_asociado")
    private ReciboAuxiliar comprobantesAsociado;
}
