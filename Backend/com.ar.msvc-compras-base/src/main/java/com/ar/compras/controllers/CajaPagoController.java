package com.ar.compras.controllers;

import com.ar.compras.entities.CajaPagos;
import com.ar.compras.services.iCajasCobranzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("compras/cajas")
public class CajaPagoController {
    
    @Autowired
    private iCajasCobranzaService cajasService;

    /**
     * Guardar una caja o Banco Asociada a una cuenta contable
     *
     * @param caja
     * @return
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> crearPreComprobante(@RequestBody CajaPagos caja) {
        return cajasService.newCaja(caja);
    }

    /**
     * Editar Caja de Cobranza
     *
     * @param caja
     * @return
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editCaja(@RequestBody CajaPagos caja) {
        return cajasService.editCaja(caja);
    }

    /**
     * Eliminar Caja de Cobranza
     *
     * @param idCaja
     * @return
     */
    @DeleteMapping("/eliminar/{idCaja}")
    public ResponseEntity<?> deleteCaja(@PathVariable Long idCaja) {
        return cajasService.deleteCaja(idCaja);
    }

    /**
     * Cambiar el estado entre Activa o Inactiva
     *
     * @param idCaja
     * @return
     */
    @PutMapping("/cambiar-estado/{idCaja}")
    public ResponseEntity<?> cambiarEstadoCaja(@PathVariable Long idCaja) {
        return cajasService.cambiarEstadoCaja(idCaja);
    }

    /**
     * Obtener Caja
     *
     * @param idCaja
     * @return
     */
    @GetMapping("/get/{idCaja}")
    public ResponseEntity<?> getCaja(@PathVariable Long idCaja) {
        return cajasService.getCaja(idCaja);
    }

    /**
     * Obtener Caja
     *
     * @return
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarCajas() {
        return cajasService.listarCajas();
    }
}
