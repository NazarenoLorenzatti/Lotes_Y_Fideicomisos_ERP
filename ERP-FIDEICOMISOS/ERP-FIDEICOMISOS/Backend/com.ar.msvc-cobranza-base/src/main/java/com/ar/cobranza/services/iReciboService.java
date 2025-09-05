package com.ar.cobranza.services;

import org.springframework.http.ResponseEntity;

public interface iReciboService {

    public ResponseEntity<?> confirmarPreRecibo(Long idPreRecibo);

    public ResponseEntity<?> confirmarPreRecibo(Long idPreRecibo, Long idReciboAsociado);

    public ResponseEntity<?> findReciboAuxiliar(Long idRecibo);

    public ResponseEntity<?> findReciboOficial(Long idRecibo);

    public ResponseEntity<?> getRecibosAuxiliares();

    public ResponseEntity<?> getRecibosOficiales();

    public ResponseEntity<?> cancelarRecibo(Long id);
}
