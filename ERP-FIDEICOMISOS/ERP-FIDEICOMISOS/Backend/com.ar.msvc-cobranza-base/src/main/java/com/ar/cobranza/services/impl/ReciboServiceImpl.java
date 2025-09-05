package com.ar.cobranza.services.impl;

import ar.gov.afip.dif.facturaelectronica.FECAERequest;
import ar.gov.afip.dif.facturaelectronica.FECAEResponse;
import com.ar.cobranza.ClientsFeign.ClientFeignAfipConfigurations;
import com.ar.cobranza.entities.*;
import com.ar.cobranza.responses.BuildResponsesServicesImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ar.cobranza.repositories.*;
import com.ar.cobranza.services.iReciboService;
import com.ar.cobranza.services.util.ContraReciboBuilder;
import com.ar.cobranza.services.util.KafkaEventBuilder;

@Service
@Slf4j
public class ReciboServiceImpl extends BuildResponsesServicesImpl implements iReciboService {

    @Autowired
    private iPreReciboDao preReciboDao;

    @Autowired
    private iEstadoReciboDao estadoReciboDao;

    @Autowired
    private iReciboOficialDao reciboOficialDao;

    @Autowired
    private ReciboBuilder recibosBuilder;

    @Autowired
    private iReciboAuxiliarDao reciboAuxiliarDao;

    @Autowired
    private ClientFeignAfipConfigurations afipConfigurations;

    private final ContraReciboBuilder contraReciboBuilder;
    private final KafkaEventBuilder kafkaEventBuilder;
    
    public ReciboServiceImpl(ContraReciboBuilder contraReciboBuilder, KafkaEventBuilder kafkaEventBuilder) {
        this.contraReciboBuilder = contraReciboBuilder;
        this.kafkaEventBuilder = kafkaEventBuilder;
    }

    private final int CANTIDAD_DE_REGISTROS = 1;
    private final Long ESTADO_CONFIRMADO = 2L;
    private final Long ESTADO_VALIDADO = 3L;
    private final Long ESTADO_CANCELADO = 4L;

    @Override
    public ResponseEntity<?> confirmarPreRecibo(Long idPreComprobante) {
        Optional<PreRecibo> o = preReciboDao.findById(idPreComprobante);

        if (o.isEmpty()) {
            return this.buildResponse("nOK", "02", "No se encontro ningun comprobante a confirmar", null, HttpStatus.NOT_FOUND);
        }

        if (o.get().isOficial()) {
            return this.confirmarComprobanteConCae(o.get(), null);
        } else {
            return this.confirmarComprobanteAuxiliar(o.get(), null);
        }
    }

    @Override
    public ResponseEntity<?> confirmarPreRecibo(Long idPreComprobante, Long idComprobanteAsociado) {
        Optional<PreRecibo> o = preReciboDao.findById(idPreComprobante);

        if (o.isEmpty()) {
            return this.buildResponse("nOK", "02", "No se encontro ningun comprobante a confirmar", null, HttpStatus.NOT_FOUND);
        }

        if (o.get().isOficial()) {
            return this.confirmarComprobanteConCae(o.get(), idComprobanteAsociado);
        } else {
            return this.confirmarComprobanteAuxiliar(o.get(), idComprobanteAsociado);
        }
    }

    @Override
    public ResponseEntity<?> getRecibosOficiales() {
        try {
            List<ReciboOficial> list = reciboOficialDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Ok", "00", "Lista de Recibos Vacia", list, HttpStatus.OK);
            }
            return this.buildResponse("Ok", "00", "Lista de Comprobantes", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getRecibosAuxiliares() {
        try {
            List<ReciboAuxiliar> list = reciboAuxiliarDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Ok", "00", "Lista de Recibos Vacia", list, HttpStatus.OK);
            }
            return this.buildResponse("Ok", "00", "Lista de Recibos", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> findReciboAuxiliar(Long idComprobante) {
        try {
            if (idComprobante == null) {
                return this.buildResponse("Error", "02", "No se envio ningun ID de comprobante valido", null, HttpStatus.BAD_REQUEST);
            }
            Optional<ReciboAuxiliar> o = reciboAuxiliarDao.findById(idComprobante);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No se pudo obtener el comprobante", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", "00", "Comprobante encontrado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> findReciboOficial(Long idComprobante) {
        try {
            if (idComprobante == null) {
                return this.buildResponse("Error", "02", "No se envio ningun ID de comprobante valido", null, HttpStatus.BAD_REQUEST);
            }
            Optional<ReciboOficial> o = reciboOficialDao.findById(idComprobante);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No se pudo obtener el comprobante", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", "00", "Comprobante encontrado", o.isPresent(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> cancelarRecibo(Long id) {
        try {
            if (id == null) {
                return this.buildResponse("Error", "02", "No se envio ningun ID de comprobante valido", null, HttpStatus.BAD_REQUEST);
            }
            Optional<PreRecibo> o = preReciboDao.findById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No se pudo obtener el comprobante", null, HttpStatus.OK);
            }
            
            if(o.get().getEstado().getId().equals(ESTADO_CANCELADO)) {
                return this.buildResponse("nOk", "02", "El recibo ya se encuentra Cancelado", null, HttpStatus.OK);
            }
            o.get().setEstado(estadoReciboDao.findById(ESTADO_CANCELADO).get());
            this.contraReciboBuilder.buildContraRecibo(id);
            preReciboDao.save(o.get());
            return this.buildResponse("Ok", "00", "Comprobante encontrado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Transactional
    private ResponseEntity<?> confirmarComprobanteConCae(PreRecibo preRecibo, Long idComprobanteAsociado) {
        try {
            Optional<ReciboOficial> optional = reciboOficialDao.findByPreRecibo(preRecibo);
            ReciboOficial reciboOficial = optional.isPresent() ? optional.get() : new ReciboOficial();
            reciboOficial.setPreRecibo(preRecibo);

            if (reciboOficial.getPreRecibo().getEstado().getId().equals(ESTADO_VALIDADO)) {
                return this.buildResponse("ok", "00", "Comprobante Ya validado", reciboOficial, HttpStatus.OK);
            }

            Integer proximoNumero = this.getUltimoNumeroSecuencia(preRecibo);
            if (proximoNumero == null) {
                return this.buildResponse("nok", "02", "No se pudo obtener el numero de secuencia", reciboOficial, HttpStatus.NOT_FOUND);
            }

            proximoNumero += CANTIDAD_DE_REGISTROS;
            reciboOficial.setCbte_nro_desde(proximoNumero);
            reciboOficial.setCbte_nro_hasta(proximoNumero);

            FECAERequest request = this.recibosBuilder.buildSolicituDeCAE(reciboOficial);

            if (request == null) {
                return this.buildErrorResponse("Error", "-01", "Error al solicitar el Cae en Afip");
            }
            this.setEstadoComprobante(reciboOficial, null, ESTADO_CONFIRMADO);
            this.buildNumeroComprobante(reciboOficial);

            ResponseEntity<FECAEResponse> responseCae = afipConfigurations.solicitarCae(request, "wsfe", reciboOficial.getPreRecibo().getCuit_emisor());

            FECAEResponse cae = responseCae.getBody();
            if (cae.getErrors() != null || cae.getFeCabResp().getResultado().equals("R")) {
                return this.buildResponse("nOK", "02", cae.getFeDetResp().getFECAEDetResponse().get(0).getObservaciones().getObs().get(0).getMsg(),
                        cae, HttpStatus.BAD_REQUEST);
            }
            this.getCaeInformacion(cae, reciboOficial);
            this.setEstadoComprobante(reciboOficial, null, ESTADO_VALIDADO);

            reciboOficial.setFecha_oficilizacion(new Date());

            reciboOficialDao.save(reciboOficial);
            afipConfigurations.saveSecuencia(preRecibo.getIdAfipTipoRecibo(), preRecibo.getPuntoVenta(),
                    preRecibo.getCuit_emisor(), proximoNumero);
          this.kafkaEventBuilder.publicarEvento(reciboOficial, null);
            return this.buildResponse("OK", "00", "Solicitud de CAE correcta", reciboOficial, HttpStatus.OK);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error al solicitar el Cae en Afip");
        }
    }

    @Transactional
    public ResponseEntity<?> confirmarComprobanteAuxiliar(PreRecibo preComprobante, Long idComprobanteAsociado) {
        Optional<ReciboAuxiliar> optional = reciboAuxiliarDao.findByPreRecibo(preComprobante);
        ReciboAuxiliar comprobanteAuxiliar = optional.isPresent() ? optional.get() : new ReciboAuxiliar();
        comprobanteAuxiliar.setPreRecibo(preComprobante);

        if (comprobanteAuxiliar.getPreRecibo().getEstado().getId().equals(ESTADO_CONFIRMADO)) {
            return this.buildResponse("ok", "00", "Comprobante Ya Confirmado", comprobanteAuxiliar, HttpStatus.OK);
        }
        Integer proximoNumero = this.getUltimoNumeroSecuencia(preComprobante);
        if (proximoNumero == null) {
            return this.buildResponse("nok", "02", "No se pudo obtener el numero de secuencia", comprobanteAuxiliar, HttpStatus.NOT_FOUND);
        }
        comprobanteAuxiliar = reciboAuxiliarDao.save(comprobanteAuxiliar);
        comprobanteAuxiliar.setNumero_recibo(comprobanteAuxiliar.getPreRecibo().getAbrebiaturaTipoRecibo()
                + " " + String.format("%05d", comprobanteAuxiliar.getPreRecibo().getPuntoVenta()).replace("0", "9")
                + " " + String.format("%08d", comprobanteAuxiliar.getId())
        );

        this.setEstadoComprobante(null, comprobanteAuxiliar, ESTADO_CONFIRMADO);
        comprobanteAuxiliar.setFecha_confirmacion(new Date());
        comprobanteAuxiliar = reciboAuxiliarDao.save(comprobanteAuxiliar);
        this.kafkaEventBuilder.publicarEvento(null,comprobanteAuxiliar);
        if (comprobanteAuxiliar != null) {
            return this.buildResponse("OK", "00", "Se confirmo el comprobante", comprobanteAuxiliar, HttpStatus.OK);
        } else {
            return this.buildErrorResponse("Error", "-01", "Error al Confirmar el Comprobante");
        }
    }

    private Integer getUltimoNumeroSecuencia(PreRecibo preComprobante) {
        ResponseEntity<?> response2 = afipConfigurations.getLastNumber(preComprobante.getIdAfipTipoRecibo(), preComprobante.getPuntoVenta(), preComprobante.getCuit_emisor());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.convertValue(response2.getBody(), JsonNode.class);
        Integer numero = root.path("response").path("response").get(0).asInt();
        return numero;
    }

    private void buildNumeroComprobante(ReciboOficial comprobanteOficial) {
        comprobanteOficial.setNumero_recibo(
                comprobanteOficial.getPreRecibo().getAbrebiaturaTipoRecibo()
                + " " + String.format("%05d", comprobanteOficial.getPreRecibo().getPuntoVenta())
                + " " + String.format("%08d", comprobanteOficial.getCbte_nro_desde()));
    }

    private void getCaeInformacion(FECAEResponse responseCAE, ReciboOficial comprobanteOficial) {
        comprobanteOficial.setCae_afip(responseCAE.getFeDetResp().getFECAEDetResponse().get(0).getCAE());
        comprobanteOficial.setVto_cae(responseCAE.getFeDetResp().getFECAEDetResponse().get(0).getCAEFchVto());
    }

    private void setEstadoComprobante(ReciboOficial comprobanteOficial, ReciboAuxiliar comprobanteAuxiliar, Long estado) {
        if (comprobanteOficial != null) {
            comprobanteOficial.getPreRecibo().setEstado(estadoReciboDao.findById(estado).get());
        }
        if (comprobanteAuxiliar != null) {
            comprobanteAuxiliar.getPreRecibo().setEstado(estadoReciboDao.findById(estado).get());
        }
    }

}
