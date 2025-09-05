package com.ar.afip.controllers;

import ar.gov.afip.dif.facturaelectronica.*;
import com.ar.afip.services.impl.AfipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/afip")
public class AfipController {

    @Autowired
    private AfipServiceImpl afipService;
    

    // 🔹 1. Probar conexión
    @GetMapping("/test-connection")
    public ResponseEntity<?> testConnection() throws Exception {
        if (afipService.testConnectionAfip()) {
            return afipService.buildResponse("OK", "00", "Conexion Correcta con Afip", null, HttpStatus.OK);
        }
        return afipService.buildErrorResponse("ERROR", "-01", "No se puede establecer la conexion con AFIP");
    }

    // 🔹 2. Obtener token (TA)
    @GetMapping("/token")
    public ResponseEntity<?> getToken(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return afipService.getOrCreateTA(service, cuit);
    }

    // 🔹 3. Consultar puntos de venta
    @PostMapping("/puntos-venta")
    public ResponseEntity<FEPtoVentaResponse> consultarPuntosVenta(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        FEPtoVentaResponse response = afipService.consultarPuntosDeVenta(service, cuit);
        return ResponseEntity.ok(response);
    }

    // 🔹 4. Consultar último comprobante autorizado
    @GetMapping("/ultimo-comprobante")
    public ResponseEntity<FERecuperaLastCbteResponse> consultarUltimoComprobante(
            @RequestParam int ptoVta,
            @RequestParam int cbteTipo,
            @RequestParam String cuit,
            @RequestParam(defaultValue = "wsfe") String service
    ) throws Exception {
        FERecuperaLastCbteResponse response = afipService.consultarUltimoComprobante(ptoVta, cbteTipo,service, cuit);
        return ResponseEntity.ok(response);
    }

    // 🔹 Tipos de comprobantes
    @PostMapping("/tipos-comprobantes")
    public ResponseEntity<CbteTipoResponse> getTiposComprobantes(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarTiposDeComprobantes(service, cuit));
    }

// 🔹 Tipos de documentos
    @PostMapping("/tipos-documentos")
    public ResponseEntity<DocTipoResponse> getTiposDocumentos(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarTiposDeDocumentos(service, cuit));
    }

// 🔹 Tipos de IVA
    @PostMapping("/tipos-iva")
    public ResponseEntity<IvaTipoResponse> getTiposIva(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarTiposDeIva(service, cuit));
    }

// 🔹 Tipos de tributos
    @GetMapping("/tipos-tributos")
    public ResponseEntity<FETributoResponse> getTiposTributos(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarTiposDeTributos(service, cuit));
    }

// 🔹 Tipos de concepto
    @PostMapping("/tipos-concepto")
    public ResponseEntity<ConceptoTipoResponse> getTiposConcepto(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarTiposDeConcepto(service, cuit));
    }

// 🔹 Monedas
    @PostMapping("/monedas")
    public ResponseEntity<MonedaResponse> getMonedas(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarMonedas(service, cuit));
    }

// 🔹 Cotización de moneda
    @GetMapping("/cotizacion-dolar")
    public ResponseEntity<FECotizacionResponse> getCotizacionMoneda(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarCotizacionMoneda(service, cuit));
    }

    // 🔹 Cotización de moneda
    @GetMapping("/condiciones-iva")
    public ResponseEntity<CondicionIvaReceptorResponse> getCondicionIvaReceptor(@RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception {
        return ResponseEntity.ok(afipService.consultarCondicionDeIVAReceptor(service, cuit));
    }
    
    @PostMapping("/solicitar-cae")
    public ResponseEntity<FECAEResponse> solicitarCae(@RequestBody FECAERequest request, @RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit) throws Exception{
        return ResponseEntity.ok(afipService.solicitarCAE(request, service, cuit));
    }
    
}
