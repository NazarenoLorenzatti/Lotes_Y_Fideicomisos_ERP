package com.ar.compras.controllers;

import com.ar.compras.entities.Articulo;
import com.ar.compras.services.iArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("compras/articulos")
public class ArticuloController {

    @Autowired
    private iArticuloService articuloService;

    /**
     * Guardar un Nuevo Articulo
     * @param articulo
     * @return 
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> newArticulo(@RequestBody Articulo articulo) {
        return articuloService.newArticulo(articulo);
    }

    /**
     * Editar un articulo Existente
     * @param articulo
     * @return 
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editArticulo(@RequestBody Articulo articulo) {
        return articuloService.editArticulo(articulo);
    }

    /**
     * Archivar Articulo
     * @param idArticulo
     * @return 
     */
    @GetMapping("/archivar/{idArticulo}")
    public ResponseEntity<?> archivarDesarchivarArticulo(@PathVariable Long idArticulo) {
        return articuloService.archivarDesarchivarArticulo(idArticulo);
    }

    /**
     * Listar todos los articulos Cargados
     * @return 
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarArticulos() {
        return articuloService.listarArticulos();
    }
    
    
}
