package com.ar.afip.controllers;

import com.ar.afip.services.impl.ComprobanteSecuenciaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/afip/configurations/secuencias")
public class ComprobanteSecuenciaController {

    @Autowired
    private ComprobanteSecuenciaServiceImpl secuenciaService;

    /**
     * Obtener ultimo numero de secuencia guardado
     *
     * @param idTipo
     * @param nroPunto
     * @param cuit
     * @return
     */
    @PostMapping("/ultimo-numero")
    public ResponseEntity<Integer> getLastNumber(
            @RequestParam("idTipo") int idTipo,
            @RequestParam("nroPunto") int nroPunto,
            @RequestParam("cuit") String cuit) {
        return this.secuenciaService.obtenerUltimoNumero(idTipo, nroPunto, cuit);
    }

    /**
     * Guardar Secuencia nueva
     *
     * @param idTipo
     * @param nroPunto
     * @param cuit
     * @param numeroSecuencia
     * @return
     */
    @PostMapping("/guardar-numero")
    public ResponseEntity<?> saveSecuencia(
            @RequestParam("idTipo") int idTipo,
            @RequestParam("nroPunto") int nroPunto,
            @RequestParam("cuit") String cuit,
            @RequestParam("nro") int numeroSecuencia) {
        return this.secuenciaService.guardarSecuencia(idTipo, nroPunto, cuit, numeroSecuencia);
    }

    /**
     * Sincronizar Numeracion de puntos de Ventas con comprobantes
     *
     * @return
     */
    @GetMapping("/sincronizar")
    public ResponseEntity<?> sincronizar() {
        return this.secuenciaService.sincronizarNumeracionConAfip();
    }

    /**
     * Iniciar secuencias de Ptos de ventas y numeros de comprobantes
     * @param cuit
     * @return 
     */
    @GetMapping("/init-numeracion")
    public ResponseEntity<?> initNumeraciones(@RequestParam("cuit") String cuit) {
        return this.secuenciaService.initNumeracionConAfip(cuit);
    }
}
