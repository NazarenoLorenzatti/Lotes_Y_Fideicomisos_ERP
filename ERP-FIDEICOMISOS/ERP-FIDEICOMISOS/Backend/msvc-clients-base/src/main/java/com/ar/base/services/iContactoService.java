package com.ar.base.services;

import com.ar.base.entities.Contacto;
import org.springframework.http.ResponseEntity;

public interface iContactoService {

    public ResponseEntity<?> getContacto(Long id);

    public ResponseEntity<?> saveContacto(Contacto contacto);

    public ResponseEntity<?> deleteContacto(Long id);
    
    public ResponseEntity<?> confirmarContacto(Long id);

    public ResponseEntity<?> editContacto(Contacto contacto);
    
    public ResponseEntity<?> getAllEstados();
    
   public ResponseEntity<?> getAllContacto2();

    public ResponseEntity<?> getAllContacto();
    
    public ResponseEntity<?> setEstadoAutomatico();
    
    public ResponseEntity<?> setEstadoBajaTemprana(Long id);
    
    public ResponseEntity<?> setEstadoBaja(Long id);
    
    public ResponseEntity<?> setEstadoArchivado(Long id);
}
