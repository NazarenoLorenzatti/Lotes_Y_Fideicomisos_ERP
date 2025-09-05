package com.ar.afip.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "cuits_emisor")
public class CuitEmisor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cuit;

    private String ingresosBrutos;

    private String inicioActividades;

    private String razonSocial;

    private String direccionFiscal;

    private String nombreFantasia;

    private String pfxPath = "src/main/resources/certificados/certificado-pfx.pfx";

    private String pfxPassword = "nlorenzatti2025";

    private String service = "wsfe";

}
