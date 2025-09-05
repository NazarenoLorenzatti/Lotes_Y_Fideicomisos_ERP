package com.ar.base.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "cuentas_por_cobrar")
public class CuentaPorCobrar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long entidadId;
    
    private Long comprobanteId;
    
    @ManyToOne
    @JoinColumn(name = "tipo_entidad_id")
    private TiposDeEntidades tipoEntidad;
    
    private Double saldoPendiente;
    
    private Double importeOriginal;
    
    private LocalDate fechaEmision;
    
    private String referencia; // Factura N°, ND, etc. 
    
    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado; // PENDIENTE, CANCELADA, PARCIAL 
    
    @OneToMany(mappedBy = "cuentaPorCobrar", cascade = CascadeType.ALL)
    private List<AplicacionPago> aplicaciones = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "asiento_id")
    private AsientoContable asiento;

    public enum EstadoCuenta {
        PENDIENTE, CANCELADA, PARCIAL, ANULADA, SALDO_A_FAVOR;
    }
}
