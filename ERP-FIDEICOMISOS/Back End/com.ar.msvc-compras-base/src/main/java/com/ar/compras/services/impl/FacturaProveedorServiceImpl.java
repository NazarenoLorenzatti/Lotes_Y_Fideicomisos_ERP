package com.ar.compras.services.impl;

import com.ar.compras.ClientsFeign.ClientFeignContactos;
import com.ar.compras.DTOs.*;
import com.ar.compras.entities.Articulo;
import com.ar.compras.entities.DetallesFacturas;
import com.ar.compras.entities.FacturaProveedor;
import com.ar.compras.repositories.iArticuloDao;
import com.ar.compras.repositories.iFacturaProveedorDao;
import com.ar.compras.responses.BuildResponsesServicesImpl;
import com.ar.compras.services.iFacturaProveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FacturaProveedorServiceImpl extends BuildResponsesServicesImpl implements iFacturaProveedorService {

    @Autowired
    private iFacturaProveedorDao facturaDao;

    @Autowired
    private ClientFeignContactos clienteFeign;

    @Autowired
    private iArticuloDao articuloDao;

    @Autowired
    private KafkaEventProducer kafka;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";
    private static final Long CC_PROVEEDORES_ID = 25L;
    private static final String CC_PROVEEDORES_NRO = "2.100.000";
    private static final String CC_PROVEEDORES_NOMBRE = "Proveedores";

    private static final Long CC_PROVEEDORES_AUX_ID = 26L;
    private static final String CC_PROVEEDORES_AUX_NRO = "92.100.000";
    private static final String CC_PROVEEDORES_AUX_NOMBRE = "Proveedores Aux";

    private static final Long CC_IVA_COMPRAS_ID = 28L;
    private static final String CC_IVA_COMPRAS_NRO = "1.101.000";
    private static final String CC_IVA_COMPRAS_NOMBRE = "Iva Compras";

    @Override
    public ResponseEntity<?> saveInvoice(FacturaProveedor factura, Long idContacto) {
        try {
            if (factura == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }
            if (facturaDao.existsByNumeroFactura(factura.getNumeroFactura())) {
                return this.buildResponse("nOK", CODIGO_NOK, "La factura que intenta cargar ya se encuentra registrada", factura, HttpStatus.BAD_REQUEST);
            }
            ContactosDTO contacto = getContacto(idContacto);
            if (contacto == null || !this.checkStatusMsvcContacto()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo Cargar el Comprobante,  no se encuentra el proveedor o no se puede acceder al servicio", contacto, HttpStatus.BAD_REQUEST);
            }

            if (!contacto.getIsProveedor()) {
                return this.buildResponse("nOK", CODIGO_NOK, "El contacto Enviado no esta catalogado como Proveedor", contacto, HttpStatus.BAD_REQUEST);
            }

            if (!this.checkAmounts(factura)) {
                return this.buildResponse("nOK", CODIGO_NOK, "El monto total de la suma de los detalles de la factura debe ser el mismo que el de la factura", factura, HttpStatus.BAD_REQUEST);
            }
            factura.setSaldoPendiente(factura.getImporteTotal());
            this.setContactInfo(factura, contacto);
            factura = this.setArticulos(factura);
            factura.setUuid("UUID-PROV-" + factura.getNumeroFactura());
            factura.setFechaDeCarga(LocalDateTime.now());
            factura.setEstado(FacturaProveedor.Estado.BORRADOR);
            factura = facturaDao.save(factura);
            if (factura == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo guardar la factura del proveedor", factura, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Factura Guardada", factura, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar una nueva Factura de Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> editInvoice(FacturaProveedor factura) {
        try {
            if (factura == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }
            Optional<FacturaProveedor> optional = facturaDao.findById(factura.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la factura que intenta editar", factura, HttpStatus.BAD_REQUEST);
            }

            if (!optional.get().getEstado().equals(FacturaProveedor.Estado.BORRADOR)) {
                return this.buildResponse("nOK", CODIGO_NOK, "La factura no se puede editar por q no se encuentra en borrador", optional.get(), HttpStatus.BAD_REQUEST);
            }
            factura = this.builEditFactura(optional, factura);
            factura = facturaDao.save(factura);
            if (factura == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo guardar la factura del proveedor", factura, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Factura Guardada", factura, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar editar una Factura de Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> addDetalles(List<DetallesFacturas> detalles, Long idFactura) {
        try {
            if (detalles == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }
            Optional<FacturaProveedor> optional = facturaDao.findById(idFactura);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la factura que intenta editar", optional.get(), HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(FacturaProveedor.Estado.BORRADOR)) {
                return this.buildResponse("nOK", CODIGO_NOK, "La factura no se puede editar por q no se encuentra en borrador", optional.get(), HttpStatus.BAD_REQUEST);
            }
            this.addDetalleInvoice(optional, detalles);
            facturaDao.save(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Factura Guardada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar editar una Factura de Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> deleteInvoice(Long id) {
        try {
            Optional<FacturaProveedor> optional = facturaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la factura que intenta eliminar", null, HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(FacturaProveedor.Estado.BORRADOR)) {
                return this.buildResponse("nOK", CODIGO_NOK, "La factura no se puede eliminar por q no se encuentra en borrador", optional.get(), HttpStatus.BAD_REQUEST);
            }
            facturaDao.deleteById(id);
            return this.buildResponse("OK", CODIGO_OK, "Factura eliminada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar eliminar una  Factura de Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> setStatusInvoice(Long id, FacturaProveedor.Estado estado) {
        try {
            Optional<FacturaProveedor> optional = facturaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la factura que intenta editar", null, HttpStatus.BAD_REQUEST);
            }

            if (optional.get().getEstado().equals(FacturaProveedor.Estado.RECHAZADA)
                    || optional.get().getEstado().equals(FacturaProveedor.Estado.PAGADA)
                    || optional.get().getEstado().equals(FacturaProveedor.Estado.PAGO_PARCIAL)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede cambiar el estado de Una factura rechazada, pagada o en proceso de pago", optional.get(), HttpStatus.BAD_REQUEST);
            }

            FacturaProveedor factura = optional.get();
            factura.setEstado(estado);
            if (estado.equals(FacturaProveedor.Estado.RECHAZADA)) {
                factura.setNumeroFactura("Rechazada-" + factura.getNumeroFactura());
            }
            factura = facturaDao.save(factura);
            if (estado.equals(FacturaProveedor.Estado.PENDIENTE)) {
                this.publicarEventoFacturaConfirmada(factura);
            }
            return this.buildResponse("OK", CODIGO_OK, "Factura Guardada", factura, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar editar una Factura de Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> getInvoice(Long id) {
        try {
            Optional<FacturaProveedor> optional = facturaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la factura", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Factura encontrada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener una  Factura de Proveedor");
        }
    }

    @Override
    public ResponseEntity<?> getAllInvoices() {
        try {
            List<FacturaProveedor> list = facturaDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la lista", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "lista de Facturas encontrada", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener la lista de Facturas de Proveedor");
        }
    }

    /* Obtener Contacto de Modulo de Contacto y Parsealdo a ContactoDTO */
    private ContactosDTO getContacto(Long id) {
        try {
            ResponseEntity<?> response = clienteFeign.getClienteById(id);
            if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
                return null;
            }

            Map<String, Object> body = (Map<String, Object>) response.getBody();
            List<Map<String, Object>> metadataList = (List<Map<String, Object>>) body.get("metadata");
            if (metadataList == null || metadataList.isEmpty()) {
                return null;
            }

            String codigo = (String) metadataList.get(0).get("codigo");
            if (!CODIGO_OK.equals(codigo)) {
                return null;
            }

            Map<String, Object> bodyResponse = (Map<String, Object>) body.get("response");
            if (bodyResponse == null || bodyResponse.isEmpty()) {
                return null;
            }

            for (Map.Entry<String, Object> entry : bodyResponse.entrySet()) {
                List<Map<String, Object>> responseList = (List<Map<String, Object>>) entry.getValue();
                if (responseList != null && !responseList.isEmpty()) {
                    Map<String, Object> contactoMap = responseList.get(0);
                    ObjectMapper mapper = new ObjectMapper();
                    ContactosDTO dto = mapper.convertValue(contactoMap, ContactosDTO.class);
                    return dto;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }

    }

    private boolean checkStatusMsvcContacto() {
        try {
            ResponseEntity<String> statusContactoMsvc = clienteFeign.getStatusMsvc();
            return statusContactoMsvc.getStatusCodeValue() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private FacturaProveedor builEditFactura(Optional<FacturaProveedor> o, FacturaProveedor factura) {
        FacturaProveedor edit = o.get();
        edit.setDescripcion(factura.getDescripcion() == null ? edit.getDescripcion() : factura.getDescripcion());
        edit.setFechaVencimiento(factura.getFechaVencimiento() == null ? edit.getFechaVencimiento() : factura.getFechaVencimiento());
        edit.setFechaEmision(factura.getFechaEmision() == null ? edit.getFechaEmision() : factura.getFechaEmision());
        edit.setImporteTotal(factura.getImporteTotal() == null ? edit.getImporteTotal() : factura.getImporteTotal());
        edit.setNumeroFactura(factura.getNumeroFactura() == null ? edit.getNumeroFactura() : factura.getNumeroFactura());
        edit.setObra(factura.getObra() == null ? edit.getObra() : factura.getObra());
        edit.setTipoFactura(factura.getTipoFactura() == null ? edit.getTipoFactura() : factura.getTipoFactura());
        return edit;
    }

    private void addDetalleInvoice(Optional<FacturaProveedor> optional, List<DetallesFacturas> detalles) {
        for (DetallesFacturas det : detalles) {
            det.setMontoNetoDetalle(det.getCantidad() * det.getPrecioUnitario());
            det.setMontoIvaDetalle(det.getMontoNetoDetalle() * det.getIva());
            det.setMontoTotalDetalle(det.getMontoNetoDetalle() + det.getMontoIvaDetalle());
            optional.get().getDetalles().add(det);
        }
    }

    private boolean checkAmounts(FacturaProveedor factura) {
        if (factura.getDetalles() == null) {
            return false;
        }
        double ammount = 0.0;
        for (DetallesFacturas det : factura.getDetalles()) {
            det.setMontoNetoDetalle(det.getPrecioUnitario() * det.getCantidad());
            det.setMontoIvaDetalle(det.getPrecioUnitario() * det.getIva());
            det.setMontoTotalDetalle(det.getMontoNetoDetalle() + det.getMontoIvaDetalle());
            det.setFactura(factura);
            log.info("VALORES unitario {} IVA {} Total {}", det.getPrecioUnitario(), det.getIva(), det.getMontoTotalDetalle());
            ammount += det.getMontoTotalDetalle();
        }
        return ammount == factura.getImporteTotal();
    }

    private FacturaProveedor setArticulos(FacturaProveedor factura) {
        for (DetallesFacturas det : factura.getDetalles()) {
            Optional<Articulo> o = articuloDao.findById(det.getArticulo().getId());
            if (o.isPresent()) {
                det.setArticulo(o.get());
            }
        }
        return factura;
    }

    private void publicarEventoFacturaConfirmada(FacturaProveedor factura) {
        FacturaProveedorEvent event = new FacturaProveedorEvent();
        List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables = new ArrayList();
        this.setAplicacionesCuentasContables(aplicacionesCuentasContables, factura);
        event.setAplicacionesCuentasContables(aplicacionesCuentasContables);
        event.setCuitProveedor(factura.getCuitProveedor());
        event.setDescripcion(factura.getDescripcion());
        event.setEstado(factura.getEstado().name());
        event.setFechaDeCarga(factura.getFechaDeCarga());
        event.setFechaEmision(factura.getFechaEmision());
        event.setFechaVencimiento(factura.getFechaVencimiento());
        event.setId(factura.getId());
        event.setImporteTotal(factura.getImporteTotal());
        event.setImporteIva(factura.getImporteIva());
        event.setImporteNeto(factura.getImporteNeto());
        
        event.setNombreProveedor(factura.getNombreProveedor());
        event.setNroProveedor(factura.getNroProveedor());
        event.setNumeroFactura(factura.getNumeroFactura());
        event.setOficial(factura.getOficial());
        event.setTipoFactura(factura.getTipoFactura().name());
        event.setUuidFactura(factura.getUuid());
        kafka.publicarEventoFactura(event);
    }

    private void setAplicacionesCuentasContables(List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables, FacturaProveedor factura) {
        AplicacionesCuentasContablesDTO applyProvedores = new AplicacionesCuentasContablesDTO();
        applyProvedores.setIdCuenta(factura.getOficial() ? CC_PROVEEDORES_ID : CC_PROVEEDORES_AUX_ID); //ARTICULOS ASOCIADOS
        applyProvedores.setNroCuenta(factura.getOficial() ? CC_PROVEEDORES_NRO : CC_PROVEEDORES_AUX_NRO);
        applyProvedores.setNombreCuenta(factura.getOficial() ? CC_PROVEEDORES_NOMBRE : CC_PROVEEDORES_AUX_NOMBRE);
        applyProvedores.setImporteAplicado(factura.getImporteTotal());
        applyProvedores.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.HABER);
        aplicacionesCuentasContables.add(applyProvedores);

        if (factura.getOficial()) {
            AplicacionesCuentasContablesDTO applyIvaCompras = new AplicacionesCuentasContablesDTO();
            applyIvaCompras.setIdCuenta(CC_IVA_COMPRAS_ID); //ARTICULOS ASOCIADOS
            applyIvaCompras.setNroCuenta(CC_IVA_COMPRAS_NRO);
            applyIvaCompras.setNombreCuenta(CC_IVA_COMPRAS_NOMBRE);
            applyIvaCompras.setImporteAplicado(factura.getImporteIva());
            applyIvaCompras.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.DEBE);
            aplicacionesCuentasContables.add(applyIvaCompras);
        }

        for (DetallesFacturas det : factura.getDetalles()) {
            Articulo art = det.getArticulo();
            AplicacionesCuentasContablesDTO applyArt = new AplicacionesCuentasContablesDTO();
            applyArt.setIdCuenta(art.getIdCuentaContable()); //ARTICULOS ASOCIADOS
            applyArt.setNroCuenta(art.getNroCuentaContable());
            applyArt.setNombreCuenta(art.getNombreCuentaContable());
            applyArt.setImporteAplicado(det.getMontoNetoDetalle());
            applyArt.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.DEBE);
            aplicacionesCuentasContables.add(applyArt);
        }
    }

    private void setContactInfo(FacturaProveedor factura, ContactosDTO contacto) {
        factura.setCuitProveedor(contacto.getNumeroDocumento());
        factura.setNombreProveedor(contacto.getRazonSocial());
        factura.setNroProveedor(contacto.getId());
        factura.setRegimenAfip(contacto.getCondicionIva());
    }

}
