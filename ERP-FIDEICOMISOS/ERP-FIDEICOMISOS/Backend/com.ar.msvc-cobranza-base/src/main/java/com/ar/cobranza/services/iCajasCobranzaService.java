package com.ar.cobranza.services;

import com.ar.cobranza.entities.CajaCobranza;
import org.springframework.http.ResponseEntity;

public interface iCajasCobranzaService {
    
    public ResponseEntity<?> newCaja(CajaCobranza caja);
    
    public ResponseEntity<?> editCaja(CajaCobranza caja);
    
    public ResponseEntity<?> deleteCaja(Long idCaja);
    
    public ResponseEntity<?> cambiarEstadoCaja(Long idCaja);
    
    public ResponseEntity<?> getCaja(Long idCaja);
    
    public ResponseEntity<?> listarCajas();
    
    public ResponseEntity<?> listarCajasCobranza();
}
