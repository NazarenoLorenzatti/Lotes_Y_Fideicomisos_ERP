package com.ar.invoices.services.impl;

import com.ar.invoices.DTOs.ImporteAplicadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.ar.invoices.services.iKafkaEventListenerService;

@Component
@Slf4j
public class KafkaEventListener implements iKafkaEventListenerService {
    
    @Autowired
    private ComprobantesServiceImpl comprobantesService;

    @KafkaListener(topics = "${topic.importe.aplicado}", containerFactory = "comprobanteSaldadoFactory")
    public void recibirFacturasProveedor(ImporteAplicadoEvent ImporteAplicado) {
        this.setSaldado(ImporteAplicado);
    }

    @Override
    public void setSaldado(ImporteAplicadoEvent ImporteAplicado) {
        comprobantesService.setSaldado(ImporteAplicado);
    }
}
