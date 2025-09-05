package com.ar.cobranza.ClientsFeign;

import ar.gov.afip.dif.facturaelectronica.FECAERequest;
import ar.gov.afip.dif.facturaelectronica.FECAEResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-afip-base", url = "http://localhost:8100")
public interface ClientFeignAfipConfigurations {

    @PostMapping("/afip/configurations/get-punto-venta/{nro}/{cuit}")
    public ResponseEntity<?> getPtoDeVenta(@PathVariable("nro") Integer nroPunto, @PathVariable("cuit") String cuit);

    @GetMapping("/afip/configurations/listar-ptos-venta")
    public ResponseEntity<?> listarPtosDeVenta();

    @PostMapping("/afip/configurations/get-comprobante/{idAfip}")
    public ResponseEntity<?> getTipoComprobante(@PathVariable("idAfip") Integer idAfip);

    @GetMapping("/afip/configurations/listar-comprobantes")
    public ResponseEntity<?> listarTipoComprobante();

    @PostMapping("/afip/configurations/get-docuemtno/{idAfip}")
    public ResponseEntity<?> getTipoDocumento(@PathVariable("idAfip") Integer idAfip);

    @GetMapping("/afip/configurations/listar-docuemntos")
    public ResponseEntity<?> listarTipoDocumento();

    @GetMapping("/afip/configurations/get-condicion/{idAfip}")
    public ResponseEntity<?> getCondicionIva(@PathVariable("idAfip") Integer idAfip);

    @GetMapping("/afip/configurations/listar-condicion")
    public ResponseEntity<?> listarCondicionesIva();

    @GetMapping("/afip/configurations/get-alicuota/{descripcion}")
    public ResponseEntity<?> getAlicuotaIva(@PathVariable("idAfip") String descripcion);

    @GetMapping("/afip/configurations/listar-alicuotas")
    public ResponseEntity<?> listarAlicuotasIva();

    @PostMapping("/afip/configurations/secuencias/ultimo-numero")
    public ResponseEntity<?> getLastNumber(@RequestParam("idTipo") int idTipo, @RequestParam("nroPunto") int nroPunto,
            @RequestParam("cuit") String cuit);

    @PostMapping("/afip/configurations/secuencias/guardar-numero")
    public ResponseEntity<?> saveSecuencia(
            @RequestParam("idTipo") int idTipo, @RequestParam("nroPunto") int nroPunto, @RequestParam("cuit") String cuit,
            @RequestParam("nro") int numeroSecuencia);

    @GetMapping("/afip/configurations/secuencias/sincronizar")
    public ResponseEntity<?> sincronizar();

    @PostMapping("/afip/solicitar-cae")
    public ResponseEntity<FECAEResponse> solicitarCae(@RequestBody FECAERequest request,
            @RequestParam(defaultValue = "wsfe") String service, @RequestParam("cuit") String cuit);

}
