
package com.ar.invoices.services;

import com.ar.invoices.entities.Articulo;
import org.springframework.http.ResponseEntity;

public interface iArticuloService {
    
    public ResponseEntity<?> guardarArticulo(Articulo art);
    public ResponseEntity<?> editarArticulo(Articulo art);
    public ResponseEntity<?> archivarArticulo(Long id);
    public ResponseEntity<?> obtenerArticulo(Long id);
    public ResponseEntity<?> listarArticulos();
}
