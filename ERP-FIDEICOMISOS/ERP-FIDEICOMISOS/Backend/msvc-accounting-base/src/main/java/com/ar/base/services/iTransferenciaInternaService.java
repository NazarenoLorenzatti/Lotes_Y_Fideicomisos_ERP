package com.ar.base.services;

import com.ar.base.entities.TransferenciaInterna;
import org.springframework.http.ResponseEntity;

public interface iTransferenciaInternaService {

    public ResponseEntity<?> registrarTransferenciaInterna(TransferenciaInterna transferenciaInterna);
    
    public ResponseEntity<?> aprobarTransferencia(Long idTransferencia);
    
    public ResponseEntity<?> anularTransferencia(Long idTransferencia);
    
    public ResponseEntity<?> getTransferencia(Long idTransferencia);
    
    public ResponseEntity<?> listarTransferencias();
    
//    public ResponseEntity<?> registrarTransferenciaAsincronica(TransferenciaInterna transferenciaInterna);
}
