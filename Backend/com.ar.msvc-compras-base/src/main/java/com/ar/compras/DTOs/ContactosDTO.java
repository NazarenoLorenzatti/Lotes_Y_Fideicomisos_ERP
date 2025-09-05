package com.ar.compras.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactosDTO {

    private Long id;

    private String razonSocial;   // o nombre completo para personas físicas

    private String tipoDocumento; // Ej: CUIT, DNI, CUIL, etc.
    private String numeroDocumento;
    
    private int idAfipTipoDocumento;

    private String direccionFiscal;

    private String pcia;

    private String localidad;

    private String codigoPostal;

    private String condicionIva;  // Responsable Inscripto, Monotributista, etc.

    private int idCondicionIva;
    
    private String email;

    private String celular;

    private String telefono;

    private Date fechaDeCreacion;

    private Boolean isCliente;

    private Boolean isProveedor;
}
