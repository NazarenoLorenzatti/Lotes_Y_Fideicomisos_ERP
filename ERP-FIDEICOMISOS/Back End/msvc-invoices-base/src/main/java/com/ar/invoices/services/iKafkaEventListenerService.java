package com.ar.invoices.services;

import com.ar.invoices.DTOs.ImporteAplicadoEvent;
import org.springframework.http.ResponseEntity;

public interface iKafkaEventListenerService {

    public void setSaldado(ImporteAplicadoEvent comprobanteSaldado);
}
