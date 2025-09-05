package com.ar.invoices.services.impl;

import ar.gov.afip.dif.facturaelectronica.*;
import com.ar.invoices.entities.ComprobanteOficial;
import com.ar.invoices.entities.ItemFacturado;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class ComprobantesBuilder {

    private final int CANTIDAD_DE_REGISTROS = 1;

    public FECAERequest buildSolicituDeCAE(ComprobanteOficial comprobanteOficial) {
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

    private FECAEDetRequest createBodyRequest(ComprobanteOficial comprobanteOficial, FECAEDetRequest detalle) {
        detalle.setConcepto(comprobanteOficial.getPreComprobante().getConcepto()); // Productos
        detalle.setDocTipo(comprobanteOficial.getPreComprobante().getTipo_documento()); // DNI
        detalle.setDocNro(comprobanteOficial.getPreComprobante().getNro_documento()); // DNI del cliente
        detalle.setCbteDesde(comprobanteOficial.getCbte_nro_desde()); // Número de comprobante (el mismo en Desde y Hasta)
        detalle.setCbteHasta(comprobanteOficial.getCbte_nro_hasta());
        detalle.setCbteFch(comprobanteOficial.getPreComprobante().getCbte_fecha());
        detalle.setMonId(comprobanteOficial.getPreComprobante().getMoneda()); // Pesos
        detalle.setMonCotiz(comprobanteOficial.getPreComprobante().getMoneda_cotizacion());
        detalle.setCondicionIVAReceptorId(comprobanteOficial.getPreComprobante().getCondicion_iva_receptor());
        if (comprobanteOficial.getPreComprobante().getConcepto() > 1) {
            detalle.setFchServDesde(comprobanteOficial.getPreComprobante().getFecha_desde());
            detalle.setFchServHasta(comprobanteOficial.getPreComprobante().getFecha_hasta());
            detalle.setFchVtoPago(comprobanteOficial.getPreComprobante().getFecha_vto());
        }
        switch (comprobanteOficial.getPreComprobante().getIdAfipTipoComprobante()) {
            case 6: // Factura B
                this.setComprobanteTipoB(comprobanteOficial, detalle);
                break;
            case 11: // Factura C
                this.setComprobanteTipoC(comprobanteOficial, detalle);
                break;
            case 1: // Factura A
                this.setComprobanteTipoA(comprobanteOficial, detalle);
                break;
        }

        return detalle;
    }

    private ArrayOfCbteAsoc setComprobantesAsociadosOficial(ComprobanteOficial comprobanteOficial) {
        CbteAsoc cbteAsociado = new CbteAsoc();
        cbteAsociado.setTipo(comprobanteOficial.getComprobantesAsociado().getPreComprobante().getIdAfipTipoComprobante());
        cbteAsociado.setPtoVta(comprobanteOficial.getComprobantesAsociado().getPreComprobante().getPuntoVenta());
        cbteAsociado.setNro(comprobanteOficial.getCbte_nro_desde());
        cbteAsociado.setCuit(comprobanteOficial.getComprobantesAsociado().getPreComprobante().getCuit_emisor());
        ArrayOfCbteAsoc arrayAsoc = new ArrayOfCbteAsoc();
        arrayAsoc.getCbteAsoc().add(cbteAsociado);
        return arrayAsoc;
    }

    private void setAlicuotaIva(ComprobanteOficial comprobanteOficial, ArrayOfAlicIva arrayIva, FECAEDetRequest detalle) {
        for (ItemFacturado item : comprobanteOficial.getPreComprobante().getItems()) {
            AlicIva iva = new AlicIva();
            iva.setId(detalle.getImpIVA() == 0 ? 3 : item.getIdAfipAlicuotaIva());
            iva.setBaseImp(item.getImporteTotalConIva());
            iva.setImporte(detalle.getImpIVA() == 0 ? 0 : item.getImporteTotalIva());
            arrayIva.getAlicIva().add(iva);
        }

    }

    private FECAECabRequest createCabeceraRequest(int cantRegistros, ComprobanteOficial comprobanteOficial) {
        FECAECabRequest cabecera = new FECAECabRequest();
        cabecera.setCantReg(cantRegistros);
        cabecera.setCbteTipo(comprobanteOficial.getPreComprobante().getIdAfipTipoComprobante());
        cabecera.setPtoVta(comprobanteOficial.getPreComprobante().getPuntoVenta());
        return cabecera;
    }

    private void setComprobanteTipoC(ComprobanteOficial comprobanteOficial, FECAEDetRequest detalle) {
        detalle.setIva(null);
        detalle.setImpTotal(comprobanteOficial.getPreComprobante().getImporte_total());
        detalle.setImpNeto(comprobanteOficial.getPreComprobante().getImporte_neto());
        detalle.setImpIVA(0);
        detalle.setImpTrib(0);
        detalle.setImpOpEx(comprobanteOficial.getPreComprobante().getImporte_excento());
        detalle.setImpTotConc(comprobanteOficial.getPreComprobante().getImporte_no_gravado());
    }

    private void setComprobanteTipoB(ComprobanteOficial comprobanteOficial, FECAEDetRequest detalle) {
        detalle.setImpTotal(comprobanteOficial.getPreComprobante().getImporte_total());
        detalle.setImpNeto(comprobanteOficial.getPreComprobante().getImporte_total());
        detalle.setImpIVA(0);
        detalle.setImpTrib(comprobanteOficial.getPreComprobante().getImporte_tributado());
        detalle.setImpOpEx(comprobanteOficial.getPreComprobante().getImporte_excento());
        detalle.setImpTotConc(comprobanteOficial.getPreComprobante().getImporte_no_gravado());
        
        //comprobanteOficial.getPreComprobante().setImporte_iva(0.0);
        for (ItemFacturado item : comprobanteOficial.getPreComprobante().getItems()){
            item.setDescripcionAlicuota("0%");
        }            
        ArrayOfAlicIva arrayIva = new ArrayOfAlicIva();
        this.setAlicuotaIva(comprobanteOficial, arrayIva, detalle);
        detalle.setIva(arrayIva);
    }

    private void setComprobanteTipoA(ComprobanteOficial comprobanteOficial, FECAEDetRequest detalle) {
        ArrayOfAlicIva arrayIva = new ArrayOfAlicIva();
        this.setAlicuotaIva(comprobanteOficial, arrayIva, detalle);
        detalle.setIva(arrayIva);
        detalle.setImpTotal(comprobanteOficial.getPreComprobante().getImporte_total());
        detalle.setImpNeto(comprobanteOficial.getPreComprobante().getImporte_neto());
        detalle.setImpIVA(comprobanteOficial.getPreComprobante().getImporte_iva());
        detalle.setImpTrib(comprobanteOficial.getPreComprobante().getImporte_iva());
        detalle.setImpNeto(comprobanteOficial.getPreComprobante().getImporte_total());
    }

}
