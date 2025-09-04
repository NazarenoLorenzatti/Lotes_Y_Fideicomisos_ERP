package com.ar.base.services;

import com.ar.base.entities.CuentaContable;
import org.springframework.http.ResponseEntity;

public interface iCuentaContableService {

    public ResponseEntity<?> guardarCuentaContable(CuentaContable cuentaContable);

    public ResponseEntity<?> editarCuentaContable(CuentaContable cuentaContable);

    public ResponseEntity<?> eliminarCuentaContable(Long id);

    public ResponseEntity<?> cambiarEstadoCuentaContable(Long id);

    public ResponseEntity<?> getCuentaContable(Long id);

    public ResponseEntity<?> listarCuentasContables();
}
