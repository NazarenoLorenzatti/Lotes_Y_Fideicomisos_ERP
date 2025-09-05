package com.ar.compras.services;

import com.ar.compras.entities.DetallesFacturas;
import com.ar.compras.entities.FacturaProveedor;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface iFacturaProveedorService {

    public ResponseEntity<?> saveInvoice(FacturaProveedor factura, Long idContacto);

    public ResponseEntity<?> editInvoice(FacturaProveedor factura);

    public ResponseEntity<?> deleteInvoice(Long id);

    public ResponseEntity<?> setStatusInvoice(Long id, FacturaProveedor.Estado estado);

    public ResponseEntity<?> getInvoice(Long id);

    public ResponseEntity<?> addDetalles(List<DetallesFacturas> detalles, Long idFactura);

    public ResponseEntity<?> getAllInvoices();
}
