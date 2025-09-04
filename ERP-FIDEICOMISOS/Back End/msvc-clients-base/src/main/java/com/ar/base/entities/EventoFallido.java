package com.ar.base.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "eventos_fallidos")
public class EventoFallido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String payload;

    @Column(name = "key_evento")
    private String key;

    private String errorMensaje;
    private String stacktrace;

    private Boolean reprocesado;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime fechaFallo;
}
