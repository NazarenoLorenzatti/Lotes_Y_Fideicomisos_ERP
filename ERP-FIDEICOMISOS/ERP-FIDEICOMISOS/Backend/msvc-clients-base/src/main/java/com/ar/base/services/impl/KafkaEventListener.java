package com.ar.base.services.impl;

import com.ar.base.DTOs.ComprobanteConfirmadoEvent;
import com.ar.base.DTOs.FacturaProveedorEvent;
import com.ar.base.DTOs.ImporteAplicadoEvent;
import com.ar.base.DTOs.MinutaDePagoEvent;
import com.ar.base.entities.MovimientoCuentaCorriente;
import com.ar.base.services.iKafkaEventListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventListener implements iKafkaEventListenerService {

    @Autowired
    private CuentaCorrienteServiceImpl ccService;

    @KafkaListener(topics = "${topic.comprobantes.confirmados}", containerFactory = "comprobanteRecibidoFactory")
    public void recibirEventoDesdeFacturacion(ComprobanteConfirmadoEvent comprobanteRecibido) {
        this.procesarEvento(comprobanteRecibido);
    }

    @KafkaListener(topics = "${topic.recibos.confirmados}", containerFactory = "reciboRecibidoFactory")
    public void recibirEventoDesdeCobranza(ComprobanteConfirmadoEvent comprobanteRecibido) {
        this.procesarEvento(comprobanteRecibido);
    }

    @KafkaListener(topics = "${topic.compras.factura-confirmada}", containerFactory = "facturaProveedorFactory")
    public void recibirFacturasProveedor(FacturaProveedorEvent facturaRecibida) {
        this.procesarFacturaProveedor(facturaRecibida);
    }

    @KafkaListener(topics = "${topic.compras.minuta-confirmada}", containerFactory = "minutaDePagoFactory")
    public void recibirFacturasProveedor(MinutaDePagoEvent minutaRecibida) {
        this.procesarMinutaDePago(minutaRecibida);
    }

    public void registrarMovimiento(Long contactoId, MovimientoCuentaCorriente movimiento) {
        ccService.registrarMovimiento(contactoId, movimiento);
    }

    private void procesarEvento(ComprobanteConfirmadoEvent comprobanteRecibido) {
        if (comprobanteRecibido != null) {
            MovimientoCuentaCorriente movimiento = new MovimientoCuentaCorriente();
            movimiento.setImporte(comprobanteRecibido.getImporteTotal());
            movimiento.setImporte_neto(comprobanteRecibido.getImporteNeto());
            movimiento.setImporte_iva(comprobanteRecibido.getImporteIva());
            movimiento.setImporte_gravado(comprobanteRecibido.getImporte_gravado());
            movimiento.setSaldoPendiente(comprobanteRecibido.getImporteTotal());
            movimiento.setComprobanteId(comprobanteRecibido.getIdComprobante());
            movimiento.setNroComprobante(comprobanteRecibido.getNroComprobante());
            movimiento.setDescripcion(comprobanteRecibido.getTipoComprobante() + " " + comprobanteRecibido.getEstado());
            movimiento.setOficial(comprobanteRecibido.isOficial());
            switch (comprobanteRecibido.getTipoComprobante()) {
                case "FA", "FB", "FC", "DA", "DB", "DC", "FM", "DM", "CRA", "CRB", "CRC", "CRM" ->
                    movimiento.setTipoMovimiento(MovimientoCuentaCorriente.TipoMovimiento.DEBITO);
                case "CA", "CB", "CC", "CM", "RA", "RB", "RC", "RM" -> {
                    movimiento.setTipoMovimiento(MovimientoCuentaCorriente.TipoMovimiento.CREDITO);
//                    movimiento.setSaldoPendiente(0.0);
                }
            }
            this.registrarMovimiento(comprobanteRecibido.getContactoId(), movimiento);
            log.info("Comprobante recibido: {}", comprobanteRecibido);
        } else {
            log.error("No se recibio ningun Comprobante desde el MSVC Facturacion: {}", comprobanteRecibido);
        }
    }

    private void procesarFacturaProveedor(FacturaProveedorEvent facturaRecibida) {
        if (facturaRecibida != null) {
            MovimientoCuentaCorriente movimiento = new MovimientoCuentaCorriente();
            movimiento.setImporte(facturaRecibida.getImporteTotal());
            movimiento.setImporte_neto(facturaRecibida.getImporteNeto());
            movimiento.setImporte_iva(facturaRecibida.getImporteIva());
            movimiento.setImporte_gravado(facturaRecibida.getImporteGravado());
            movimiento.setSaldoPendiente(facturaRecibida.getImporteTotal());
            movimiento.setComprobanteId(facturaRecibida.getId());
            movimiento.setNroComprobante(facturaRecibida.getNumeroFactura());
            movimiento.setDescripcion(facturaRecibida.getTipoFactura() + " " + facturaRecibida.getEstado());
            movimiento.setOficial(facturaRecibida.getOficial());
            movimiento.setTipoMovimiento(MovimientoCuentaCorriente.TipoMovimiento.CREDITO);
            this.registrarMovimiento(facturaRecibida.getNroProveedor(), movimiento);
            log.info("Comprobante recibido: {}", facturaRecibida);
        } else {
            log.error("No se recibio ningun Comprobante desde el MSVC Facturacion: {}", facturaRecibida);
        }
    }

    private void procesarMinutaDePago(MinutaDePagoEvent minutaRecibida) {
        if (minutaRecibida != null) {
            MovimientoCuentaCorriente movimiento = new MovimientoCuentaCorriente();
            movimiento.setImporte(minutaRecibida.getImporte());
            movimiento.setSaldoPendiente(0.0);
            movimiento.setComprobanteId(minutaRecibida.getId());
            movimiento.setNroComprobante(minutaRecibida.getNumero());
            movimiento.setDescripcion("Minuta de Pago " + minutaRecibida.getCajaNombre());
            movimiento.setOficial(minutaRecibida.getOficial());
            movimiento.setTipoMovimiento(MovimientoCuentaCorriente.TipoMovimiento.DEBITO);

            this.registrarMovimiento(minutaRecibida.getNroProveedor(), movimiento);
            log.info("Comprobante recibido: {}", minutaRecibida);
        } else {
            log.error("No se recibio ningun Comprobante desde el MSVC Facturacion: {}", minutaRecibida);
        }
    }

}
