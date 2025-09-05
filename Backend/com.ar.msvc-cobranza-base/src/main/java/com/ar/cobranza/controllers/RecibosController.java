package com.ar.cobranza.controllers;

import com.ar.cobranza.entities.EstadoRecibo;
import com.ar.cobranza.entities.PreRecibo;
import com.ar.cobranza.services.iPreReciboService;
import com.ar.cobranza.services.impl.ReciboServiceImpl;
import com.ar.cobranza.services.impl.PreReciboServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recibos")
@CrossOrigin("*")
public class RecibosController {

    @Autowired
    private iPreReciboService preRecibosService;

    @Autowired
    private ReciboServiceImpl RecibosService;

    /**
     * Crear y guardar un pre comprobante asociado a un contacto
     *
     * @param recibo
     * @param contactoId
     * @return
     */
    @PostMapping("/crear-pre-recibo/{contacto-id}")
    public ResponseEntity<?> crearPreComprobante(@RequestBody PreRecibo recibo, @PathVariable("contacto-id") Long contactoId) {
        return preRecibosService.initPreRecibo(recibo, contactoId);
    }

    /**
     * Editar un pre comprobante que se encuentra en Borrador
     *
     * @param recibo
     * @return
     */
    @PutMapping("/editar-pre-recibo")
    public ResponseEntity<?> editarPreComprobante(@RequestBody PreRecibo recibo) {
        return preRecibosService.editPreRecibo(recibo);
    }

    /**
     * Eliminar Pre Comprobante en Borrador
     *
     * @param id
     * @return
     */
    @DeleteMapping("/eliminar-pre-recibo/{id}")
    public ResponseEntity<?> eliminarPreComprobante(@PathVariable("id") Long id) {
        return preRecibosService.deletePreRecibo(id);
    }

    /**
     * Lista de Pre Comprobantes
     *
     * @return
     */
    @GetMapping("/listar-pre-recibos")
    public ResponseEntity<?> listarPreRecibo() {
        return preRecibosService.getPreRecibos();
    }

    @PostMapping("/listar-pre-recibos")
    public ResponseEntity<?> listarPreReciboPorEstado(@RequestBody EstadoRecibo estado) {
        return preRecibosService.getPreRecibosPorEstado(estado);
    }

    @GetMapping("/listar-recibos")
    public ResponseEntity<?> listarRecibos() {
        return RecibosService.getRecibosOficiales();
    }

    @GetMapping("/listar-recibos/aux")
    public ResponseEntity<?> listarRecibosAuxiliares() {
        return RecibosService.getRecibosAuxiliares();
    }

    /**
     * Confirmar un Pre comprobante
     *
     * @param preReciboId
     * @return
     */
    @PostMapping("/confirmar-prerecibo/{pre-comprobante-id}")
    public ResponseEntity<?> confirmarRecibo(@PathVariable("pre-comprobante-id") Long preReciboId) {
        return RecibosService.confirmarPreRecibo(preReciboId);
    }

    /**
     * Confirmar un Pre comprobante con Comprobante Asociado
     *
     * @param preReciboId
     * @param comprobanteAsociadoId
     * @return
     */
    @PostMapping("/confirmar-prerecibo/{pre-recibo-id}/{comprobante-asociado-id}")
    public ResponseEntity<?> confirmarRecibo(@PathVariable("pre-recibo-id") Long preReciboId, @PathVariable("comprobante-asociado-id") Long comprobanteAsociadoId) {
        return RecibosService.confirmarPreRecibo(preReciboId, comprobanteAsociadoId);
    }

    /**
     *
     *
     * @param preReciboId
     * @return
     */
    @PostMapping("/contrarecibo/{pre-comprobante-id}")
    public ResponseEntity<?> contraRecibo(@PathVariable("pre-comprobante-id") Long preReciboId) {
        return RecibosService.cancelarRecibo(preReciboId);
    }
    
        /**
     * Buscar Comprobante
     *
     * @param id
     * @return
     */
    @GetMapping("/pre-recibo/obtener/{id}")
    public ResponseEntity<?> buscarPreRecibo(@PathVariable("id") Long id) {
        return preRecibosService.getPreRecibo(id);
    }

    /**
     * Buscar Comprobante
     *
     * @param id
     * @return
     */
    @GetMapping("/buscar-recibo/{id}")
    public ResponseEntity<?> buscarRecibo(@PathVariable("id") Long id) {
        return RecibosService.findReciboOficial(id);
    }

    /**
     * Buscar Comprobante Auxiliar
     *
     * @param id
     * @return
     */
    @GetMapping("/buscar-recibo-aux/{id}")
    public ResponseEntity<?> buscarReciboAux(@PathVariable("id") Long id) {
        return RecibosService.findReciboAuxiliar(id);
    }

}
