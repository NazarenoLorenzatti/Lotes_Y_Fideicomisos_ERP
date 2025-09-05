package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "conciliaciones_contables")
public class ConciliacionContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private String tipoOperacion; // Ej: COBRO_FACTURA, DEVOLUCION
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Estado estado;
    
    private String originada_por;
    private String anulada_por;
    
    @OneToMany(mappedBy = "conciliacion", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetalleConciliacion> detalles;

    public enum Estado {
        ACTIVA, ANULADA, REALIZADA;
    }
}
