package com.ar.base.controllers;

import com.ar.base.DTOs.ConciliacionRequestDTO;
import com.ar.base.entities.AsientoContable;
import com.ar.base.services.iAsientoContableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asientos")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AsientosContablesController {

    @Autowired
    private iAsientoContableService asientoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAsiento(@RequestBody AsientoContable asiento) {
        return asientoService.registrarAsiento(asiento);
    }

    @PostMapping("/{id}/revertir")
    public ResponseEntity<?> revertirAsiento(@PathVariable Long id, @RequestBody AsientoContable asiento) {
        return asientoService.registrarAsientoReversion(id, asiento);
    }

    @PostMapping("/conciliar")
    public ResponseEntity<?> conciliarMovimientos(@RequestBody ConciliacionRequestDTO request) {
        return asientoService.conciliarMovimientos(request.getMovimientosIds(), request.getTipoOperacion(), request.getDescripcion());
    }

    @PostMapping("/{id}/anular-conciliaciones")
    public ResponseEntity<?> anularConciliaciones(@PathVariable Long id) {
        return asientoService.anularConciliacion(id);
    }

    /**
     * Listar y Obtener Asientos y movimientos Contables
     *
     * @param id
     * @return
     */
    @GetMapping("/get-asiento/{id}")
    public ResponseEntity<?> getAsiento(@PathVariable Long id) {
        return asientoService.getAsiento(id);
    }

    @GetMapping("/get-asiento/referencia/{referencia}")
    public ResponseEntity<?> getAsiento(@PathVariable String referencia) {
        return asientoService.getAsiento(referencia);
    }

    @GetMapping("/listar-asientos")
    public ResponseEntity<?> listarAsientos() {
        return asientoService.listarAsientos();
    }

    @GetMapping("/get-movimiento/{id}")
    public ResponseEntity<?> getMovimiento(@PathVariable Long id) {
        return asientoService.getMovimiento(id);
    }

    @GetMapping("/get-movimientos/{asientoId}")
    public ResponseEntity<?> getMovimientosPorAsiento(@PathVariable Long asientoId) {
        return asientoService.listarMovimientosPorAsiento(asientoId);
    }

    @GetMapping("/listar-movimientos")
    public ResponseEntity<?> listarMovimientos() {
        return asientoService.listarAsientos();
    }

    @GetMapping("/get-conciliacion/{id}")
    public ResponseEntity<?> getConciliacion(@PathVariable Long id) {
        return asientoService.getConciliacion(id);
    }

    @GetMapping("/listar-conciliaciones")
    public ResponseEntity<?> listarConciliaciones() {
        return asientoService.listarConciliaciones();
    }

    @GetMapping("/conciliaciones/{id}")
    public ResponseEntity<?> listarConciliacionesPorMovimiento(@PathVariable Long id) {
        return asientoService.buscarConciliacionPorMoviento(id);
    }

}
