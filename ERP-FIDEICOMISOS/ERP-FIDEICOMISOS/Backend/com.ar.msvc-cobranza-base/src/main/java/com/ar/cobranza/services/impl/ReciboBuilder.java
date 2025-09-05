package com.ar.cobranza.services.impl;

import ar.gov.afip.dif.facturaelectronica.*;
import com.ar.cobranza.entities.ReciboOficial;
import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class ReciboBuilder {

    private final int CANTIDAD_DE_REGISTROS = 1;

    public FECAERequest buildSolicituDeCAE(ReciboOficial comprobanteOficial) {
        try {
            ArrayOfFECAEDetRequest array = new ArrayOfFECAEDetRequest();
            FECAERequest request = new FECAERequest();
            request.setFeCabReq(createCabeceraRequest(CANTIDAD_DE_REGISTROS, comprobanteOficial));

            FECAEDetRequest detalle = new FECAEDetRequest();
            this.createBodyRequest(comprobanteOficial, detalle);

            if (comprobanteOficial.getComprobantesAsociado() != null) {
                detalle.setCbtesAsoc(this.setComprobantesAsociadosOficial(comprobanteOficial));
            }
            array.getFECAEDetRequest().add(detalle);
            request.setFeDetReq(array);
            return request;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private FECAEDetRequest createBodyRequest(ReciboOficial comprobanteOficial, FECAEDetRequest detalle) {
        detalle.setConcepto(comprobanteOficial.getPreRecibo().getConcepto()); // Productos
        detalle.setDocTipo(comprobanteOficial.getPreRecibo().getTipo_documento()); // DNI
        detalle.setDocNro(comprobanteOficial.getPreRecibo().getNro_documento()); // DNI del cliente
        detalle.setCbteDesde(comprobanteOficial.getCbte_nro_desde()); // Número de comprobante (el mismo en Desde y Hasta)
        detalle.setCbteHasta(comprobanteOficial.getCbte_nro_hasta());
        detalle.setCondicionIVAReceptorId(comprobanteOficial.getPreRecibo().getCondicion_iva_receptor());
        detalle.setCbteFch(comprobanteOficial.getPreRecibo().getRecibo_fecha());
        detalle.setMonId(comprobanteOficial.getPreRecibo().getMoneda()); // Pesos
        detalle.setMonCotiz(comprobanteOficial.getPreRecibo().getMoneda_cotizacion());
        if (comprobanteOficial.getPreRecibo().getConcepto() > 1) {
            detalle.setFchServDesde(comprobanteOficial.getPreRecibo().getFecha_desde());
            detalle.setFchServHasta(comprobanteOficial.getPreRecibo().getFecha_hasta());
            detalle.setFchVtoPago(comprobanteOficial.getPreRecibo().getFecha_vto());
        }
        switch (comprobanteOficial.getPreRecibo().getIdAfipTipoRecibo()) {
            case 9: // Recibo B
                this.setComprobanteTipoB(comprobanteOficial, detalle);
                break;
            case 15: // Recibo C
                this.setComprobanteTipoC(comprobanteOficial, detalle);
                break;
            case 4: // Recibo A
                this.setComprobanteTipoA(comprobanteOficial, detalle);
                break;
        }

        return detalle;
    }

    private ArrayOfCbteAsoc setComprobantesAsociadosOficial(ReciboOficial comprobanteOficial) {
        CbteAsoc cbteAsociado = new CbteAsoc();
        cbteAsociado.setTipo(comprobanteOficial.getComprobantesAsociado().getPreRecibo().getIdAfipTipoRecibo());
        cbteAsociado.setPtoVta(comprobanteOficial.getComprobantesAsociado().getPreRecibo().getPuntoVenta());
        cbteAsociado.setNro(comprobanteOficial.getCbte_nro_desde());
        cbteAsociado.setCuit(comprobanteOficial.getComprobantesAsociado().getPreRecibo().getCuit_emisor());
        ArrayOfCbteAsoc arrayAsoc = new ArrayOfCbteAsoc();
        arrayAsoc.getCbteAsoc().add(cbteAsociado);
        return arrayAsoc;
    }

    private void setAlicuotaIva(ReciboOficial comprobanteOficial, ArrayOfAlicIva arrayIva) {
        if (comprobanteOficial.getPreRecibo().getIdAfipAlicuotaIva() == null ) {
            return;
        }
        AlicIva iva = new AlicIva();
        iva.setId(comprobanteOficial.getPreRecibo().getIdAfipAlicuotaIva());
        iva.setBaseImp(comprobanteOficial.getPreRecibo().getImporte_total());
        iva.setImporte(comprobanteOficial.getPreRecibo().getImporte_iva());
        arrayIva.getAlicIva().add(iva);
    }

    private FECAECabRequest createCabeceraRequest(int cantRegistros, ReciboOficial comprobanteOficial) {
        FECAECabRequest cabecera = new FECAECabRequest();
        cabecera.setCantReg(cantRegistros);
        cabecera.setCbteTipo(comprobanteOficial.getPreRecibo().getIdAfipTipoRecibo());
        cabecera.setPtoVta(comprobanteOficial.getPreRecibo().getPuntoVenta());
        return cabecera;
    }

    private void setComprobanteTipoC(ReciboOficial comprobanteOficial, FECAEDetRequest detalle) {
        detalle.setIva(null);
        detalle.setImpTotal(comprobanteOficial.getPreRecibo().getImporte_total());
        detalle.setImpNeto(comprobanteOficial.getPreRecibo().getImporte_neto());
        detalle.setImpIVA(0);
        detalle.setImpTrib(0);
        detalle.setImpOpEx(comprobanteOficial.getPreRecibo().getImporte_excento());
        detalle.setImpTotConc(comprobanteOficial.getPreRecibo().getImporte_no_gravado());
    }

    private void setComprobanteTipoB(ReciboOficial comprobanteOficial, FECAEDetRequest detalle) {
        detalle.setImpTotal(comprobanteOficial.getPreRecibo().getImporte_total());
        detalle.setImpNeto(comprobanteOficial.getPreRecibo().getImporte_total());
        detalle.setImpIVA(0);
        detalle.setImpTrib(comprobanteOficial.getPreRecibo().getImporte_tributado());
        detalle.setImpOpEx(comprobanteOficial.getPreRecibo().getImporte_excento());
        detalle.setImpTotConc(comprobanteOficial.getPreRecibo().getImporte_no_gravado());
        
        comprobanteOficial.getPreRecibo().setImporte_iva(0.0);
        comprobanteOficial.getPreRecibo().setDescripcionIva("0%");
        ArrayOfAlicIva arrayIva = new ArrayOfAlicIva();
        this.setAlicuotaIva(comprobanteOficial, arrayIva);
        detalle.setIva(arrayIva);
    }

    private void setComprobanteTipoA(ReciboOficial comprobanteOficial, FECAEDetRequest detalle) {
        ArrayOfAlicIva arrayIva = new ArrayOfAlicIva();
        this.setAlicuotaIva(comprobanteOficial, arrayIva);
        detalle.setIva(arrayIva);
        detalle.setImpTotal(comprobanteOficial.getPreRecibo().getImporte_total());
        detalle.setImpNeto(comprobanteOficial.getPreRecibo().getImporte_neto());
        detalle.setImpIVA(comprobanteOficial.getPreRecibo().getImporte_iva());
        detalle.setImpTrib(comprobanteOficial.getPreRecibo().getImporte_iva());
        detalle.setImpNeto(comprobanteOficial.getPreRecibo().getImporte_total());
    }

}
