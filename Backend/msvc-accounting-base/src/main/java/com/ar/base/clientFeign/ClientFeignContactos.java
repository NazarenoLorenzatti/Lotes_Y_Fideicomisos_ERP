package com.ar.base.clientFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-contactos-base", url = "http://localhost:8102")
public interface ClientFeignContactos {

    @GetMapping("/cuentas/anular/{comprobanteNro}")
    public ResponseEntity<?> anularAplicacion(@PathVariable("comprobanteNro") String comprobanteNro);
    
    @GetMapping("/contactos/status")
    public ResponseEntity<String> getStatusMsvc();
}
