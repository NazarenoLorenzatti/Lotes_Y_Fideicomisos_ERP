package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "contactos")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "contacto", cascade = CascadeType.ALL)
    private CuentaCorriente cuentaCorriente;
    
    private String nombre;
    
    private String apellido;

    private String razonSocial;   // o nombre completo para personas físicas

    private String tipoDocumento; // Ej: CUIT, DNI, CUIL, etc.

    private Integer idAfipTipoDocumento;

    @Column(nullable = false, unique = true)
    private String numeroDocumento;

    private String direccionFiscal;
    
    private String altura;

    private String pcia;

    private String localidad;

    private String codigoPostal;

    private String condicionIva;  // Responsable Inscripto, Monotributista, etc.

    private Integer idCondicionIva;

    private String email;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estados estado;

    private String celular;

    private String celular_auxiliar;

    private String telefono;

    private String telefono_auxiliar;

    private String email_auxiliar;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaDeCreacion;

    private Boolean isCliente;

    private Boolean isProveedor;

    // CAMBIAR POR ENTIDAD USUARIO DEL MODULO DE USUARIOS Y LOGIN
    private String creador_por;
}
