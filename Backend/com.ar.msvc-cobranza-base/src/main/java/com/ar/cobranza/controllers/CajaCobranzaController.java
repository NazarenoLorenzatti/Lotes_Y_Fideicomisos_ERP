package com.ar.cobranza.controllers;

import com.ar.cobranza.entities.CajaCobranza;
import com.ar.cobranza.services.iCajasCobranzaService;
import com.ar.cobranza.services.impl.CajasCobranzaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cajas")
@CrossOrigin("*")
public class CajaCobranzaController {

    @Autowired
    private iCajasCobranzaService cajasService;

    /**
     * Guardar una caja o Banco Asociada a una cuenta contable
     *
     * @param caja
     * @return
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> crearPreComprobante(@RequestBody CajaCobranza caja) {
        return cajasService.newCaja(caja);
    }

    /**
     * Editar Caja de Cobranza
     *
     * @param caja
     * @return
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editCaja(@RequestBody CajaCobranza caja) {
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

    @GetMapping("/listar/cobranza")
    public ResponseEntity<?> listarCajasCobranza() {
        return cajasService.listarCajas();
    }
}
