package com.ar.compras.services.impl;

import com.ar.compras.DTOs.AplicacionesCuentasContablesDTO;
import com.ar.compras.DTOs.FacturaProveedorEvent;
import com.ar.compras.DTOs.MinutaDePagoEvent;
import com.ar.compras.entities.*;
import com.ar.compras.repositories.*;
import com.ar.compras.responses.BuildResponsesServicesImpl;
import com.ar.compras.services.iMinutaDePagoService;
import java.time.LocalDateTime;
import java.util.*;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MinutaDePagoServiceImpl extends BuildResponsesServicesImpl implements iMinutaDePagoService {

    @Autowired
    private iCajaPagoDao cajaDao;

    @Autowired
    private iOrdenDePagoDao ordenDao;

    @Autowired
    private iMinutaDePagoDao minutaDao;

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

    @Override
    @Transactional
    public ResponseEntity<?> buildMinutaDePago(MinutaDePago minuta) {
        try {
            if (minuta == null || minuta.getOrden() == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar, o no se asocio ninguna orden de pago", null, HttpStatus.BAD_REQUEST);
            }
//            if (minuta.getImporte() <= 0) {
//                return this.buildResponse("nOK", CODIGO_NOK, "El monto de la minuta de pago debe ser mayor a 0", null, HttpStatus.BAD_REQUEST);
//            }
            if (!this.setCajaAndOrden(minuta)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se guardo la minuta, falta asociar Caja y Orden de pago", null, HttpStatus.BAD_REQUEST);
            }
            minuta.setImporte(minuta.getOrden().getImporte());
            minuta.setFechaCreacion(LocalDateTime.now());
            minuta.setEstado(MinutaDePago.Estado.BORRADOR);
            minuta = minutaDao.save(minuta);
            return this.buildResponse("OK", CODIGO_OK, "Se guardo la Minuta de pago", minuta, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar una nueva Minuta de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> editMinutaDePago(MinutaDePago minuta) {
        try {
            if (minuta == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar, o no se asocio ninguna orden de pago", null, HttpStatus.BAD_REQUEST);
            }
            Optional<MinutaDePago> optional = minutaDao.findById(minuta.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la minuta de pago", null, HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(MinutaDePago.Estado.BORRADOR)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No Puede editar una minuta que no este en borrador", null, HttpStatus.BAD_REQUEST);
            }
            minuta = this.buildEditMinutaDePago(optional, minuta);
            minuta = minutaDao.save(minuta);
            return this.buildResponse("OK", CODIGO_OK, "Se guardo la Minuta de pago", minuta, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar editar la Minuta de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteMinutaDePago(Long id) {
        try {
            Optional<MinutaDePago> optional = minutaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la minuta de pago", null, HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(MinutaDePago.Estado.BORRADOR)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No Puede eliminar una minuta que no este en borrador", null, HttpStatus.BAD_REQUEST);
            }
            minutaDao.delete(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Se elimino la Minuta de pago", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar eliminar la Minuta de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> getMinutaDePago(Long id) {
        try {
            Optional<MinutaDePago> optional = minutaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la minuta de pago", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Se encontro la Minuta de pago", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener la Minuta de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> getAllMinutaDePago() {
        try {
            List<MinutaDePago> list = minutaDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la lista de minutas de pago", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Se encontro la lista de Minutas de pago", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener la lista de Minutas de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> confirmMinutaDePago(Long id) {
        try {
            Optional<MinutaDePago> optional = minutaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la minuta de pago", null, HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(MinutaDePago.Estado.BORRADOR)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No Puede Confirmar una minuta que no este en BORRADOR", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setEstado(MinutaDePago.Estado.PENDIENTE);
            optional.get().setFechaEfectiva(LocalDateTime.now());
            optional.get().setNumero("MIN-P-" + String.format("%09d", optional.get().getId()));
            optional.get().setUuid("UUID-PROV-" + optional.get().getNumero());

            if (this.checkInfo(optional)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede confirmar la minuta, "
                        + "Alguna caja no es correcta para el tipo de Orden de pago, "
                        + ", No coinciden los importes de los documentos "
                        + "o la orden de pago se encuentra paga o anulada", optional.get(), HttpStatus.BAD_REQUEST);
            }

            minutaDao.save(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Minuta de pago Confirmada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar confirmar la Minuta de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> declineMinutaDePago(Long id) {
        try {
            Optional<MinutaDePago> optional = minutaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la minuta de pago", null, HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(MinutaDePago.Estado.PENDIENTE) && !optional.get().getEstado().equals(MinutaDePago.Estado.PAGADA)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No Puede Declinar una minuta que no este en Pendiente", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setEstado(MinutaDePago.Estado.RECHAZADO);
            optional.get().setFechaRechazo(LocalDateTime.now());
            minutaDao.save(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Minuta de Pago Rechazada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar rechazar la Minuta de pago a Proveedor");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> payMinutaDePago(Long id) {
        try {
            Optional<MinutaDePago> optional = minutaDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la minuta de pago", null, HttpStatus.BAD_REQUEST);
            }
            if (!optional.get().getEstado().equals(MinutaDePago.Estado.PENDIENTE)) {
                return this.buildResponse("nOK", CODIGO_NOK, "No Puede Pagar una minuta que no este Pendiente", null, HttpStatus.BAD_REQUEST);
            }
            this.setEstadosAsociados(optional);
            optional.get().setEstado(MinutaDePago.Estado.PAGADA);
            optional.get().setFechaPagada(LocalDateTime.now());
            minutaDao.save(optional.get());
            this.publicarEventoMinutaDePago(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Se Pago la Minuta de pago", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar obtener la Minuta de pago a Proveedor");
        }
    }

    private MinutaDePago buildEditMinutaDePago(Optional<MinutaDePago> optional, MinutaDePago minuta) {
        MinutaDePago edit = optional.get();
        edit.setMedioDePago(minuta.getMedioDePago() == null ? edit.getMedioDePago() : minuta.getMedioDePago());
        CajaPagos caja = null;
        if (minuta.getCaja() != null) {
            Optional<CajaPagos> o = cajaDao.findById(minuta.getCaja().getId());
            if (o.isPresent()) {
                caja = o.get();
            }
        }
        edit.setCaja(caja == null ? edit.getCaja() : caja);
        return edit;
    }

    private boolean checkInfo(Optional<MinutaDePago> optional) {
        if (!Objects.equals(optional.get().getOrden().getOficial(), optional.get().getCaja().getOficial())) {
            return false;
        }
        return !Objects.equals(optional.get().getOrden().getImporte(), optional.get().getImporte());
    }

    /**
     * COMPROBAR QUE SE APLIQUE DE LA MAS VIEJA A LA MAS NUEVA
     *
     */
    private void setEstadosAsociados(Optional<MinutaDePago> optional) {
        Optional<OrdenDePago> orden = ordenDao.findById(optional.get().getOrden().getId());
        orden.get().setEstado(OrdenDePago.Estado.PAGADA);
        Double montoPagado = optional.get().getImporte();
        for (FacturaOrdenDePago factApply : orden.get().getFacturasAplicadas()) {
            FacturaProveedor fact = factApply.getFactura();
            montoPagado -= fact.getImporteTotal();
            if (montoPagado >= 0) {
                fact.setEstado(FacturaProveedor.Estado.PAGADA);
                fact.setImporteAbonado(fact.getImporteTotal());
                fact.setSaldoPendiente(0.0);
//                this.publicarEventoFacturaPagada(fact);
            }
            fact.setEstado(FacturaProveedor.Estado.PAGO_PARCIAL);
            fact.setSaldoPendiente(fact.getImporteTotal() + montoPagado);
            fact.setImporteAbonado(montoPagado);
        }
    }

    private void publicarEventoMinutaDePago(MinutaDePago minuta) {
        Map<String, String> facturasPagadas = new HashMap<>();
        List<AplicacionesCuentasContablesDTO> aplicaciones = new ArrayList();
        MinutaDePagoEvent event = new MinutaDePagoEvent();
        event.setCajaCuentaContable(minuta.getCaja().getNroCuentaContable());
        event.setCajaId(minuta.getCaja().getId());
        event.setCajaNombre(minuta.getCaja().getNombre_caja());
        event.setFechaEfectiva(minuta.getFechaPagada());
        event.setId(minuta.getId());
        event.setImporte(minuta.getImporte());
        event.setMedioDePago(minuta.getMedioDePago().name());
        event.setNumero(minuta.getNumero());
        event.setUuidMinuta(minuta.getUuid());
        event.setUuidOrdenPagoAsociada(minuta.getOrden().getUuid());
        event.setNroProveedor(minuta.getOrden().getFacturasAplicadas().get(0).getFactura().getNroProveedor());
        event.setOficial(minuta.getOrden().getOficial());
        for (FacturaOrdenDePago factApply : minuta.getOrden().getFacturasAplicadas()) {
            FacturaProveedor fact = factApply.getFactura();
            facturasPagadas.put(fact.getNroProveedor().toString(), fact.getUuid());
        }
        this.setAplicacionesCuentasContables(aplicaciones, minuta);
        event.setAplicacionesCuentasContables(aplicaciones);
        kafka.publicarEventoMinutas(event);
    }

    private boolean setCajaAndOrden(MinutaDePago minuta) {
        if (minuta.getCaja() == null || minuta.getOrden() == null) {
            return false;
        }
        Optional<CajaPagos> cajaPago = cajaDao.findById(minuta.getCaja().getId());
        Optional<OrdenDePago> ordenAsociada = ordenDao.findById(minuta.getOrden().getId());

        if (cajaPago.isEmpty() || ordenAsociada.isEmpty()) {
            return false;
        }

        minuta.setCaja(cajaPago.get());
        minuta.setOrden(ordenAsociada.get());
        return true;

    }

    private void setAplicacionesCuentasContables(List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables, MinutaDePago minuta) {
        AplicacionesCuentasContablesDTO apply = new AplicacionesCuentasContablesDTO();
        apply.setIdCuenta(minuta.getOrden().getOficial() ? CC_PROVEEDORES_ID : CC_PROVEEDORES_AUX_ID); //ARTICULOS ASOCIADOS
        apply.setNroCuenta(minuta.getOrden().getOficial() ? CC_PROVEEDORES_NRO : CC_PROVEEDORES_AUX_NRO);
        apply.setNombreCuenta(minuta.getOrden().getOficial() ? CC_PROVEEDORES_NOMBRE : CC_PROVEEDORES_AUX_NOMBRE);
        apply.setImporteAplicado(minuta.getImporte());
        apply.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.DEBE);
        aplicacionesCuentasContables.add(apply);

        AplicacionesCuentasContablesDTO applyCaja = new AplicacionesCuentasContablesDTO();
        applyCaja.setIdCuenta(minuta.getCaja().getIdCuentaContable());
        applyCaja.setNombreCuenta(minuta.getCaja().getNombreCuentaContable());
        apply.setImporteAplicado(minuta.getImporte());
        apply.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.HABER);
    }

}
