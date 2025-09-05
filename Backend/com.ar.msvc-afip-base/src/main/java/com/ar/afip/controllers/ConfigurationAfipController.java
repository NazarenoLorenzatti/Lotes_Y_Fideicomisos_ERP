package com.ar.afip.controllers;

import com.ar.afip.services.impl.ConfigurationAfipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/afip/configurations")
@CrossOrigin("*")
public class ConfigurationAfipController {

    @Autowired
    private ConfigurationAfipServiceImpl configurationService;

    /**
     * Obtener Punto de Venta para facturacion AFIP en base al nro del punto de
     * venta y el cuit
     *
     * @param nroPunto
     * @param cuit
     * @return
     */
    @PostMapping("/get-punto-venta/{nro}/{cuit}")
    public ResponseEntity<?> getPtoDeVenta(@PathVariable("nro") Integer nroPunto, @PathVariable("cuit") String cuit) {
        return configurationService.getPtoDeVenta(nroPunto, cuit);
    }

    /**
     * Obtener Lista de Puntos de Venta Informados
     *
     * @return
     */
    @GetMapping("/listar-ptos-venta")
    public ResponseEntity<?> listarPtosDeVenta() {
        return configurationService.listarPtoDeVenta();
    }
    
        /**
     * Obtener Lista de Puntos de Venta Informados
     *
     * @param cuit
     * @return
     */
    @GetMapping("/listar-ptos-venta/{cuit}")
    public ResponseEntity<?> listarPtosDeVentaCuit(@PathVariable("cuit") String cuit) {
        return configurationService.listarPtosDeVentaCuits(cuit);
    }

    /**
     * Obtener Lista de Puntos de Venta Informados
     *
     * @return
     */
    @GetMapping("/listar-cuits")
    public ResponseEntity<?> listarCuitsEmisores() {
        return configurationService.listarCuitEmisores();
    }

    /**
     * Obtener Tipos de Comprobante Aceptado por AFIP
     *
     * @param idAfip
     * @return
     */
    @PostMapping("/get-comprobante/{idAfip}")
    public ResponseEntity<?> getTipoComprobante(@PathVariable("idAfip") Integer idAfip) {
        return configurationService.getTipoComprobante(idAfip);
    }

    /**
     * Obtener Lista de Comprobantes aceptados por AFIP
     *
     * @return
     */
    @GetMapping("/listar-comprobantes")
    public ResponseEntity<?> listarTipoComprobante() {
        return configurationService.listarTipoComprobante();
    }

    /**
     * Obtener tipo de documento aceptado por AFIP
     *
     * @param idAfip
     * @return
     */
    @PostMapping("/get-docuemtno/{idAfip}")
    public ResponseEntity<?> getTipoDocumento(@PathVariable("idAfip") Integer idAfip) {
        return configurationService.getTipoDeDocumento(idAfip);
    }

    /**
     * Obtener listado de Documentos Aceptados por AFIP
     *
     * @return
     */
    @GetMapping("/listar-documentos")
    public ResponseEntity<?> listarTipoDocumento() {
        return configurationService.listarTipoDeDocumento();
    }

    /**
     * Obtener tipo de condicion frente al IVA aceptada por AFIP
     *
     * @param idAfip
     * @return
     */
    @GetMapping("/get-condicion/{idAfip}")
    public ResponseEntity<?> getCondicionIva(@PathVariable("idAfip") Integer idAfip) {
        return configurationService.getCondicionIvaReceptor(idAfip);
    }

    /**
     * Obtener Listado de condicion frente al Iva de AFIP
     *
     * @return
     */
    @GetMapping("/listar-condicion")
    public ResponseEntity<?> listarCondicionesIva() {
        return configurationService.listarCondicionIvaReceptor();
    }

    /**
     * Obtener Tipos de Alicuotas Aceptadas por AFIP
     *
     * @param descripcion
     * @return
     */
    @GetMapping("/get-alicuota/{descripcion}")
    public ResponseEntity<?> getAlicuotaIva(@PathVariable("idAfip") String descripcion) {
        return configurationService.getAlicuotasIva(descripcion);
    }

    /**
     * Listar Alicuotas de Iva aceptadas por AFIP
     *
     * @return
     */
    @GetMapping("/listar-alicuotas")
    public ResponseEntity<?> listarAlicuotasIva() {
        return configurationService.listarAlicuotasIva();
    }

    /**
     * Listar Alicuotas de Iva aceptadas por AFIP
     *
     * @return
     */
    @GetMapping("/listar-ciudades")
    public ResponseEntity<?> listarCiudades() {
        return configurationService.listarCiudades();
    }
}
