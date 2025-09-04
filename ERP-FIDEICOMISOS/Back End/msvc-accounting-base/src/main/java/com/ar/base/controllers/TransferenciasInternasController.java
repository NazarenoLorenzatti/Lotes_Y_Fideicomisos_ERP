package com.ar.base.controllers;

import com.ar.base.entities.TransferenciaInterna;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ar.base.services.iTransferenciaInternaService;

@RestController
@RequestMapping("/api/transferencias-internas")
@RequiredArgsConstructor
public class TransferenciasInternasController {

    @Autowired
    private iTransferenciaInternaService transferenciaInterna;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarTransferenciaInterna(@RequestBody TransferenciaInterna trasnferenciaInterna) {
        return this.transferenciaInterna.registrarTransferenciaInterna(trasnferenciaInterna);
    }

    @GetMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobarTransferencia(@PathVariable("id") Long id) {
        return this.transferenciaInterna.aprobarTransferencia(id);
    }

    @DeleteMapping("/anular/{id}")
    public ResponseEntity<?> anularTransferencia(@PathVariable("id") Long id) {
        return this.transferenciaInterna.anularTransferencia(id);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getTransferencia(@PathVariable("id") Long id) {
        return this.transferenciaInterna.getTransferencia(id);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarTransferencias() {
        return this.transferenciaInterna.listarTransferencias();
    }

//    @PostMapping("/registrar/asincrona")
//    public ResponseEntity<?> registrarTransferenciaAsincronica(@RequestParam Long cuentaOrigenId, @RequestParam Long cuentaDestinoId,
//            @RequestParam Double importe, @RequestParam String descripcion, @RequestParam Long contactoId,
//            @RequestParam Long idTipoEntidad) {
//        return this.transferenciaInterna.registrarTransferenciaAsincronica(cuentaOrigenId, cuentaDestinoId, importe,
//                 descripcion, contactoId, idTipoEntidad);
//    }
}
