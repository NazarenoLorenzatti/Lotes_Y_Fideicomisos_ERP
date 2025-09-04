package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "cuentas_contables")
public class CuentaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo; // Ej: 1.1.1.01
    private String nombre; // Ej: Caja Sucursal 1

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipo; // ACTIVO, PASIVO, PATRIMONIO, INGRESO, EGRESO

    private boolean activa = true;

    private boolean conciliable = true;

    private boolean oficial = true;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CuentaContable padre; // Para jerarquías tipo árbol (opcional)

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<CuentaContable> hijas = new ArrayList<>();

    public enum TipoCuenta {
        ACTIVO, PASIVO, PATRIMONIO, INGRESO, EGRESO
    }
}
