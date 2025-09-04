package com.ar.base.controllers;

import com.ar.base.services.util.HandleEventsFails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dlt")
public class DltReprocessorController {

 
    private final HandleEventsFails handler;

    public DltReprocessorController(HandleEventsFails handler) {
        this.handler = handler;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return handler.listarEventosFallidos();
    }

    @PostMapping("/{id}/reprocesar")
    public ResponseEntity<?> reprocesar(@PathVariable Long id) {
        return handler.reprocesarEvento(id);
    }

    @PutMapping("/{id}/corregir")
    public ResponseEntity<?> corregir(@PathVariable Long id, @RequestBody String nuevoPayload) {
        return handler.corregirEvento(id, nuevoPayload);
    }
}
