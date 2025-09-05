package com.ar.base.controllers;

import com.ar.base.services.iReportesContablesService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contabilidad/reportes")
public class ReportesController {
    
    @Autowired
    private iReportesContablesService reportes;
    
    
    @PostMapping("/get/{id}")
    public ResponseEntity<?> generarLibroDiario(@RequestParam Date desde, @RequestParam Date hasta) {
        return this.reportes.generarLibroDiario(desde, hasta);
    }

    @PostMapping("/get-all")
    public ResponseEntity<?> generarLibroMayor(@RequestParam Date desde, @RequestParam Date hasta) {
        return this.reportes.generarLibroMayor(desde, hasta);
    }
}
