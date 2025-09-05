package com.ar.invoices.services;

import com.ar.invoices.entities.EstadoComprobante;
import com.ar.invoices.entities.PreComprobante;
import org.springframework.http.ResponseEntity;

public interface iPreComprobanteService {

    public ResponseEntity<?> initPreComprobante(PreComprobante preComprobante, Long idContacto);

    public ResponseEntity<?> editPreComprobante(PreComprobante preComprobante);

    public ResponseEntity<?> deletePreComprobante(Long idPreComprobante);

    public ResponseEntity<?> getPreComprobantes(EstadoComprobante estado);

    public ResponseEntity<?> getPreComprobante(Long id);
}
