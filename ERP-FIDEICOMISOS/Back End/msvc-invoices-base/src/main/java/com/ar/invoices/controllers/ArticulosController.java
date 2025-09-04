package com.ar.invoices.controllers;

import com.ar.invoices.entities.Articulo;
import com.ar.invoices.services.iArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articulos")
@CrossOrigin("*")
public class ArticulosController {

    @Autowired
    private iArticuloService articuloService;

    /**
     * Guardar un nuevo articulo
     *
     * @param art
     * @return
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarArticulo(@RequestBody Articulo art) {
        return articuloService.guardarArticulo(art);
    }

    /**
     * Editar Articulo guardado
     * @param art
     * @return 
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editarArticulo(@RequestBody Articulo art) {
        return articuloService.editarArticulo(art);
    }
    
    /**
     * Marcar articulo como archivado
     * @param id
     * @return 
     */
    @GetMapping("/archivar/{id}")
    public ResponseEntity<?> archivarArticulo(@PathVariable Long id) {
        return articuloService.archivarArticulo(id);
    }

    /**
     * Obtener articulo Guardado
     * @param id
     * @return 
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<?> obtenerArticulo(@PathVariable Long id) {
        return articuloService.obtenerArticulo(id);
    }

    /**
     * listar todos los articulos guardados
     * @return 
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarArticulos() {
        return articuloService.listarArticulos();
    }
}
