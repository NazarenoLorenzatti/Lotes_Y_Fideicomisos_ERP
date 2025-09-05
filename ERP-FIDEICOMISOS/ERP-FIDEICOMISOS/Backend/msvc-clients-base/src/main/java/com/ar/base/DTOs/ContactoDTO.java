package com.ar.base.DTOs;

import com.ar.base.entities.Estados;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ContactoDTO {

    private Long id;

    private double saldo;

    private String razonSocial;   // o nombre completo para personas físicas

    private String tipoDocumento; // Ej: CUIT, DNI, CUIL, etc.

    private String numeroDocumento;

    private String localidad;

    private String email;

    private Estados estado;

    private String celular;

    private String telefono;

    private LocalDate fechaDeCreacion;

    private Boolean isCliente;

    private Boolean isProveedor;

    // CAMBIAR POR ENTIDAD USUARIO DEL MODULO DE USUARIOS Y LOGIN
    private String creador_por;
}
