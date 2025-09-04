package com.ar.base.services.impl;

import com.ar.base.DTOs.ImporteAplicadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventProducer {
   
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.importe.aplicado}")
    private String topic;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoImporteAplicado(ImporteAplicadoEvent evento) {
        kafkaTemplate.send(topic, evento);
        log.info("Evento de comprobante Saldado enviado a Kafka: {}", evento);
    } 
}
