package com.ar.compras.controllers;

import com.ar.compras.entities.OrdenDePago;
import com.ar.compras.services.iOrdenDePagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("compras/orden-pago")
public class OrdenDePagoController {
    
    @Autowired
    private iOrdenDePagoService ordenDePago;

    @PostMapping("/crear")
    public ResponseEntity<?> buildOrdenDePago(@RequestBody OrdenDePago orden){
        return ordenDePago.buildOrdenDePago(orden);
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editOrdenDePago(@RequestBody OrdenDePago orden){
        return ordenDePago.editOrdenDePago(orden);
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<?> setEstadoOrdenDePago(@PathVariable Long id, @RequestParam OrdenDePago.Estado estado){
        return ordenDePago.setEstadoOrdenDePago(id, estado);
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<?> getOrdenDePago(@PathVariable Long id){
         return ordenDePago.getOrdenDePago(id);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> getAllOrdenDePago(){
               return ordenDePago.getAllOrdenDePago();
    }
}
