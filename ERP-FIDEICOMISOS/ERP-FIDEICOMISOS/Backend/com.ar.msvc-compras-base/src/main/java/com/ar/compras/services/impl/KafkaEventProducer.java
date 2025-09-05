package com.ar.compras.services.impl;

import com.ar.compras.DTOs.FacturaProveedorEvent;
import com.ar.compras.DTOs.MinutaDePagoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventProducer {

    private final KafkaTemplate<String, FacturaProveedorEvent> kafkaTemplateFactura;
    private final KafkaTemplate<String, MinutaDePagoEvent> kafkaTemplateMinuta;

    @Value("${topic.compras.factura-confirmada}")
    private String topicFactura;

    @Value("${topic.compras.minuta-confirmada}")
    private String topicMinutas;

    public KafkaEventProducer(KafkaTemplate<String, FacturaProveedorEvent> kafkaTemplateFactura,
            KafkaTemplate<String, MinutaDePagoEvent> kafkaTemplateMinuta) {
        this.kafkaTemplateFactura = kafkaTemplateFactura;
        this.kafkaTemplateMinuta = kafkaTemplateMinuta;
    }

    public void publicarEventoFactura(FacturaProveedorEvent evento) {
        this.kafkaTemplateFactura.send(topicFactura, evento);
        log.info("Evento de Factura de proveedor confirmada enviado a Kafka: {}", evento);
    }

    public void publicarEventoMinutas(MinutaDePagoEvent evento) {
        this.kafkaTemplateMinuta.send(topicMinutas, evento);
        log.info("Evento de Minuta confirmada enviado a Kafka: {}", evento);
    }

//    public void publicarEventoFacturaPagada(FacturaProveedorEvent evento) {
//        this.kafkaTemplateFactura.send(topicFacturaPagada, evento);
//        log.info("Evento de Factura de proveedor confirmada enviado a Kafka: {}", evento);
//    }
}
