package com.ar.base.services.impl;

import com.ar.base.DTOs.AplicacionesCuentasContablesDTO;
import com.ar.base.DTOs.ComprobanteConfirmadoEvent;
import com.ar.base.DTOs.FacturaProveedorEvent;
import com.ar.base.DTOs.MinutaDePagoEvent;
import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.CuentaContable;
import com.ar.base.entities.MovimientoContable;
import com.ar.base.entities.TiposDeEntidades;
import com.ar.base.repositories.iAsientoContableDao;
import com.ar.base.repositories.iCuentaContableDao;
import com.ar.base.repositories.iTiposDeEntidadesDao;
import com.ar.base.services.iAsientoContableService;
import com.ar.base.services.iEventListenerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventListener implements iEventListenerService {

    @Autowired
    private iAsientoContableService asientosService;

    @Autowired
    private iAsientoContableDao asientoDao;

    @Autowired
    private iCuentaContableDao cuentaContableDao;

    @Autowired
    private iTiposDeEntidadesDao tiposDao;

//    private final ObjectMapper objectMapper;
//    public KafkaEventListener(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
    @KafkaListener(topics = "${topic.comprobantes.confirmados}", containerFactory = "comprobanteRecibidoFactory")
    public void recibirEventoDesdeFacturacion(ComprobanteConfirmadoEvent comprobanteRecibido) {
        log.info("Comprobante recibido: {}", comprobanteRecibido);
        this.procesarFacturasYRecibosClientes(comprobanteRecibido);
    }

    @KafkaListener(topics = "${topic.recibos.confirmados}", containerFactory = "reciboRecibidoFactory")
    public void recibirEventoDesdeCobranza(ComprobanteConfirmadoEvent comprobanteRecibido) {
        log.info("Comprobante recibido: {}", comprobanteRecibido);
        this.procesarFacturasYRecibosClientes(comprobanteRecibido);
    }

    @KafkaListener(topics = "${topic.compras.factura-confirmada}", containerFactory = "facturaProveedorFactory")
    public void listenFacturaProveedor(FacturaProveedorEvent event) {
        System.out.println("Factura Proveedor recibida: " + event.getId());
        this.procesarFacturasProveedor(event);
    }

    @KafkaListener(topics = "${topic.compras.minuta-confirmada}", containerFactory = "minutaDePagoFactory")
    public void recibirMinutaDePago(MinutaDePagoEvent minutaRecibida) {
        log.info("Comprobante recibido: {}", minutaRecibida);
        this.procesarMinutaDePago(minutaRecibida);
    }

    private void procesarFacturasYRecibosClientes(ComprobanteConfirmadoEvent event) {
        AsientoContable asiento = new AsientoContable();
        asiento.setContactoId(event.getContactoId());
        asiento.setComprobanteId(event.getIdComprobante());
        asiento.setTipoAsiento(AsientoContable.TipoAsiento.ASIENTO_AUTOMATICO);
        asiento.setEstado(AsientoContable.Estado.APROBADO);
        asiento.setReferenciaExterna("UUID-" + event.getNroComprobante());
        asiento.setOficial(event.isOficial());
        Optional<TiposDeEntidades> tipoEntidad = tiposDao.findById(event.getIdEntidad());
        asiento.setTipoEntidad(tipoEntidad.get());
        this.buildMovimientos(event.getAplicacionesCuentasContables(), asiento);
        Optional<AsientoContable> asientoAsociado = null;
        if (event.getUUIDComprobanteAsociado() != null) {
            asientoAsociado = asientoDao.findByReferenciaExterna(event.getUUIDComprobanteAsociado());
            if (asientoAsociado.isPresent()) {
                asiento.setAsientoOrigen(asientoAsociado.get());
                asiento.setOriginada_por(asientoAsociado.get().getReferenciaExterna());
            }
        }
        switch (event.getTipoComprobante()) {
            case "FA", "FB", "FC", "FM" -> {
                asiento.setDescripcion(event.isOficial() ? "Factura Cliente" : "Factura cliente Aux");
                asiento.setTipoOperacion(AsientoContable.TipoOperacion.FACTURA_CLIENTE);
            }
            case "DA", "DB", "DC", "DM" -> {
                asiento.setDescripcion(event.isOficial() ? "Nota de Debito" : "Nota de Debito Aux");
                asiento.setTipoOperacion(AsientoContable.TipoOperacion.NOTA_DEBITO);
            }

            case "RA", "RB", "RC", "RM" -> {
                asiento.setDescripcion(event.isOficial() ? "Recibo" : "Recibo Aux");
                asiento.setTipoOperacion(AsientoContable.TipoOperacion.RECIBO);
            }
            case "CRA", "CRB", "CRC", "CRM" -> {
                asiento.setDescripcion(event.isOficial() ? "Contra Recibo" : "Contra Recibo Aux");
                asiento.setTipoOperacion(AsientoContable.TipoOperacion.CONTRA_RECIBO);
            }
            case "CA", "CB", "CC", "CM" -> {
                asiento.setDescripcion(event.isOficial() ? "Nota de Credito" : "Nota de Credito Aux");
                asiento.setTipoOperacion(AsientoContable.TipoOperacion.NOTA_CREDITO);
                asiento.setReferenciaExterna("REV-" + asiento.getReferenciaExterna());
            }

        }
        if (asiento.getAsientoOrigen() == null) {
            this.asientosService.registrarAsiento(asiento);
        } else {
            this.asientosService.registrarAsientoReversion(asientoAsociado.get().getId(), asiento);
        }
    }

    private void procesarFacturasProveedor(FacturaProveedorEvent event) {
        Optional<AsientoContable> o = asientoDao.findByReferenciaExterna(event.getUuidFactura());
        if (o.isPresent() && event.getEstado().equals("RECHAZADA")) {
            this.asientosService.registrarAsientoReversion(o.get().getId(), o.get());
            return;
        }

        AsientoContable asiento = new AsientoContable();
        List<MovimientoContable> list = new ArrayList();
        log.info("FACTURA PROVEEDOR recibida: {}", event);
        asiento.setContactoId(event.getNroProveedor());
        asiento.setComprobanteId(event.getId());
        asiento.setTipoAsiento(AsientoContable.TipoAsiento.ASIENTO_AUTOMATICO);
        asiento.setEstado(AsientoContable.Estado.APROBADO);
        asiento.setReferenciaExterna(event.getUuidFactura());
        asiento.setOficial(event.getOficial());
        Optional<TiposDeEntidades> tipoEntidad = tiposDao.findById(3L);
        asiento.setTipoEntidad(tipoEntidad.get());
        this.buildMovimientos(event.getAplicacionesCuentasContables(), asiento);
        asiento.setDescripcion(event.getOficial() ? "Factura Proveedor" : "Factura Proveedor Aux");
        asiento.setTipoOperacion(AsientoContable.TipoOperacion.FACTURA_PROVEEDOR);
        this.asientosService.registrarAsiento(asiento);
    }

    private void procesarMinutaDePago(MinutaDePagoEvent minutaRecibida) {
        AsientoContable asiento = new AsientoContable();
        List<MovimientoContable> list = new ArrayList();
        log.info("MINUTA DE PAGO recibida: {}", minutaRecibida);
        asiento.setContactoId(minutaRecibida.getNroProveedor());
        asiento.setComprobanteId(minutaRecibida.getId());
        asiento.setTipoAsiento(AsientoContable.TipoAsiento.ASIENTO_AUTOMATICO);
        asiento.setEstado(AsientoContable.Estado.APROBADO);
        asiento.setReferenciaExterna(minutaRecibida.getUuidMinuta());
        Optional<TiposDeEntidades> tipoEntidad = tiposDao.findById(3L);
        asiento.setTipoEntidad(tipoEntidad.get());
        asiento.setDescripcion(minutaRecibida.getOficial() ? "Pago Proveedor" : "Pago Proveedor Aux");
        asiento.setTipoOperacion(AsientoContable.TipoOperacion.ORDEN_PAGO);
        this.buildMovimientos(minutaRecibida.getAplicacionesCuentasContables(), asiento);
    }

    private void buildMovimientos(List<AplicacionesCuentasContablesDTO> aplicaciones, AsientoContable asiento) {
        asiento.getMovimientos().clear();
        for (AplicacionesCuentasContablesDTO apply : aplicaciones) {
            MovimientoContable mov = new MovimientoContable();
            Optional<CuentaContable> cuentaContable = cuentaContableDao.findByCodigo(apply.getNroCuenta());
            mov.setCuenta(cuentaContable.get());
            mov.setEntidadId(asiento.getContactoId());
            mov.setFecha(asiento.getFecha());
            mov.setTipoEntidad(asiento.getTipoEntidad());
            mov.setDebe(apply.getTipoMov().equals(AplicacionesCuentasContablesDTO.TipoMov.DEBE) ? apply.getImporteAplicado() : 0.0);
            mov.setHaber(apply.getTipoMov().equals(AplicacionesCuentasContablesDTO.TipoMov.HABER) ? apply.getImporteAplicado() : 0.0);
            asiento.getMovimientos().add(mov);
        }
    }

}
