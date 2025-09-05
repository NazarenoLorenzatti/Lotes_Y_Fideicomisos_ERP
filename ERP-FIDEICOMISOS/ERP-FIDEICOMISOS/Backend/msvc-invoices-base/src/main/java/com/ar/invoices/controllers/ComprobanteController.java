package com.ar.invoices.controllers;

import com.ar.invoices.entities.EstadoComprobante;
import com.ar.invoices.entities.PreComprobante;
import com.ar.invoices.services.impl.ComprobantesServiceImpl;
import com.ar.invoices.services.impl.PreComprobanteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comprobantes")
@CrossOrigin("*")
public class ComprobanteController {

    @Autowired
    private PreComprobanteServiceImpl preComprobantesService;

    @Autowired
    private ComprobantesServiceImpl comprobantesService;

    /**
     * Crear y guardar un pre comprobante asociado a un contacto
     *
     * @param comprobante
     * @param contactoId
     * @return
     */
    @PostMapping("/crear-precomprobante/{contacto-id}")
    public ResponseEntity<?> crearPreComprobante(@RequestBody PreComprobante comprobante, @PathVariable("contacto-id") Long contactoId) {
        
        return preComprobantesService.initPreComprobante(comprobante, contactoId);
    }

    /**
     * Editar un pre comprobante que se encuentra en Borrador
     *
     * @param comprobante
     * @return
     */
    @PutMapping("/editar-precomprobante")
    public ResponseEntity<?> editarPreComprobante(@RequestBody PreComprobante comprobante) {
        return preComprobantesService.editPreComprobante(comprobante);
    }

    /**
     * Eliminar Pre Comprobante en Borrador
     *
     * @param id
     * @return
     */
    @DeleteMapping("/eliminar-precomprobante/{id}")
    public ResponseEntity<?> eliminarPreComprobante(@PathVariable("id") Long id) {
        return preComprobantesService.deletePreComprobante(id);
    }
    
    @GetMapping("/obtener/pre/{id}")
    public ResponseEntity<?> getPreComprobante(@PathVariable("id")Long id) {
        return preComprobantesService.getPreComprobante(id);
    }

    /**
     * Endpoints Para Listar Comprobantes y Pre Comprobantes
     *
     * @return
     */
    @GetMapping("/listar-precomprobantes")
    public ResponseEntity<?> listarPreComprobantes() {
        return preComprobantesService.getPreComprobantes(null);
    }

    @PostMapping("/listar-precomprobantes")
    public ResponseEntity<?> listarPreComprobantes(@RequestBody EstadoComprobante estado) {
        return preComprobantesService.getPreComprobantes(estado);
    }

    @GetMapping("/listar-comprobantes")
    public ResponseEntity<?> listarComprobantes() {
        return comprobantesService.getComprobantesOficiales(false);
    }

    @GetMapping("/listar-comprobantesaux")
    public ResponseEntity<?> listarComprobantesAuxiliares() {
        return comprobantesService.getComprobantesAuxiliares(false);
    }

    @GetMapping("/listar-comprobantes-pendientes")
    public ResponseEntity<?> listarComprobantesPendientes() {
        return comprobantesService.getComprobantesOficiales(true);
    }

    @GetMapping("/listar-comprobantesaux-pendientes")
    public ResponseEntity<?> listarComprobantesAuxiliaresPendientes() {
        return comprobantesService.getComprobantesAuxiliares(true);
    }

    /**
     * Confirmar un Pre comprobante
     *
     * @param preComprobanteId
     * @return
     */
    @PostMapping("/confirmar-precomprobante/{pre-comprobante-id}")
    public ResponseEntity<?> confirmarComprobante(@PathVariable("pre-comprobante-id") Long preComprobanteId) {
        return comprobantesService.confirmarPreComprobante(preComprobanteId);
    }

    /**
     * Confirmar un Pre comprobante con Comprobante Asociado
     *
     * @param preComprobanteId
     * @param comprobanteAsociadoId
     * @return
     */
    @PostMapping("/confirmar-precomprobante/{pre-comprobante-id}/{comprobante-asociado-id}")
    public ResponseEntity<?> confirmarComprobante(@PathVariable("pre-comprobante-id") Long preComprobanteId, @PathVariable("comprobante-asociado-id") Long comprobanteAsociadoId) {
        return comprobantesService.confirmarPreComprobante(preComprobanteId, comprobanteAsociadoId);
    }

    /**
     * Buscar Comprobante
     *
     * @param id
     * @return
     */
    @GetMapping("/buscar-comprobante/{id}")
    public ResponseEntity<?> buscarComprobante(@PathVariable("id") Long id) {
        return comprobantesService.findComprobanteOficial(id);
    }

    /**
     * Buscar Comprobante Auxiliar
     *
     * @param id
     * @return
     */
    @GetMapping("/buscar-comprobante-aux/{id}")
    public ResponseEntity<?> buscarComprobanteAux(@PathVariable("id") Long id) {

        return comprobantesService.findComprobanteAuxiliar(id);
    }

}
