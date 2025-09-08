package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "movimientos_cuenta_corriente")
public class MovimientoCuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cuenta_corriente_id", nullable = false)
    @JsonIgnore
    private CuentaCorriente cuentaCorriente;

    @Column(nullable = false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Double importe;
    
    private Double importe_iva;
    
    private Double importe_neto;
    
    private Double importe_gravado;
    
    private Double saldoPendiente = 0.0;

    private boolean oficial;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento; // CREDITO (Pago Cliente) o DEBITO (Factura)

    private Long comprobanteId;

    private String nroComprobante;

    private String descripcion;

    private Boolean isApply = false;

    @OneToMany(mappedBy = "movimientoOrigen", cascade = CascadeType.ALL)
    private List<AplicacionMovimiento> aplicacionesComoOrigen = new ArrayList<>();

    @OneToMany(mappedBy = "movimientoDestino", cascade = CascadeType.ALL)
    private List<AplicacionMovimiento> aplicacionesComoDestino = new ArrayList<>();

    public enum TipoMovimiento {
        DEBITO, CREDITO;
    }

}
