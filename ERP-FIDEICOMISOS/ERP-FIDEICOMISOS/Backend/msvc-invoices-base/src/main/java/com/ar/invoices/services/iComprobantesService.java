package com.ar.invoices.services;

import org.springframework.http.ResponseEntity;

public interface iComprobantesService {
    
    public ResponseEntity<?> confirmarPreComprobante(Long idPreComprobante);
    public ResponseEntity<?> confirmarPreComprobante(Long idPreComprobante, Long idComprobanteAsociado);
    public ResponseEntity<?> findComprobanteAuxiliar(Long idComprobante);
    public ResponseEntity<?> findComprobanteOficial(Long idComprobante);
    public ResponseEntity<?> getComprobantesAuxiliares(boolean findPendientes);
    public ResponseEntity<?> getComprobantesOficiales(boolean findPendientes);
}
