package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "transferencias_internas")
public class TransferenciaInterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    private CuentaContable cuentaOrigen;

    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id")
    private CuentaContable cuentaDestino;

    private Double importe;

    private String descripcion;

    private Long contactoId;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private String referencia;

    @OneToMany()
    @JoinColumn(name = "transferencia_interna_id")
    private List<AsientoContable> asientos;

    @ManyToOne
    @JoinColumn(name = "tipo_entidad_id")
    private TiposDeEntidades tipoEntidad;

    public enum Estado {
        PENDIENTE, APROBADA, ANULADA;
    }
}
