package com.ar.base.controllers;

import com.ar.base.entities.Contacto;
import com.ar.base.services.iContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contactos")
@CrossOrigin("*")
public class ContactosController {

    @Autowired
    private iContactoService contactoService;

    /**
     * Buscar Contacto por ID
     *
     * @param id
     * @return
     */
    @GetMapping("/find/{id}")
    public ResponseEntity<?> getContacto(@PathVariable("id") Long id) {
        return contactoService.getContacto(id);
    }

    /**
     * Listar todos los contactos
     *
     * @return
     */
    @GetMapping("/find-all")
    public ResponseEntity<?> getAllContactos() {
        return contactoService.getAllContacto();
    }
    
        /**
     * Listar todos los contactos
     *
     * @return
     */
    @GetMapping("/find-all2")
    public ResponseEntity<?> getAllContactos2() {
        return contactoService.getAllContacto2();
    }


    /**
     * Guardar un nuevo contacto
     *
     * @param contacto
     * @return
     */
    @PostMapping("/saved")
    public ResponseEntity<?> saveContacto(@RequestBody Contacto contacto) {
        return contactoService.saveContacto(contacto);
    }

    /**
     * Eliminar Contacto en Borrador
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContacto(@PathVariable("id") Long id) {
        return contactoService.deleteContacto(id);
    }

    /**
     * Confirmar Contacto
     *
     * @param id
     * @return
     */
    @GetMapping("/confirm/{id}")
    public ResponseEntity<?> confirmarContacto(@PathVariable("id") Long id) {
        return contactoService.confirmarContacto(id);
    }

    /**
     * Baja temprana de contacto
     *
     * @param id
     * @return
     */
    @GetMapping("/baja-temprana/{id}")
    public ResponseEntity<?> setBajaTempranaContacto(@PathVariable("id") Long id) {
        return contactoService.setEstadoBajaTemprana(id);
    }

    /**
     * Baja definitiva de contacto
     *
     * @param id
     * @return
     */
    @GetMapping("/baja/{id}")
    public ResponseEntity<?> bajaContacto(@PathVariable("id") Long id) {
        return contactoService.setEstadoBaja(id);
    }

    /**
     * Archivar Contacto
     *
     * @param id
     * @return
     */
    @GetMapping("/archivar/{id}")
    public ResponseEntity<?> archivarContacto(@PathVariable("id") Long id) {
        return contactoService.setEstadoArchivado(id);
    }

    /**
     * Editar contacto
     *
     * @param contacto
     * @return
     */
    @PutMapping("/edit")
    public ResponseEntity<?> editContacto(@RequestBody Contacto contacto) {
        return contactoService.editContacto(contacto);
    }

    /**
     * Obtener el estado del MSVC
     *
     * @return
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Contacto Base Service is up and running");
    }
    
    @GetMapping("/estados")
     public ResponseEntity<?> listarEstados() {
        return contactoService.getAllEstados();
    }

}
