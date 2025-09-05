package com.ar.cobranza.services;

import com.ar.cobranza.entities.EstadoRecibo;
import com.ar.cobranza.entities.PreRecibo;
import org.springframework.http.ResponseEntity;

public interface iPreReciboService {

    public ResponseEntity<?> initPreRecibo(PreRecibo preRecibo, Long idContacto);

    public ResponseEntity<?> editPreRecibo(PreRecibo preRecibo);

    public ResponseEntity<?> deletePreRecibo(Long idPreRecibo);

    public ResponseEntity<?> getPreRecibos();

    public ResponseEntity<?> getPreRecibosPorEstado(EstadoRecibo estado);

    public ResponseEntity<?> getPreRecibo(Long id);
}
