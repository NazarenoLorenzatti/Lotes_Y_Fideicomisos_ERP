package com.ar.cobranza.ClientsFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "msvc-invoices-base", url = "http://localhost:8101")
public interface ClientFeignComprobantes {

    @GetMapping("/listar-comprobantes-pendientes")
    public ResponseEntity<?> listarComprobantes();

    @GetMapping("/listar-comprobantesaux-pendientes")
    public ResponseEntity<?> listarComprobantesAuxiliares();
}
