package com.ar.base.DTOs;

import com.ar.base.entities.CuentaContable;
import com.ar.base.entities.CuentaContable.TipoCuenta;
import java.util.List;
import lombok.Data;

@Data
public class CuentaContableDTO {
    
    private Long id;
    private String codigo; // Ej: 1.1.1.01
    private String nombre; // Ej: Caja Sucursal 1
    private TipoCuenta tipo; // ACTIVO, PASIVO, PATRIMONIO, INGRESO, EGRESO
    private boolean activa = true;
    private boolean conciliable = true;
    private boolean oficial = true;
    private List<CuentaContableDTO> hijas;
}
