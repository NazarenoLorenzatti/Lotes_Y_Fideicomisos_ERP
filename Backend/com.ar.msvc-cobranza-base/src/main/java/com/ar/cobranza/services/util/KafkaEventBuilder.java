package com.ar.cobranza.services.util;

import com.ar.cobranza.DTOs.AplicacionesCuentasContablesDTO;
import com.ar.cobranza.DTOs.ComprobanteConfirmadoEvent;
import com.ar.cobranza.entities.ImputacionPreReciboCaja;
import com.ar.cobranza.entities.PreRecibo;
import com.ar.cobranza.entities.ReciboAuxiliar;
import com.ar.cobranza.entities.ReciboOficial;
import com.ar.cobranza.services.impl.KafkaEventProducer;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class KafkaEventBuilder {

    private static final Long CC_DEUDORES_ID = 10L;
    private static final String CC_DEUDORES_NRO = "1.100.000";
    private static final String CC_DEUDORES_NOMBRE = "Deudores por Ventas";

    private static final Long CC_DEUDORES_AUX_ID = 16L;
    private static final String CC_DEUDORES_AUX_NRO = "91.100.000";
    private static final String CC_DEUDORES_AUX_NOMBRE = "Deudores por Ventas Aux";

    private final Long ESTADO_CANCELADO = 4L;

    @Autowired
    private KafkaEventProducer reciboEventProducer;

    public void publicarEvento(ReciboOficial comprobanteOfic, ReciboAuxiliar comprobanteAux) {
        ComprobanteConfirmadoEvent evento = new ComprobanteConfirmadoEvent();
        evento.setIdEntidad(1L);
        evento.setIdComprobante(comprobanteOfic != null ? comprobanteOfic.getId() : comprobanteAux.getId());
        evento.setContactoId(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getContactoId() : comprobanteAux.getPreRecibo().getContactoId());
        evento.setCbteFecha(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getRecibo_fecha() : comprobanteAux.getPreRecibo().getRecibo_fecha());
        evento.setImporteTotal(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getImporte_total() : comprobanteAux.getPreRecibo().getImporte_total());
        evento.setImporteIva(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getImporte_iva() : comprobanteAux.getPreRecibo().getImporte_iva());
        evento.setImporteNeto(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getImporte_neto() : comprobanteAux.getPreRecibo().getImporte_neto());
        evento.setImporte_gravado(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getImporte_gravado() : comprobanteAux.getPreRecibo().getImporte_gravado());
        evento.setNroComprobante(comprobanteOfic != null ? comprobanteOfic.getNumero_recibo() : comprobanteAux.getNumero_recibo());
        evento.setTipoComprobante(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getAbrebiaturaTipoRecibo() : comprobanteAux.getPreRecibo().getAbrebiaturaTipoRecibo());
        evento.setOficial(comprobanteOfic != null);
        evento.setEstado(comprobanteOfic != null ? comprobanteOfic.getPreRecibo().getEstado().getDescripcion() : comprobanteAux.getPreRecibo().getEstado().getDescripcion());

        if (comprobanteOfic != null) {
            if (comprobanteOfic.getComprobantesAsociado() != null) {
                if (comprobanteOfic.getComprobantesAsociado().getPreRecibo().getEstado().getId().equals(ESTADO_CANCELADO)) {
                    evento.setCancelado(true);
                }
                evento.setIdComprobanteAsociado(comprobanteOfic.getComprobantesAsociado().getId());
                evento.setUUIDComprobanteAsociado("UUID-" + comprobanteOfic.getComprobantesAsociado().getNumero_recibo());
            }
            List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables = new ArrayList();
            this.setAplicacionesCuentasContables(aplicacionesCuentasContables, comprobanteOfic.getPreRecibo(), evento);
        }

        if (comprobanteAux != null) {
            if (comprobanteAux.getComprobantesAsociado() != null) {
                if (comprobanteAux.getComprobantesAsociado().getPreRecibo().getEstado().getId().equals(ESTADO_CANCELADO)) {
                    evento.setCancelado(true);
                }
                evento.setIdComprobanteAsociado(comprobanteAux.getComprobantesAsociado().getId());
                evento.setUUIDComprobanteAsociado("UUID-" + comprobanteAux.getComprobantesAsociado().getNumero_recibo());
            }
            List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables = new ArrayList();
            this.setAplicacionesCuentasContables(aplicacionesCuentasContables, comprobanteAux.getPreRecibo(), evento);
        }
        reciboEventProducer.publicarEvento(evento);
    }

    private void setAplicacionesCuentasContables(List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables, PreRecibo preRecibo, ComprobanteConfirmadoEvent evento) {
        AplicacionesCuentasContablesDTO apply = new AplicacionesCuentasContablesDTO();
        apply.setIdCuenta(preRecibo.isOficial() ? CC_DEUDORES_ID : CC_DEUDORES_AUX_ID); //ARTICULOS ASOCIADOS
        apply.setNroCuenta(preRecibo.isOficial() ? CC_DEUDORES_NRO : CC_DEUDORES_AUX_NRO);
        apply.setNombreCuenta(preRecibo.isOficial() ? CC_DEUDORES_NOMBRE : CC_DEUDORES_AUX_NOMBRE);
        apply.setImporteAplicado(preRecibo.getImporte_total());
        apply.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.HABER);
        aplicacionesCuentasContables.add(apply);

        for (ImputacionPreReciboCaja imp : preRecibo.getImputaciones()) {
            AplicacionesCuentasContablesDTO applyCaja = new AplicacionesCuentasContablesDTO();
            applyCaja.setIdCuenta(imp.getCajaCobranza().getIdCuentaContable());
            applyCaja.setNombreCuenta(imp.getCajaCobranza().getNombreCuentaContable());
            applyCaja.setNroCuenta(imp.getCajaCobranza().getNroCuentaContable());
            applyCaja.setImporteAplicado(imp.getImporteImputado());
            applyCaja.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.DEBE);
            aplicacionesCuentasContables.add(applyCaja);
        }
        evento.setAplicacionesCuentasContables(aplicacionesCuentasContables);
    }
}
