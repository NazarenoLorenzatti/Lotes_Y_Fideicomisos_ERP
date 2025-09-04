package com.ar.afip.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import lombok.Data;

@Data
@Entity
@Table(name = "afip_tokens")
public class AfipToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String service;
    
    @Lob
    private String token;
    
    @Lob
    private String sign;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;
    
    @ManyToOne
    @JoinColumn(name = "cuit_emisor")
    private CuitEmisor cuit;
            
}
