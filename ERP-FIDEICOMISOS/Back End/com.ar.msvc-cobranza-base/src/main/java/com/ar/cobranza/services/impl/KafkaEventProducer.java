package com.ar.cobranza.services.impl;

import com.ar.cobranza.DTOs.ComprobanteConfirmadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventProducer {
   
    private final KafkaTemplate<String, ComprobanteConfirmadoEvent> kafkaTemplate;

    @Value("${topic.recibos.confirmados}")
    private String topic;

    public KafkaEventProducer(KafkaTemplate<String, ComprobanteConfirmadoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEvento(ComprobanteConfirmadoEvent evento) {
        kafkaTemplate.send(topic, evento);
        log.info("Evento de recibo confirmado enviado a Kafka: {}", evento);
    } 
}
