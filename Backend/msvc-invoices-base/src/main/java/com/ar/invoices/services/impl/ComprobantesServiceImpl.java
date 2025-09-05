package com.ar.invoices.services.impl;

import ar.gov.afip.dif.facturaelectronica.FECAERequest;
import ar.gov.afip.dif.facturaelectronica.FECAEResponse;
import com.ar.invoices.ClientsFeign.ClientFeignAfipConfigurations;
import com.ar.invoices.DTOs.ImporteAplicadoEvent;
import com.ar.invoices.entities.*;
import com.ar.invoices.repositories.*;
import com.ar.invoices.responses.BuildResponsesServicesImpl;
import com.ar.invoices.services.iComprobantesService;
import com.ar.invoices.services.impl.util.KafkaEventBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComprobantesServiceImpl extends BuildResponsesServicesImpl implements iComprobantesService {

    @Autowired
    private iPreComprobanteDao preComprobanteDao;

    @Autowired
    private iEstadoComprobanteDao estadoComprobanteDao;

    @Autowired
    private iComprobanteOficialDao comprobanteOficialDao;

    @Autowired
    private ComprobantesBuilder comprobantesBuilder;

    @Autowired
    private iComprobanteAuxiliarDao comprobanteAuxiliarDao;

    @Autowired
    private KafkaEventBuilder kafkaEventBuilder;

    @Autowired
    private ClientFeignAfipConfigurations afipConfigurations;

    private final int CANTIDAD_DE_REGISTROS = 1;
    private final Long ESTADO_CONFIRMADO = 2L;
    private final Long ESTADO_VALIDADO = 3L;
    private final Long ESTADO_CANCELADO = 4L;

    @Override
    public ResponseEntity<?> confirmarPreComprobante(Long idPreComprobante) {
        Optional<PreComprobante> o = preComprobanteDao.findById(idPreComprobante);

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
    public ResponseEntity<?> confirmarPreComprobante(Long idPreComprobante, Long idComprobanteAsociado) {
        Optional<PreComprobante> o = preComprobanteDao.findById(idPreComprobante);

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
    public ResponseEntity<?> getComprobantesOficiales(boolean findPendientes) {
        try {
            List<ComprobanteOficial> list = findPendientes
                    ? comprobanteOficialDao.findAllBySaldado(!findPendientes)
                    : comprobanteOficialDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Ok", "00", "Lista Vacia", list, HttpStatus.OK);
            }
            return this.buildResponse("Ok", "00", "Lista de Comprobantes", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getComprobantesAuxiliares(boolean findPendientes) {
        try {
            List<ComprobanteAuxiliar> list = findPendientes
                    ? comprobanteAuxiliarDao.findAllBySaldado(!findPendientes)
                    : comprobanteAuxiliarDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Ok", "00", "Lista Vacia", list, HttpStatus.OK);
            }
            return this.buildResponse("Ok", "00", "Lista de Comprobantes", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> findComprobanteAuxiliar(Long idComprobante) {
        try {
            if (idComprobante == null) {
                return this.buildResponse("Error", "02", "No se envio ningun ID de comprobante valido", null, HttpStatus.BAD_REQUEST);
            }
            Optional<ComprobanteAuxiliar> o = comprobanteAuxiliarDao.findById(idComprobante);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No se encontro el comprobante", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", "00", "Comprobante encontrado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> findComprobanteOficial(Long idComprobante) {
        try {
            if (idComprobante == null) {
                return this.buildResponse("Error", "02", "No se envio ningun ID de comprobante valido", null, HttpStatus.BAD_REQUEST);
            }
            Optional<ComprobanteOficial> o = comprobanteOficialDao.findById(idComprobante);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No se encontro el comprobante", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", "00", "Comprobante encontrado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Transactional
    private ResponseEntity<?> confirmarComprobanteConCae(PreComprobante preComprobante, Long idComprobanteAsociado) {
        try {
            Optional<ComprobanteOficial> optional = comprobanteOficialDao.findByPreComprobante(preComprobante);
            ComprobanteOficial comprobanteOficial = optional.isPresent() ? optional.get() : new ComprobanteOficial();
            comprobanteOficial.setPreComprobante(preComprobante);
            boolean continuar;
            if (idComprobanteAsociado != null) {
                Optional<ComprobanteOficial> optionalAsociado = comprobanteOficialDao.findById(idComprobanteAsociado);
                comprobanteOficial.setComprobantesAsociado(optionalAsociado.get());
                continuar = this.cancelarComprobanteAsociado(comprobanteOficial);
                if (!continuar) {
                    return this.buildResponse("nOk", "02", "El comprobante que intenta Cancelar "
                            + "ya se encuentra cancelado por otra entidad", comprobanteOficial, HttpStatus.OK);
                }
            }

            if (comprobanteOficial.getPreComprobante().getEstado().getId().equals(ESTADO_VALIDADO)) {
                return this.buildResponse("ok", "00", "Comprobante Ya validado", comprobanteOficial, HttpStatus.OK);
            }

            Integer proximoNumero = this.getUltimoNumeroSecuencia(preComprobante);
            if (proximoNumero == null) {
                return this.buildResponse("nok", "02", "No se pudo obtener el numero de secuencia", comprobanteOficial, HttpStatus.NOT_FOUND);
            }

            proximoNumero += CANTIDAD_DE_REGISTROS;
            comprobanteOficial.setCbte_nro_desde(proximoNumero);
            comprobanteOficial.setCbte_nro_hasta(proximoNumero);

            FECAERequest request = this.comprobantesBuilder.buildSolicituDeCAE(comprobanteOficial);

            if (request == null) {
                return this.buildErrorResponse("Error", "-01", "Error al solicitar el Cae en Afip");
            }
            //this.setEstadoComprobante(comprobanteOficial, null, ESTADO_CONFIRMADO);
            this.buildNumeroComprobante(comprobanteOficial);

            ResponseEntity<FECAEResponse> responseCae = afipConfigurations.solicitarCae(request, "wsfe", comprobanteOficial.getPreComprobante().getCuit_emisor());

            FECAEResponse cae = responseCae.getBody();
            if (cae.getErrors() != null || cae.getFeCabResp().getResultado().equals("R")) {
                return this.buildResponse("nOK", "02", "No se pudo obtener el cae de Afip: ",
                        cae, HttpStatus.REQUEST_TIMEOUT);
            }
            this.getCaeInformacion(cae, comprobanteOficial);
            this.setEstadoComprobante(comprobanteOficial, null, ESTADO_VALIDADO);
            this.cancelarComprobanteAsociado(comprobanteOficial);

            comprobanteOficial.setFecha_oficilizacion(new Date());
            comprobanteOficial.setImporte_adeudado(preComprobante.getImporte_total());
            comprobanteOficialDao.save(comprobanteOficial);
            afipConfigurations.saveSecuencia(preComprobante.getIdAfipTipoComprobante(), preComprobante.getPuntoVenta(),
                    preComprobante.getCuit_emisor(), proximoNumero);
            kafkaEventBuilder.publicarEventoOfi(comprobanteOficial);
            return this.buildResponse("OK", "00", "Solicitud de CAE correcta", comprobanteOficial, HttpStatus.OK);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error al solicitar el Cae en Afip");
        }
    }

    @Transactional
    public ResponseEntity<?> confirmarComprobanteAuxiliar(PreComprobante preComprobante, Long idComprobanteAsociado) {
        Optional<ComprobanteAuxiliar> optional = comprobanteAuxiliarDao.findByPreComprobante(preComprobante);
        ComprobanteAuxiliar comprobanteAuxiliar = optional.isPresent() ? optional.get() : new ComprobanteAuxiliar();
        comprobanteAuxiliar.setPreComprobante(preComprobante);

        if (idComprobanteAsociado != null) {
            Optional<ComprobanteAuxiliar> optionalAsociado = comprobanteAuxiliarDao.findById(idComprobanteAsociado);
            comprobanteAuxiliar.setComprobantesAsociado(optionalAsociado.get());
            boolean continuar = this.cancelarComprobanteAsociado(comprobanteAuxiliar);
            if (!continuar) {
                return this.buildResponse("nOk", "02", "El comprobante que intenta Cancelar "
                        + "ya se encuentra cancelado por otra entidad", comprobanteAuxiliar, HttpStatus.OK);
            }
        }

        if (comprobanteAuxiliar.getPreComprobante().getEstado().getId().equals(ESTADO_CONFIRMADO)) {
            return this.buildResponse("ok", "00", "Comprobante Ya Confirmado", comprobanteAuxiliar, HttpStatus.OK);
        }
        Integer proximoNumero = this.getUltimoNumeroSecuencia(preComprobante);
        if (proximoNumero == null) {
            return this.buildResponse("nok", "02", "No se pudo obtener el numero de secuencia", comprobanteAuxiliar, HttpStatus.NOT_FOUND);
        }
        comprobanteAuxiliar = comprobanteAuxiliarDao.save(comprobanteAuxiliar);
        comprobanteAuxiliar.setNumero_comprobante(comprobanteAuxiliar.getPreComprobante().getAbrebiaturaTipoComprobante()
                + " " + String.format("%05d", comprobanteAuxiliar.getPreComprobante().getPuntoVenta()).replace("0", "9")
                + " " + String.format("%08d", comprobanteAuxiliar.getId())
        );

        this.setEstadoComprobante(null, comprobanteAuxiliar, ESTADO_CONFIRMADO);
        comprobanteAuxiliar.setFecha_confirmacion(new Date());
        comprobanteAuxiliar.setImporte_adeudado(preComprobante.getImporte_total());
        comprobanteAuxiliar = comprobanteAuxiliarDao.save(comprobanteAuxiliar);
        kafkaEventBuilder.publicarEventoAux(comprobanteAuxiliar);
        if (comprobanteAuxiliar != null) {
            return this.buildResponse("OK", "00", "Se confirmo el comprobante", comprobanteAuxiliar, HttpStatus.OK);
        } else {
            return this.buildErrorResponse("Error", "-01", "Error al Confirmar el Comprobante");
        }
    }

    private Integer getUltimoNumeroSecuencia(PreComprobante preComprobante) {
        ResponseEntity<?> response2 = afipConfigurations.getLastNumber(preComprobante.getIdAfipTipoComprobante(), preComprobante.getPuntoVenta(), preComprobante.getCuit_emisor());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.convertValue(response2.getBody(), JsonNode.class);
        Integer numero = root.path("response").path("response").get(0).asInt();
        return numero;
    }

    private void buildNumeroComprobante(ComprobanteOficial comprobanteOficial) {
        comprobanteOficial.setNumero_comprobante(
                comprobanteOficial.getPreComprobante().getAbrebiaturaTipoComprobante()
                + " " + String.format("%05d", comprobanteOficial.getPreComprobante().getPuntoVenta())
                + " " + String.format("%08d", comprobanteOficial.getCbte_nro_desde()));
    }

    private void getCaeInformacion(FECAEResponse responseCAE, ComprobanteOficial comprobanteOficial) {
        comprobanteOficial.setCae_afip(responseCAE.getFeDetResp().getFECAEDetResponse().get(0).getCAE());
        comprobanteOficial.setVto_cae(responseCAE.getFeDetResp().getFECAEDetResponse().get(0).getCAEFchVto());
    }

    private void setEstadoComprobante(ComprobanteOficial comprobanteOficial, ComprobanteAuxiliar comprobanteAuxiliar, Long estado) {
        if (comprobanteOficial != null) {
            comprobanteOficial.getPreComprobante().setEstado(estadoComprobanteDao.findById(estado).get());
        }
        if (comprobanteAuxiliar != null) {
            comprobanteAuxiliar.getPreComprobante().setEstado(estadoComprobanteDao.findById(estado).get());
        }
    }

    private boolean cancelarComprobanteAsociado(ComprobanteOficial comprobante) {
        if (comprobante.getPreComprobante().getDescripcionTipoComprobante().contains("Crédito")
                && !comprobante.getPreComprobante().getEstado().getDescripcion().equals("Cancelado")) {

            Optional<ComprobanteOficial> comprobanteAsociado = comprobanteOficialDao.findById(comprobante.getComprobantesAsociado().getId());

            if (comprobanteAsociado.isPresent()) {
                if (comprobanteAsociado.get().getPreComprobante().getEstado().getDescripcion().equals("Cancelado")) {
                    return false;
                }
                this.setEstadoComprobante(comprobanteAsociado.get(), null, ESTADO_CANCELADO);
                comprobanteOficialDao.save(comprobanteAsociado.get());
//                kafkaEventBuilder.publicarEventoOfi(comprobante);
                return true;
            }
        }
        return false;
    }

    private boolean cancelarComprobanteAsociado(ComprobanteAuxiliar comprobante) {
        if (comprobante.getPreComprobante().getDescripcionTipoComprobante().contains("Crédito")
                && !comprobante.getPreComprobante().getEstado().getDescripcion().equals("Cancelado")) {

            Optional<ComprobanteAuxiliar> comprobanteAsociado = comprobanteAuxiliarDao.findById(comprobante.getComprobantesAsociado().getId());
            if (comprobanteAsociado.isPresent()) {
                if (comprobanteAsociado.get().getPreComprobante().getEstado().getDescripcion().equals("Cancelado")) {
                    return false;
                }
                this.setEstadoComprobante(null, comprobanteAsociado.get(), ESTADO_CANCELADO);
                comprobanteAuxiliarDao.save(comprobanteAsociado.get());
//                kafkaEventBuilder.publicarEventoAux(comprobante);
                return true;
            }
        }
        return false;
    }

    protected void setSaldado(ImporteAplicadoEvent ImporteAplicado) {
        if (ImporteAplicado.isOficial()) {
            Optional<ComprobanteOficial> comprobante = comprobanteOficialDao.findById(ImporteAplicado.getFacturaId());
            if (!comprobante.get().getPreComprobante().getContactoId().equals(ImporteAplicado.getClienteId())) {
                return;
            }

            comprobante.get().setImporte_adeudado(comprobante.get().getImporte_adeudado() - ImporteAplicado.getImporteAplicado()
            );

            if (comprobante.get().getImporte_adeudado() == 0) {
                comprobante.get().setSaldado(true);
            }
            comprobanteOficialDao.save(comprobante.get());

        } else {
            Optional<ComprobanteAuxiliar> comprobante = comprobanteAuxiliarDao.findById(ImporteAplicado.getFacturaId());
            if (!comprobante.get().getPreComprobante().getContactoId().equals(ImporteAplicado.getClienteId())) {
                return;
            }
            comprobante.get().setImporte_adeudado(comprobante.get().getImporte_adeudado() - ImporteAplicado.getImporteAplicado()
            );

            if (comprobante.get().getImporte_adeudado() == 0) {
                comprobante.get().setSaldado(true);
            }
            comprobanteAuxiliarDao.save(comprobante.get());
        }
    }
}
