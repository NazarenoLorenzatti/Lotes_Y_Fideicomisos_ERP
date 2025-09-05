package com.ar.base.controllers;

import com.ar.base.entities.CuentaContable;
import com.ar.base.services.impl.CuentaContableServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CuentasContablesController {

    @Autowired
    private CuentaContableServiceImpl cuentaService;

    /**
     * Guardar una nueva cuenta Contable
     *
     * @param cuentaContable
     * @return
     */
    @PostMapping("/save")
    public ResponseEntity<?> guardarCuentaContable(@RequestBody CuentaContable cuentaContable) {
        return this.cuentaService.guardarCuentaContable(cuentaContable);
    }

    /**
     * Editar una cuenta contable Guardada
     *
     * @param cuentaContable
     * @return
     */
    @PutMapping("/edit")
    public ResponseEntity<?> editarCuentaContable(@RequestBody CuentaContable cuentaContable) {
        return this.cuentaService.editarCuentaContable(cuentaContable);
    }

    /**
     * Eliminar una cuenta contable. Solo si no tiene cuentas contables Hijas ni
     * Movimientos
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarCuentaContable(@PathVariable("id") Long id) {
        return this.cuentaService.eliminarCuentaContable(id);
    }

    /**
     * Cambiar el estado de la cuenta contable
     *
     * @param id
     * @return
     */
    @GetMapping("/set-estado/{id}")
    public ResponseEntity<?> cambiarEstadoCuentaContable(@PathVariable("id") Long id) {
        return this.cuentaService.cambiarEstadoCuentaContable(id);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable("id") Long id) {
        return this.cuentaService.getCuentaContable(id);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> listarCuentas() {
        return this.cuentaService.listarCuentasContables();
    }
}
