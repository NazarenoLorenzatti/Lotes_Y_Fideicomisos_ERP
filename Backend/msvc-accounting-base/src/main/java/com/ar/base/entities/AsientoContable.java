package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Table(name = "asientos_contables")
public class AsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    private TipoOperacion tipoOperacion; // Ej: FACTURA, RECIBO 
    
    private String referenciaExterna; // UUID de evento o ID externo 
    
    @Enumerated(EnumType.STRING)
    private Estado estado;
    
    private Long contactoId;
    
    @Enumerated(EnumType.STRING)
    private TipoAsiento tipoAsiento;
    
    @ManyToOne
    @JoinColumn(name = "tipo_entidad_id")
    private TiposDeEntidades tipoEntidad;
    
    private Long comprobanteId;
    
    private String revertido_por;
    private String originada_por;
    
    private LocalDateTime fechaReversion;
    private LocalDateTime fechaCreacion;
    
    @ManyToOne
    @JoinColumn(name = "asiento_origen_id")
    private AsientoContable asientoOrigen; // para reversión 
    
    @OneToMany(mappedBy = "asientoOrigen")
    @JsonIgnore
    private List<AsientoContable> reversiones;
    
   @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoContable> movimientos = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "conciliacion_id")
    private ConciliacionContable conciliacion;
    
    private boolean oficial;

    public enum Estado {
        APROBADO, REVERTIDO, CONCILIADO;
    }

    public enum TipoAsiento {
        ASIENTO_AUTOMATICO, TRANSFERENCIA_INTERNA, ASIENTO_REVERSION, ASIENTO_MANUAL, TRANSFERENCIA_ENTRADA;
    }

    public enum TipoOperacion {
        FACTURA_CLIENTE, FACTURA_PROVEEDOR, NOTA_CREDITO, NOTA_DEBITO, RECIBO, ORDEN_PAGO, MOVIMIENTO_INTERNO, REVERSA_MOV_INTERNO, CONTRA_RECIBO;
    }
}
