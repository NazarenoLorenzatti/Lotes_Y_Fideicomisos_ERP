package com.ar.compras.controllers;

import com.ar.compras.entities.DetallesFacturas;
import com.ar.compras.entities.FacturaProveedor;
import com.ar.compras.services.iFacturaProveedorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("compras/facturas")
public class FacturaProveedorController {

    @Autowired
    private iFacturaProveedorService facturaService;

    /**
     * Guardar Factura de proveedor Nueva
     * @param factura
     * @param idContacto
     * @return 
     */
    @PostMapping("/guardar/{idContacto}")
    public ResponseEntity<?> saveInvoice(@RequestBody FacturaProveedor factura, @PathVariable Long idContacto) {
        return facturaService.saveInvoice(factura, idContacto);
    }

    /** 
     * Editar una Factura de Proveedor
     * @param factura
     * @return 
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editInvoice(@RequestBody FacturaProveedor factura) {
        return facturaService.editInvoice(factura);
    }

    /**
     * Eliminar una factura en Borrador
     * @param id
     * @return 
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        return facturaService.deleteInvoice(id);
    }

    /** 
     * Cambiar el estado de una Factura 
     * @param id
     * @param estado
     * @return 
     */
    @PostMapping("/cambiar-estado")
    public ResponseEntity<?> setStatusInvoice(@RequestParam Long id, @RequestParam FacturaProveedor.Estado estado) {
        return facturaService.setStatusInvoice(id, estado);
    }

    /** 
     * Obtener una Factura
     * @param id
     * @return 
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        return facturaService.getInvoice(id);
    }

    /**
     * Agregar nuevos detalles a una factura en Borrador
     * @param factura
     * @return 
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> addDetalles(@RequestBody FacturaProveedor factura){
        return facturaService.addDetalles(factura.getDetalles(), factura.getId());
    }
    
    /**
     * Listar todas las facturas
     * @return 
     */
    @GetMapping("/listar")
    public ResponseEntity<?> getAllInvoices() {
        return facturaService.getAllInvoices();
    }
}
