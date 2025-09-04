package com.ar.base.services.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventsFailsListener {

    private final HandleEventsFails handleEvents;

    public KafkaEventsFailsListener(ObjectMapper objectMapper, HandleEventsFails handleEvents) {
        this.handleEvents = handleEvents;
    }

    @KafkaListener(topics = "${topic.comprobantes.confirmados}.DLT", groupId = "dlt-group")
    public void recibirDLTComprobante(String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        handleEvents.recibirDLT(payload, topic, key);
    }

    @KafkaListener(topics = "${topic.recibos.confirmados}.DLT", groupId = "dlt-group")
    public void recibirDLTRecibo(String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        handleEvents.recibirDLT(payload, topic, key);
    }

    @KafkaListener(topics = "${topic.compras.factura-confirmada}.DLT", groupId = "dlt-group")
    public void recibirDLTFacturaProveedor(String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        handleEvents.recibirDLT(payload, topic, key);
    }

    @KafkaListener(topics = "${topic.compras.minuta-confirmada}.DLT", groupId = "dlt-group")
    public void recibirDLTMinuta(String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        handleEvents.recibirDLT(payload, topic, key);
    }

}
