package com.ar.invoices.services.impl.util;

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

    @KafkaListener(topics = "${topic.importe.aplicado}.DLT", groupId = "dlt-group")
    public void recibirImporteAplicado(String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        handleEvents.recibirDLT(payload, topic, key);
    }

}
