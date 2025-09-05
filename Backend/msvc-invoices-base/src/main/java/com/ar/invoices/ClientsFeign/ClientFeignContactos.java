package com.ar.invoices.ClientsFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-contactos-base", url = "http://localhost:8102")
public interface ClientFeignContactos {

    @GetMapping("/contactos/find/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable("id") Long id);
    
    @GetMapping("/contactos/status")
    public ResponseEntity<String> getStatusMsvc();
}
