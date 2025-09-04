package com.ar.invoices.services.impl.util;

import com.ar.invoices.DTOs.AplicacionesCuentasContablesDTO;
import com.ar.invoices.DTOs.ComprobanteConfirmadoEvent;
import com.ar.invoices.entities.Articulo;
import com.ar.invoices.entities.ComprobanteAuxiliar;
import com.ar.invoices.entities.ComprobanteOficial;
import com.ar.invoices.entities.ItemFacturado;
import com.ar.invoices.entities.PreComprobante;
import com.ar.invoices.services.impl.KafkaEventProducer;
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

    private static final Long CC_IVA_VENTAS_ID = 14L;
    private static final String CC_IVA_VENTAS_NRO = "2.200.000";
    private static final String CC_IVA_VENTAS_NOMBRE = "Iva Ventas";

    private final Long ESTADO_CANCELADO = 4L;

   
    @Autowired
    private KafkaEventProducer comprobanteEventProducer;

    public void publicarEventoOfi(ComprobanteOficial comprobante) {
        ComprobanteConfirmadoEvent evento = new ComprobanteConfirmadoEvent();
        evento.setIdEntidad(comprobante.getPreComprobante().getAbrebiaturaTipoComprobante().equals("FB") ? 1L : 2L);
        evento.setIdComprobante(comprobante.getId());
        evento.setContactoId(comprobante.getPreComprobante().getContactoId());
        evento.setCbteFecha(comprobante.getPreComprobante().getCbte_fecha());
        evento.setImporteTotal(comprobante.getPreComprobante().getImporte_total());

        Double iva = comprobante.getPreComprobante().getImporte_iva() == 0
                ? comprobante.getPreComprobante().getImporte_neto() * 0.21
                : comprobante.getPreComprobante().getImporte_iva();
        evento.setImporteIva(iva);

        evento.setImporteNeto(comprobante.getPreComprobante().getImporte_neto());
        evento.setImporte_gravado(comprobante.getPreComprobante().getImporte_gravado());
        evento.setNroComprobante(comprobante.getNumero_comprobante());
        evento.setTipoComprobante(comprobante.getPreComprobante().getAbrebiaturaTipoComprobante());
        evento.setOficial(comprobante.getPreComprobante().isOficial());
        evento.setEstado(comprobante.getPreComprobante().getEstado().getDescripcion());
        if (comprobante.getComprobantesAsociado() != null) {
            if (comprobante.getComprobantesAsociado().getPreComprobante().getEstado().getId().equals(ESTADO_CANCELADO)) {
                evento.setCancelado(true);
            }
            evento.setIdComprobanteAsociado(comprobante.getComprobantesAsociado().getId());
            evento.setUUIDComprobanteAsociado("UUID-" + comprobante.getComprobantesAsociado().getNumero_comprobante());
        }
        List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables = new ArrayList();
        this.setAplicacionesCuentasContables(aplicacionesCuentasContables, comprobante.getPreComprobante(), evento);
        comprobanteEventProducer.publicarEvento(evento);
    }

    public void publicarEventoAux(ComprobanteAuxiliar comprobante) {
        ComprobanteConfirmadoEvent evento = new ComprobanteConfirmadoEvent();
        evento.setIdEntidad(comprobante.getPreComprobante().getAbrebiaturaTipoComprobante().equals("FB") ? 1L : 2L);
        evento.setIdComprobante(comprobante.getId());
        evento.setContactoId(comprobante.getPreComprobante().getContactoId());
        evento.setCbteFecha(comprobante.getPreComprobante().getCbte_fecha());
        evento.setImporteTotal(comprobante.getPreComprobante().getImporte_total());

        Double iva = comprobante.getPreComprobante().getImporte_iva() == 0
                ? comprobante.getPreComprobante().getImporte_neto() * 0.21
                : comprobante.getPreComprobante().getImporte_iva();
        evento.setImporteIva(iva);

        evento.setImporteNeto(comprobante.getPreComprobante().getImporte_neto());
        evento.setImporte_gravado(comprobante.getPreComprobante().getImporte_gravado());
        evento.setNroComprobante(comprobante.getNumero_comprobante());
        evento.setTipoComprobante(comprobante.getPreComprobante().getAbrebiaturaTipoComprobante());
        evento.setOficial(comprobante.getPreComprobante().isOficial());
        evento.setEstado(comprobante.getPreComprobante().getEstado().getDescripcion());
        if (comprobante.getComprobantesAsociado() != null) {
            if (comprobante.getComprobantesAsociado().getPreComprobante().getEstado().getId().equals(ESTADO_CANCELADO)) {
                evento.setCancelado(true);
            }
            evento.setIdComprobanteAsociado(comprobante.getComprobantesAsociado().getId());
            evento.setUUIDComprobanteAsociado("UUID-" + comprobante.getComprobantesAsociado().getNumero_comprobante());
        }
        List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables = new ArrayList();
        this.setAplicacionesCuentasContables(aplicacionesCuentasContables, comprobante.getPreComprobante(), evento);
        comprobanteEventProducer.publicarEvento(evento);
    }

    private void setAplicacionesCuentasContables(List<AplicacionesCuentasContablesDTO> aplicacionesCuentasContables, PreComprobante preComprobante, ComprobanteConfirmadoEvent evento) {
        AplicacionesCuentasContablesDTO applyProvedores = new AplicacionesCuentasContablesDTO();
        applyProvedores.setIdCuenta(preComprobante.isOficial() ? CC_DEUDORES_ID : CC_DEUDORES_AUX_ID); //ARTICULOS ASOCIADOS
        applyProvedores.setNroCuenta(preComprobante.isOficial() ? CC_DEUDORES_NRO : CC_DEUDORES_AUX_NRO);
        applyProvedores.setNombreCuenta(preComprobante.isOficial() ? CC_DEUDORES_NOMBRE : CC_DEUDORES_AUX_NOMBRE);
        applyProvedores.setImporteAplicado(preComprobante.getImporte_total());
        applyProvedores.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.DEBE);
        aplicacionesCuentasContables.add(applyProvedores);

        if (preComprobante.isOficial()) {
            AplicacionesCuentasContablesDTO applyIvaCompras = new AplicacionesCuentasContablesDTO();
            applyIvaCompras.setIdCuenta(CC_IVA_VENTAS_ID); //ARTICULOS ASOCIADOS
            applyIvaCompras.setNroCuenta(CC_IVA_VENTAS_NRO);
            applyIvaCompras.setNombreCuenta(CC_IVA_VENTAS_NOMBRE);
            applyIvaCompras.setImporteAplicado(preComprobante.getImporte_iva());
            applyIvaCompras.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.HABER);
            aplicacionesCuentasContables.add(applyIvaCompras);
        }

        for (ItemFacturado item : preComprobante.getItems()) {
            if (item.getArticulo() != null) {
                Articulo art = item.getArticulo();
                AplicacionesCuentasContablesDTO applyArt = new AplicacionesCuentasContablesDTO();
                applyArt.setIdCuenta(preComprobante.isOficial() ? art.getIdCuentaContable() : art.getIdCuentaContableAux()); //ARTICULOS ASOCIADOS
                applyArt.setNroCuenta(preComprobante.isOficial() ? art.getNroCuentaContable() : art.getNroCuentaContableAux());
                applyArt.setNombreCuenta(preComprobante.isOficial() ? art.getNombreCuentaContable() : art.getNombreCuentaContableAux());
                applyArt.setImporteAplicado(item.getImporteTotalSinIva());
                applyArt.setTipoMov(AplicacionesCuentasContablesDTO.TipoMov.HABER);
                aplicacionesCuentasContables.add(applyArt);
            }
        }
        evento.setAplicacionesCuentasContables(aplicacionesCuentasContables);
    }
}
