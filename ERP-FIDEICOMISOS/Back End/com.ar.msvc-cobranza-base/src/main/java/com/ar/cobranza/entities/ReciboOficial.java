package com.ar.cobranza.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "recibos_oficiales")
public class ReciboOficial implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cae_afip;

    private String vto_cae;

    private String numero_recibo;

    private boolean isValid = true;

    private Integer cbte_nro_desde;

    private Integer cbte_nro_hasta;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha_oficilizacion;
    
    @OneToOne
    @JoinColumn(name = "id_pre_comprobante")
    private PreRecibo preRecibo;

    // 🔁 Relación opcional a otro comprobante (ej: Factura que se corrige)
    @ManyToOne
    @JoinColumn(name = "id_comprobante_asociado")
    private ReciboOficial comprobantesAsociado;
}
