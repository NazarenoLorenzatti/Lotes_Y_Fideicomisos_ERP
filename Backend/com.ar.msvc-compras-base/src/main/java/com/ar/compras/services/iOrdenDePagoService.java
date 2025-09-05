package com.ar.compras.services;

import com.ar.compras.entities.*;
import org.springframework.http.ResponseEntity;

public interface iOrdenDePagoService {

    public ResponseEntity<?> buildOrdenDePago(OrdenDePago orden);

    public ResponseEntity<?> editOrdenDePago(OrdenDePago orden);

    public ResponseEntity<?> setEstadoOrdenDePago(Long id, OrdenDePago.Estado estado);
    
    public ResponseEntity<?> getOrdenDePago(Long id);
    
    public ResponseEntity<?> getAllOrdenDePago();

}
