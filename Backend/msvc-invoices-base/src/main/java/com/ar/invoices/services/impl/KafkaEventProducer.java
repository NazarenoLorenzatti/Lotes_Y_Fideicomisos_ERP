package com.ar.invoices.services.impl;

import com.ar.invoices.DTOs.ComprobanteConfirmadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventProducer {
   
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.comprobantes.confirmados}")
    private String topic;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEvento(ComprobanteConfirmadoEvent evento) {
        kafkaTemplate.send(topic, evento);
        log.info("Evento de comprobante confirmado enviado a Kafka: {}", evento);
    } 
}
