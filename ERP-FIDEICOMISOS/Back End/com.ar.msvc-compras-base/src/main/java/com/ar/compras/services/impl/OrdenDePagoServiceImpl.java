package com.ar.compras.services.impl;

import com.ar.compras.entities.*;
import com.ar.compras.repositories.*;
import com.ar.compras.responses.BuildResponsesServicesImpl;
import com.ar.compras.services.iOrdenDePagoService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdenDePagoServiceImpl extends BuildResponsesServicesImpl implements iOrdenDePagoService {

    @Autowired
    private iFacturaProveedorDao facturaDao;
    
    @Autowired
    private iFacturaOrdenDePago applyDao;

    @Autowired
    private iOrdenDePagoDao ordenDao;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    public ResponseEntity<?> buildOrdenDePago(OrdenDePago orden) {
        try {
            if (orden == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }
            int facturasEnviadas = orden.getFacturasAplicadas().size();
            List<FacturaOrdenDePago> facturasAplicadas = new ArrayList();
            this.findFacturasProveedor(orden, facturasAplicadas);
            orden.setEstado(OrdenDePago.Estado.PENDIENTE);
            orden.setFecha(LocalDateTime.now());
            orden = this.ordenDao.save(orden);
            this.setUuiidAplicacion(orden);
            return this.buildResponse("OK", CODIGO_OK, "Se guardo la Orden de pago"
                    + ", se enviaron a pagar: " + facturasEnviadas + " Facturas, Se agregaron: " + orden.getFacturasAplicadas().size()
                    + " si encuentra diferencia revise que los comprobantes sean correspondientes en su tipo", orden, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar una nueva Orden de pago a Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> editOrdenDePago(OrdenDePago orden) {
        try {
            if (orden == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }

            if (!orden.getEstado().equals(OrdenDePago.Estado.PENDIENTE)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede editar una orden de pago que no se encuentre Pendiente de aprobacion", null, HttpStatus.BAD_REQUEST);
            }

            Optional<OrdenDePago> optional = ordenDao.findById(orden.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encuentra la orden  para editar", null, HttpStatus.BAD_REQUEST);
            }

            orden = this.buildEditOrden(optional, orden);
            orden = ordenDao.save(orden);
            return this.buildResponse("OK", CODIGO_OK, "Se guardo la Orden de pago", orden, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar editar una nueva Orden de pago a Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> setEstadoOrdenDePago(Long id, OrdenDePago.Estado estado) {
        try {
            Optional<OrdenDePago> optional = ordenDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encuentra la orden", null, HttpStatus.BAD_REQUEST);
            }
            if (optional.get().getEstado().equals(OrdenDePago.Estado.RECHAZADA) || optional.get().getEstado().equals(OrdenDePago.Estado.PAGADA)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede cambiar el estado a una orden Pagada o Rechazada", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setEstado(estado);
            if (estado.equals(OrdenDePago.Estado.APROBADA)) {
                optional.get().setNumero("OP-" + String.format("%09d", optional.get().getId()));
                optional.get().setUuid("UUID-PROV-" + optional.get().getNumero());
            }
            ordenDao.save(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Se guardo la Orden de pago", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar cambiar de estado una Orden de pago a Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> getOrdenDePago(Long id) {
        try {
            Optional<OrdenDePago> optional = ordenDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encuentra la orden", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Se Encontro la Orden de pago", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener una  Orden de pago a Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> getAllOrdenDePago() {
        try {
            List<OrdenDePago> list = ordenDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encuentra la lista de ordenes", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Se Encontro la lista de Ordenes de pago", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener una  Orden de pago a Proveedor");
        }
    }

    private void findFacturasProveedor(OrdenDePago orden, List<FacturaOrdenDePago> list) {
        for (FacturaOrdenDePago apply : orden.getFacturasAplicadas()) {
            Optional<FacturaProveedor> o = facturaDao.findById(apply.getFactura().getId());
            if (o.isEmpty()) {
                return;
            }
            if (o.get().getOficial().equals(orden.getOficial())) {
                FacturaOrdenDePago applyFact = new FacturaOrdenDePago();
                applyFact.setFactura(o.get());
                applyFact.setOrden(orden);
                applyFact.setUuidFactura(o.get().getUuid());
                list.add(applyFact);
            }
            
        }
        orden.setFacturasAplicadas(list);
    }

    private OrdenDePago buildEditOrden(Optional<OrdenDePago> optional, OrdenDePago orden) {
        OrdenDePago edit = optional.get();
        edit.setFecha(orden.getFecha() == null ? edit.getFecha() : orden.getFecha());
        edit.setImporte(orden.getImporte() == null ? edit.getImporte() : orden.getImporte());
        edit.setObservaciones(orden.getObservaciones() == null ? edit.getObservaciones() : orden.getObservaciones());
        List<FacturaOrdenDePago> facturasAplicadas = new ArrayList();
        this.findFacturasProveedor(edit, facturasAplicadas);
        return edit;
    }

    private void setUuiidAplicacion(OrdenDePago orden) {
        orden.setUuid("OP-UUID-"+String.format("%08d",orden.getId()));
        for (FacturaOrdenDePago apply : orden.getFacturasAplicadas()) {
            apply.setUuidOrden(orden.getUuid());
            applyDao.save(apply);
        }
    }

}
