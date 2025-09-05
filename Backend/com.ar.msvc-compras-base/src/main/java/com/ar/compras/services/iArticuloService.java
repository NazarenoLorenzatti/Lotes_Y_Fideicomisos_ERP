package com.ar.compras.services;

import com.ar.compras.entities.Articulo;
import org.springframework.http.ResponseEntity;

public interface iArticuloService {
    
    public ResponseEntity<?> newArticulo(Articulo articulo);
    public ResponseEntity<?> editArticulo(Articulo articulo);
    public ResponseEntity<?> archivarDesarchivarArticulo(Long idArticulo);
    public ResponseEntity<?> listarArticulos();
}
