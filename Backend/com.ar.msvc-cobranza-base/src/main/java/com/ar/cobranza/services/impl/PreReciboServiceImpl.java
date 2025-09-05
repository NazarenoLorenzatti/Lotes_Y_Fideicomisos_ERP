package com.ar.cobranza.services.impl;

import com.ar.cobranza.ClientsFeign.ClientFeignContactos;
import com.ar.cobranza.DTOs.ContactosDTO;
import com.ar.cobranza.entities.CajaCobranza;
import com.ar.cobranza.entities.EstadoRecibo;
import com.ar.cobranza.entities.ImputacionPreReciboCaja;
import com.ar.cobranza.entities.PreRecibo;
import com.ar.cobranza.repositories.iCajaCobranzaDao;
import com.ar.cobranza.responses.BuildResponsesServicesImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ar.cobranza.repositories.iPreReciboDao;
import com.ar.cobranza.repositories.iEstadoReciboDao;
import com.ar.cobranza.services.iPreReciboService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
public class PreReciboServiceImpl extends BuildResponsesServicesImpl implements iPreReciboService {

    @Autowired
    private iPreReciboDao preReciboDao;

    @Autowired
    private ClientFeignContactos contactoFeign;

    @Autowired
    private iCajaCobranzaDao cajaDao;

    @Autowired
    private iEstadoReciboDao estadoReciboDao;

    private final Long ESTADO_BORRADOR = 1L;
    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    public ResponseEntity<?> initPreRecibo(PreRecibo preRecibo, Long idContacto) {
        try {
            if (!this.checkStatusMsvcContacto()) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se puede acceder al Msvc del modulo de Contacto por que no esta disponible", null, HttpStatus.BAD_GATEWAY);
            }
            ContactosDTO contacto = getContacto(idContacto);
            if (preRecibo == null || contacto == null) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo Cargar el Comprobante, consulta Vacia", contacto, HttpStatus.BAD_REQUEST);
            }

            List<ImputacionPreReciboCaja> listImputaciones = new ArrayList();
            this.buildImputaciones(listImputaciones, preRecibo.getImputaciones(), preRecibo);
            if (listImputaciones.isEmpty()) {
                return this.buildResponse("nOk", CODIGO_NOK, "Esta intentando imputar un Recibo a una caja que no corresponde", preRecibo, HttpStatus.BAD_REQUEST);
            }

            return this.createAndSaveComprobante(preRecibo, contacto);
        } catch (Exception e) {
            return this.buildErrorResponse("Error", CODIGO_ERROR, "No se pudo Cargar el Comprobante, Ocurrio un error en el servidor");
        }
    }

    @Override
    public ResponseEntity<?> editPreRecibo(PreRecibo preRecibo) {
        try {
            if (preRecibo == null) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo Editar el Pre Comprobante,  consulta Vacia", preRecibo, HttpStatus.BAD_REQUEST);
            }
            Optional<PreRecibo> o = preReciboDao.findById(preRecibo.getId());
            if (!o.isPresent()) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo Editar el Pre Comprobante,  No se encontro con el ID informado", preRecibo, HttpStatus.BAD_REQUEST);
            }
            if (!o.get().getEstado().getId().equals(ESTADO_BORRADOR)) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se puede Editar el Pre Comprobante,  No esta en Borrador", o.get(), HttpStatus.BAD_REQUEST);
            }

            preRecibo = this.buildEditPreRecibo(o, preRecibo);
            preRecibo.setFechaModificacion(LocalDateTime.now());
            preRecibo = preReciboDao.save(preRecibo);

            if (preRecibo == null) {
                return this.buildErrorResponse("nOk", CODIGO_NOK, "No se pudo Editar el Pre Comprobante, ocurrio un error al intentar guardar");
            }
            return this.buildResponse("Ok", CODIGO_OK, "Se edito el comprobante", preRecibo, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar editar el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getPreRecibos() {
        try {
            List<PreRecibo> list = preReciboDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo obtener la lista", list, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", CODIGO_OK, "Lista de Pre Comprobantes", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getPreRecibosPorEstado(EstadoRecibo estado) {
        try {
            Optional<EstadoRecibo> o = estadoReciboDao.findById(estado.getId());
            if (o.isEmpty()) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo obtener el estado enviado", estado, HttpStatus.BAD_REQUEST);
            }
            List<PreRecibo> list = preReciboDao.findAllByEstado(o.get());
            if (list.isEmpty()) {
                return this.buildResponse("Ok", CODIGO_OK, "No se encuentran en Borrador", list, HttpStatus.OK);
            }
            return this.buildResponse("Ok", CODIGO_OK, "Lista de Pre Comprobantes", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getPreRecibo(Long id) {
        try {
            Optional<PreRecibo> o = preReciboDao.findById(id);
            if (o.isEmpty()) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se encuentran en Borrador", null, HttpStatus.NOT_FOUND);
            }
            return this.buildResponse("Ok", CODIGO_OK, "Pre Recibo Encontrado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> deletePreRecibo(Long idPreRecibo) {
        try {
            if (idPreRecibo == null) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo Editar el Pre Comprobante,  consulta Vacia", null, HttpStatus.BAD_REQUEST);
            }
            Optional<PreRecibo> o = preReciboDao.findById(idPreRecibo);
            if (!o.isPresent()) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo Eliminar el Pre Comprobante,  No se encontro con el ID informado", null, HttpStatus.BAD_REQUEST);
            }

            if (!o.get().getEstado().getId().equals(ESTADO_BORRADOR)) {
                return this.buildResponse("nOk", CODIGO_NOK, "No se pudo Eliminar el Pre Comprobante,  No esta en Borrador", o.get(), HttpStatus.BAD_REQUEST);
            }

            preReciboDao.delete(o.get());
            return this.buildResponse("Ok", CODIGO_OK, "Comprobante Eliminado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar eliminar el comprobante");
        }
    }


    /*Crear y guardar Pre Comprobante */
    @Transactional
    private ResponseEntity<?> createAndSaveComprobante(PreRecibo preRecibo, ContactosDTO contacto) {
        preRecibo.setRazon_social(contacto.getRazonSocial());
        preRecibo.setDireccion_fiscal(contacto.getDireccionFiscal());
        preRecibo.setEmail_envio(contacto.getEmail());
        preRecibo.setLocalidad(contacto.getLocalidad());
        preRecibo.setContactoId(contacto.getId());
        preRecibo.setNro_documento(Long.valueOf(contacto.getNumeroDocumento()));
        preRecibo.setCondicion_iva_receptor(contacto.getIdCondicionIva());
        preRecibo.setTipo_documento(contacto.getIdAfipTipoDocumento());
        preRecibo.setEstado(estadoReciboDao.findById(ESTADO_BORRADOR).get());
        preRecibo.setFechaCreacion(LocalDateTime.now());
        preRecibo.setFechaModificacion(LocalDateTime.now());
        preRecibo = preReciboDao.save(preRecibo);
        return preRecibo != null
                ? this.buildResponse("OK", CODIGO_OK, "Se Guardo Correctamente el PRE comprobante", preRecibo, HttpStatus.OK)
                : this.buildResponse("nOK", CODIGO_NOK, "No se Guardo el comprobante", preRecibo, HttpStatus.BAD_REQUEST);
    }

    /* Obtener Contacto de Modulo de Contacto y Parsealdo a ContactoDTO */
    private ContactosDTO getContacto(Long id) {
        try {
            ResponseEntity<?> response = contactoFeign.getClienteById(id);
            if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
                return null;
            }

            Map<String, Object> body = (Map<String, Object>) response.getBody();
            List<Map<String, Object>> metadataList = (List<Map<String, Object>>) body.get("metadata");
            if (metadataList == null || metadataList.isEmpty()) {
                return null;
            }

            String codigo = (String) metadataList.get(0).get("codigo");
            if (!CODIGO_OK.equals(codigo)) {
                return null;
            }

            Map<String, Object> bodyResponse = (Map<String, Object>) body.get("response");
            if (bodyResponse == null || bodyResponse.isEmpty()) {
                return null;
            }

            for (Entry<String, Object> entry : bodyResponse.entrySet()) {
                List<Map<String, Object>> responseList = (List<Map<String, Object>>) entry.getValue();
                if (responseList != null && !responseList.isEmpty()) {
                    Map<String, Object> contactoMap = responseList.get(0);
                    ObjectMapper mapper = new ObjectMapper();
                    ContactosDTO dto = mapper.convertValue(contactoMap, ContactosDTO.class);
                    return dto;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }

    }

    private void buildImputaciones(List<ImputacionPreReciboCaja> list, List<ImputacionPreReciboCaja> imputaciones, PreRecibo preRecibo) {
        for (ImputacionPreReciboCaja imp : imputaciones) {
            Optional<CajaCobranza> caja = cajaDao.findById(imp.getCajaCobranza().getId());
            if (caja.get().getOficial() == preRecibo.isOficial()) {
                imp.setCajaCobranza(caja.get());
                imp.setPreRecibo(preRecibo);
                imp.setFechaImputacion(LocalDateTime.now());
                imp.setFechaRealPago(imp.getFechaRealPago() != null ? imp.getFechaRealPago() : LocalDate.now());
                list.add(imp);
            }
        }
    }

    private boolean checkStatusMsvcContacto() {
        try {
            ResponseEntity<String> statusContactoMsvc = contactoFeign.getStatusMsvc();
            return statusContactoMsvc.getStatusCodeValue() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private PreRecibo buildEditPreRecibo(Optional<PreRecibo> o, PreRecibo preRecibo) {
        PreRecibo edit = o.get();
        edit.setLocalidad(preRecibo.getLocalidad() == null ? edit.getLocalidad() : preRecibo.getLocalidad());
        edit.setEmail_envio(preRecibo.getEmail_envio() == null ? edit.getEmail_envio() : preRecibo.getEmail_envio());
        edit.setDireccion_fiscal(preRecibo.getDireccion_fiscal() == null ? edit.getDireccion_fiscal() : preRecibo.getDireccion_fiscal());
        edit.setRazon_social(preRecibo.getRazon_social() == null ? edit.getRazon_social() : preRecibo.getRazon_social());
        edit.setFecha_desde(preRecibo.getFecha_desde() == null ? edit.getFecha_desde() : preRecibo.getFecha_desde());
        edit.setFecha_hasta(preRecibo.getFecha_hasta() == null ? edit.getFecha_hasta() : preRecibo.getFecha_hasta());
        edit.setFecha_vto(preRecibo.getFecha_vto() == null ? edit.getFecha_vto() : preRecibo.getFecha_vto());
        edit.setImporte_total(preRecibo.getImporte_total() == null ? edit.getImporte_total() : preRecibo.getImporte_total());
        edit.setImporte_neto(preRecibo.getImporte_neto() == null ? edit.getImporte_neto() : preRecibo.getImporte_neto());
        edit.setImporte_iva(preRecibo.getImporte_iva() == null ? edit.getImporte_iva() : preRecibo.getImporte_iva());
        edit.setImporte_gravado(preRecibo.getImporte_gravado() == null ? edit.getImporte_gravado() : preRecibo.getImporte_gravado());
        edit.setImporte_excento(preRecibo.getImporte_excento() == null ? edit.getImporte_excento() : preRecibo.getImporte_excento());
        edit.setImporte_no_gravado(preRecibo.getImporte_no_gravado() == null ? edit.getImporte_no_gravado() : preRecibo.getImporte_no_gravado());
        edit.setIdAfipTipoRecibo(preRecibo.getIdAfipTipoRecibo() == null ? edit.getIdAfipTipoRecibo() : preRecibo.getIdAfipTipoRecibo());
        edit.setRecibo_fecha(preRecibo.getRecibo_fecha() == null ? edit.getRecibo_fecha() : preRecibo.getRecibo_fecha());
        edit.setConcepto(preRecibo.getConcepto() == null ? edit.getConcepto() : preRecibo.getConcepto());
        edit.setDescripcionIva(preRecibo.getDescripcionIva() == null ? edit.getDescripcionIva() : preRecibo.getDescripcionIva());
        edit.setPuntoVenta(preRecibo.getPuntoVenta() == null ? edit.getPuntoVenta() : preRecibo.getPuntoVenta());
        return edit;
    }

}
