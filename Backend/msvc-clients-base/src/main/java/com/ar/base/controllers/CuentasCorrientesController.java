package com.ar.base.controllers;

import com.ar.base.DTOs.AplicacionManualRequest;
import com.ar.base.entities.Contacto;
import com.ar.base.entities.MovimientoCuentaCorriente;
import com.ar.base.services.impl.CuentaCorrienteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuentas")
@CrossOrigin("*")
public class CuentasCorrientesController {

    @Autowired
    private CuentaCorrienteServiceImpl cuentaCorrienteService;

    /**
     * Obtener cuenta corriente del contacto
     *
     * @param contacto
     * @return
     */
    @PostMapping("/cuenta-corriente")
    public ResponseEntity<?> getCuentaCorriente(@RequestBody Contacto contacto) {
        return this.cuentaCorrienteService.getCuentaCorriente(contacto);
    }

    /**
     * Registrar un nuevo movimiento en la cuenta contable del cliente
     *
     * @param idCliente
     * @param movimiento
     * @return
     */
    @PostMapping("/registrar-movimiento/{idCliente}")
    public ResponseEntity<?> registrarMovimiento(@PathVariable Long idCliente, @RequestBody MovimientoCuentaCorriente movimiento) {
        return this.cuentaCorrienteService.registrarMovimiento(idCliente, movimiento);
    }

    /**
     * Eliminar conciliacion de Movimientos
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAplicacion(@PathVariable Long id) {
        return this.cuentaCorrienteService.deleteAplicacion(id);
    }

    /**
     * Conciliar Movimientos Manuales
     *
     * @param aplicacion
     * @return
     */
    @PostMapping("/aplicacion")
    public ResponseEntity<?> aplicacionManual(@RequestBody AplicacionManualRequest aplicacion) {
        return this.cuentaCorrienteService.applyMovimiento(aplicacion);
    }

    @GetMapping("/get/aplicaciones/origen/{id}")
    public ResponseEntity<?> getAplicacionesOrigen(@PathVariable Long id) {
        return this.cuentaCorrienteService.getAplicaciones(id, null);
    }

    @GetMapping("/get/aplicaciones/destino/{id}")
    public ResponseEntity<?> getAplicacionesDestino(@PathVariable Long id) {
        return this.cuentaCorrienteService.getAplicaciones(null, id);
    }
}
