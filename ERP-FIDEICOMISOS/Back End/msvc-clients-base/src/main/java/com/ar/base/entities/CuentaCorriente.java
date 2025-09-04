package com.ar.base.entities;

import com.ar.base.entities.MovimientoCuentaCorriente.TipoMovimiento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "cuentas_corrientes")
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "contacto_id", nullable = false)
    @JsonIgnore
    private Contacto contacto;

    @OneToMany(mappedBy = "cuentaCorriente")
    @OrderBy("fecha DESC")
    private List<MovimientoCuentaCorriente> movimientos = new ArrayList<>();

    @Transient
    private Double saldo;

    @JsonProperty("saldo")
    public Double getSaldoCalculado() {
        double saldo = 0.0;
        for (MovimientoCuentaCorriente mov : movimientos) {
            if (mov.getTipoMovimiento() == TipoMovimiento.CREDITO) {
                saldo += mov.getImporte();
            } else {
                saldo -= mov.getImporte();
            }
        }
        return saldo;
    }

    @JsonProperty("tipoSaldo")
    public String getTipoSaldo() {
        double saldo = this.getSaldoCalculado();
        if (saldo > 0) {
            return "ACREEDOR";   // Cliente tiene saldo a favor
        } else if (saldo < 0) {
            return "DEUDOR"; // Cliente debe
        } else {
            return "NULO";                  // Cuenta en equilibrio
        }
    }

    /**
     * "DEUDOR" si el cliente debe
     * "ACREEDOR" si el cliente tiene saldo a favor
     * "NULO" si está saldado (saldo = 0)
     */
    public enum TipoSaldo {
        ACREEDOR, DEUDOR, NULO
    }

}
