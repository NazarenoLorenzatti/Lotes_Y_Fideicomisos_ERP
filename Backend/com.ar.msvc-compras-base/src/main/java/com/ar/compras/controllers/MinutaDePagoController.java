package com.ar.compras.controllers;

import com.ar.compras.entities.MinutaDePago;
import com.ar.compras.services.iMinutaDePagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("compras/pago")
public class MinutaDePagoController {

    @Autowired
    private iMinutaDePagoService minutaDePagoService;

    /**
     * Guardar nueva minuta de Pago
     * @param minuta
     * @return 
     */
    @PostMapping("/crear")
    public ResponseEntity<?> buildMinutaDePago(@RequestBody MinutaDePago minuta){
        return minutaDePagoService.buildMinutaDePago(minuta);
    }

    /**
     * Editar Minuta de pago
     * @param minuta
     * @return 
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editMinutaDePago(@RequestBody MinutaDePago minuta){
         return minutaDePagoService.editMinutaDePago(minuta);
    }

    /**
     * Eliminar minuta de pago
     * @param id
     * @return 
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> deleteMinutaDePago(@PathVariable Long id){
        return minutaDePagoService.declineMinutaDePago(id);
    }
    
    /**
     * Confirmar minuta de pago
     * @param id
     * @return 
     */
    @GetMapping("/confirmar/{id}")
    public ResponseEntity<?> confirmMinutaDePago(@PathVariable Long id){
        return minutaDePagoService.confirmMinutaDePago(id);
    }

    /**
     * Rechazar minuta de pago
     * @param id
     * @return 
     */
    @GetMapping("/rechazar/{id}")
    public ResponseEntity<?> declineMinutaDePago(@PathVariable Long id){
        return minutaDePagoService.declineMinutaDePago(id);
    }

    /**
     * establecer como paga minuta de pago
     * @param id
     * @return 
     */
    @GetMapping("/pagar/{id}")
    public ResponseEntity<?> payMinutaDePago(@PathVariable Long id){
        return minutaDePagoService.payMinutaDePago(id);
    }

    /**
     * Obtener minuta de Pago
     * @param id
     * @return 
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<?> getMinutaDePago(@PathVariable Long id){
        return minutaDePagoService.getMinutaDePago(id);
    }

    /**
     * Rechazar minuta de pago
     * @return 
     */
    @GetMapping("/listar")
    public ResponseEntity<?> getAllMinutaDePago(){
        return minutaDePagoService.getAllMinutaDePago();
    }
}
