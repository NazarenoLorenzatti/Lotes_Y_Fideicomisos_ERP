package com.ar.compras.services;

import com.ar.compras.entities.CajaPagos;
import org.springframework.http.ResponseEntity;

public interface iCajasCobranzaService {
    
    public ResponseEntity<?> newCaja(CajaPagos caja);
    
    public ResponseEntity<?> editCaja(CajaPagos caja);
    
    public ResponseEntity<?> deleteCaja(Long idCaja);
    
    public ResponseEntity<?> cambiarEstadoCaja(Long idCaja);
    
    public ResponseEntity<?> getCaja(Long idCaja);
    
    public ResponseEntity<?> listarCajas();
}
