package com.ar.cobranza.services.util;

import com.ar.cobranza.entities.*;
import com.ar.cobranza.repositories.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class ContraReciboBuilder {

    @Autowired
    private iPreReciboDao preReciboDao;

    @Autowired
    private iReciboAuxiliarDao reciboAuxiliarDao;

    @Autowired
    private iReciboOficialDao reciboOficialDao;
    
        @Autowired
    private iEstadoReciboDao estadoReciboDao;


    private final KafkaEventBuilder kafkaEventBuilder;

    private final Long ESTADO_CONFIRMADO = 2L;
    private final Long ESTADO_VALIDADO = 3L;
    private final Long ESTADO_CANCELADO = 4L;

    public ContraReciboBuilder(KafkaEventBuilder kafkaEventBuilder) {
        this.kafkaEventBuilder = kafkaEventBuilder;
    }

    public void buildContraRecibo(Long id) {
        Optional<PreRecibo> preRecibo = preReciboDao.findById(id);

        if (preRecibo.isEmpty()) {
            throw new IllegalArgumentException("No se encontró PreRecibo con ID: " + id);
        }
        PreRecibo original = preRecibo.get();
        PreRecibo nuevoPrecontrarecibo = new PreRecibo();

        // Copia de campos
        nuevoPrecontrarecibo.setCuit_emisor(original.getCuit_emisor());

        // Valores del body
        nuevoPrecontrarecibo.setRecibo_fecha(original.getRecibo_fecha());
        nuevoPrecontrarecibo.setFecha_desde(original.getFecha_desde());
        nuevoPrecontrarecibo.setFecha_hasta(original.getFecha_hasta());
        nuevoPrecontrarecibo.setFecha_vto(original.getFecha_vto());
        nuevoPrecontrarecibo.setImporte_neto(original.getImporte_neto());
        nuevoPrecontrarecibo.setImporte_total(original.getImporte_total());
        nuevoPrecontrarecibo.setImporte_iva(original.getImporte_iva());
        nuevoPrecontrarecibo.setImporte_gravado(original.getImporte_gravado());
        nuevoPrecontrarecibo.setImporte_excento(original.getImporte_excento());
        nuevoPrecontrarecibo.setImporte_no_gravado(original.getImporte_no_gravado());
        nuevoPrecontrarecibo.setImporte_tributado(original.getImporte_tributado());
        nuevoPrecontrarecibo.setConcepto(original.getConcepto());
        nuevoPrecontrarecibo.setMoneda(original.getMoneda());
        nuevoPrecontrarecibo.setOficial(original.isOficial());
        nuevoPrecontrarecibo.setDescripcionIva(original.getDescripcionIva());

        // Deducidos
        nuevoPrecontrarecibo.setMoneda_cotizacion(original.getMoneda_cotizacion());

        // Contacto
        nuevoPrecontrarecibo.setNro_documento(original.getNro_documento());
        nuevoPrecontrarecibo.setTipo_documento(original.getTipo_documento());
        nuevoPrecontrarecibo.setRazon_social(original.getRazon_social());
        nuevoPrecontrarecibo.setDireccion_fiscal(original.getDireccion_fiscal());
        nuevoPrecontrarecibo.setLocalidad(original.getLocalidad());
        nuevoPrecontrarecibo.setEmail_envio(original.getEmail_envio());
        nuevoPrecontrarecibo.setContactoId(original.getContactoId());
        nuevoPrecontrarecibo.setCondicion_iva_receptor(original.getCondicion_iva_receptor());

        // Fechas
        nuevoPrecontrarecibo.setFechaCreacion(LocalDateTime.now());
        nuevoPrecontrarecibo.setFechaModificacion(LocalDateTime.now());

        // Datos AFIP
        nuevoPrecontrarecibo.setIdAfipTipoRecibo(original.getIdAfipTipoRecibo());
        nuevoPrecontrarecibo.setAbrebiaturaTipoRecibo("C" + original.getAbrebiaturaTipoRecibo());
        nuevoPrecontrarecibo.setDescripcionTipoRecibo("CONTRA " + original.getDescripcionTipoRecibo());
        nuevoPrecontrarecibo.setPuntoVenta(original.getPuntoVenta());
        nuevoPrecontrarecibo.setNombrePtoVenta(original.getNombrePtoVenta());
        nuevoPrecontrarecibo.setIdAfipAlicuotaIva(original.getIdAfipAlicuotaIva());

        // Estado comprobante
        nuevoPrecontrarecibo.setEstado(nuevoPrecontrarecibo.isOficial() 
                ? estadoReciboDao.findById(ESTADO_VALIDADO).get() 
                : estadoReciboDao.findById(ESTADO_CONFIRMADO).get());
        
        List<ImputacionPreReciboCaja> list = new ArrayList();
        for (ImputacionPreReciboCaja imp : original.getImputaciones()) {
            ImputacionPreReciboCaja reversaImputacion = new ImputacionPreReciboCaja();
            reversaImputacion.setCajaCobranza(imp.getCajaCobranza());
            reversaImputacion.setImporteImputado(imp.getImporteImputado() * -1);
            reversaImputacion.setFechaImputacion(LocalDateTime.now());
            reversaImputacion.setPreRecibo(nuevoPrecontrarecibo);
            list.add(reversaImputacion);
        }
        nuevoPrecontrarecibo.setImputaciones(list);
        nuevoPrecontrarecibo = preReciboDao.save(nuevoPrecontrarecibo);

        if (nuevoPrecontrarecibo.isOficial()) {
            Optional<ReciboOficial> optionalRecibo = reciboOficialDao.findByPreRecibo(original);
            ReciboOficial reciboOficial = new ReciboOficial();
            reciboOficial.setCae_afip(optionalRecibo.get().getCae_afip());
            reciboOficial.setCbte_nro_desde(optionalRecibo.get().getCbte_nro_desde());
            reciboOficial.setCbte_nro_hasta(optionalRecibo.get().getCbte_nro_hasta());
            reciboOficial.setFecha_oficilizacion(new Date());
            reciboOficial.setValid(true);
            reciboOficial.setNumero_recibo("C" + optionalRecibo.get().getNumero_recibo());
            reciboOficial.setVto_cae(optionalRecibo.get().getVto_cae());
            reciboOficial.setComprobantesAsociado(optionalRecibo.get());
            reciboOficial.setPreRecibo(nuevoPrecontrarecibo);
            reciboOficial = reciboOficialDao.save(reciboOficial);
            this.kafkaEventBuilder.publicarEvento(reciboOficial, null);
        }

        if (!nuevoPrecontrarecibo.isOficial()) {
            Optional<ReciboAuxiliar> optionalRecibo = reciboAuxiliarDao.findByPreRecibo(original);
            ReciboAuxiliar reciboAuxiliar = new ReciboAuxiliar();
            reciboAuxiliar.setFecha_confirmacion(new Date());
            reciboAuxiliar.setValid(false);
            reciboAuxiliar.setNumero_recibo("C" + optionalRecibo.get().getNumero_recibo());
            reciboAuxiliar.setPreRecibo(nuevoPrecontrarecibo);
            reciboAuxiliar.setComprobantesAsociado(optionalRecibo.get());
            reciboAuxiliar = reciboAuxiliarDao.save(reciboAuxiliar);
            this.kafkaEventBuilder.publicarEvento(null, reciboAuxiliar);
        }

    }

}
