package com.ar.invoices.services.impl.util;

import com.ar.invoices.entities.EventoFallido;
import com.ar.invoices.repositories.EventoFallidoDao;
import com.ar.invoices.responses.BuildResponsesServicesImpl;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HandleEventsFails extends BuildResponsesServicesImpl {

    private final EventoFallidoDao repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public HandleEventsFails(EventoFallidoDao repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void recibirDLT(String payload, String topic, String key) {
        EventoFallido evento = new EventoFallido();
        evento.setTopic(topic);
        evento.setPayload(payload);
        evento.setKey(key);
        evento.setErrorMensaje("Fallo de procesamiento");
        evento.setFechaFallo(LocalDateTime.now());
        evento.setReprocesado(false);

        repository.save(evento);
        log.error("Evento DLT guardado. Topic: {}, Key: {}, Payload: {}", topic, key, payload);
    }

    public ResponseEntity<?> listarEventosFallidos() {
        List<EventoFallido> list = repository.findByReprocesadoFalse();
        if (list.isEmpty()) {
            return buildResponse("OK", "00", "No hay eventos pendientes", list, HttpStatus.OK);
        }
        return buildResponse("OK", "00", "Eventos pendientes: " + list.size(), list, HttpStatus.OK);
    }

    public ResponseEntity<?> corregirEvento(Long id, String nuevoPayload) {
        EventoFallido evento = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setPayload(nuevoPayload);
        evento.setReprocesado(false);
        evento.setFechaFallo(LocalDateTime.now());
        repository.save(evento);

        return buildResponse("OK", "00", "Evento actualizado correctamente", evento, HttpStatus.OK);
    }

    public ResponseEntity<?> reprocesarEvento(Long id) {
        EventoFallido evento = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        String originalTopic = obtenerTopicOriginal(evento.getTopic());

        kafkaTemplate.send(originalTopic, evento.getKey(), evento.getPayload());
        evento.setReprocesado(true);
        repository.save(evento);

        log.info("Evento reprocesado con éxito. Topic: {}, Key: {}", originalTopic, evento.getKey());

        return buildResponse("OK", "00", "Evento reprocesado", null, HttpStatus.OK);
    }

    private String obtenerTopicOriginal(String dltTopic) {
        return dltTopic.replace(".DLT", "");
    }
}