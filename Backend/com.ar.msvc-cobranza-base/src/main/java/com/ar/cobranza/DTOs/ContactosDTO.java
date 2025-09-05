package com.ar.cobranza.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
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

//    private LocalDate fechaDeCreacion;

    private boolean isCliente = true;

    private boolean isProveedor = false;
}
