package com.ar.compras.ClientsFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-accounting-base", url = "http://localhost:8104")
public interface ClientFeignCuentasContables {

    @GetMapping("/api/cuentas/get/{id}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable("id") Long id);

    @GetMapping("/api/cuentas/get-all")
    public ResponseEntity<?> listarCuentas();
}
