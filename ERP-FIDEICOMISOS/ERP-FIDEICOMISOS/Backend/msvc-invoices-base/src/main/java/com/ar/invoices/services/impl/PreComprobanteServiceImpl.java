package com.ar.invoices.services.impl;

import com.ar.invoices.DTOs.ContactosDTO;
import com.ar.invoices.entities.PreComprobante;
import com.ar.invoices.repositories.*;
import com.ar.invoices.responses.BuildResponsesServicesImpl;
import com.ar.invoices.services.iPreComprobanteService;
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
import com.ar.invoices.ClientsFeign.ClientFeignContactos;
import com.ar.invoices.entities.Articulo;
import com.ar.invoices.entities.EstadoComprobante;
import com.ar.invoices.entities.ItemFacturado;

@Service
@Slf4j
public class PreComprobanteServiceImpl extends BuildResponsesServicesImpl implements iPreComprobanteService {

    @Autowired
    private iPreComprobanteDao preComprobanteDao;

    @Autowired
    private ClientFeignContactos clienteFeign;

    @Autowired
    private iEstadoComprobanteDao estadoComprobanteDao;

    @Autowired
    private iArticuloDao articuloDao;

    private final Long ESTADO_BORRADOR = 1L;
    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    public ResponseEntity<?> initPreComprobante(PreComprobante preComprobante, Long idContacto) {
        try {
            if (!this.checkStatusMsvcContacto()) {
                return this.buildResponse("Error", "02", "No se puede acceder al Msvc del modulo de Contacto por que no esta disponible", null, HttpStatus.BAD_REQUEST);
            }
            ContactosDTO contacto = getContacto(idContacto);
            if (preComprobante == null || contacto == null) {
                return this.buildResponse("Error", "02", "No se pudo Cargar el Comprobante,  consulta Vacia", contacto, HttpStatus.BAD_REQUEST);
            }

            return this.createAndSaveComprobante(preComprobante, contacto);
        } catch (Exception e) {
            return this.buildErrorResponse("Error", "-01", "No se pudo Cargar el Comprobante, Ocurrio un error en el servidor");
        }
    }

    @Override
    public ResponseEntity<?> editPreComprobante(PreComprobante preComprobante) {
        try {
            if (preComprobante == null) {
                return this.buildResponse("Error", "02", "No se pudo Editar el Pre Comprobante,  consulta Vacia", preComprobante, HttpStatus.BAD_REQUEST);
            }
            Optional<PreComprobante> o = preComprobanteDao.findById(preComprobante.getId());
            if (!o.isPresent()) {
                return this.buildResponse("Error", "02", "No se pudo Editar el Pre Comprobante,  No se encontro con el ID informado", preComprobante, HttpStatus.BAD_REQUEST);
            }
            if (!o.get().getEstado().getId().equals(ESTADO_BORRADOR)) {
                return this.buildResponse("Error", "02", "No se puede Editar el Pre Comprobante,  No esta en Borrador", o.get(), HttpStatus.BAD_REQUEST);
            }

            preComprobante = this.buildEditPreComprobante(o, preComprobante);
            preComprobante.setFechaModificacion(new Date());
            preComprobante = preComprobanteDao.save(preComprobante);

            if (preComprobante == null) {
                return this.buildErrorResponse("Error", "02", "No se pudo Editar el Pre Comprobante, ocurrio un error al intentar guardar");
            }
            return this.buildResponse("Ok", "00", "Se edito el comprobante", preComprobante, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar editar el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getPreComprobante(Long id) {
        try {
            Optional<PreComprobante> o = preComprobanteDao.findById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No se pudo obtener el PreComprobante con Id: " + id, null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", "00", "Pre Comprobante", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir EL Pre Comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getPreComprobantes(EstadoComprobante estado) {
        try {
            List<PreComprobante> list;
            if (estado != null) {
                Optional<EstadoComprobante> o = estadoComprobanteDao.findById(estado.getId());
                list = o.isPresent() ? preComprobanteDao.findAllByEstado(o.get()) : preComprobanteDao.findAll();
            } else {
                list = preComprobanteDao.findAll();
            }

            if (list.isEmpty()) {
                return this.buildResponse("Error", "02", "Lista Vacia", list, HttpStatus.OK);
            }
            return this.buildResponse("Ok", "00", "Lista de Pre Comprobantes", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar conseguir la lista de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> deletePreComprobante(Long idPreComprobante) {
        try {
            if (idPreComprobante == null) {
                return this.buildResponse("Error", "02", "No se pudo Editar el Pre Comprobante,  consulta Vacia", null, HttpStatus.BAD_REQUEST);
            }
            Optional<PreComprobante> o = preComprobanteDao.findById(idPreComprobante);
            if (!o.isPresent()) {
                return this.buildResponse("Error", "02", "No se pudo Eliminar el Pre Comprobante,  No se encontro con el ID informado", null, HttpStatus.BAD_REQUEST);
            }

            if (!o.get().getEstado().getId().equals(ESTADO_BORRADOR)) {
                return this.buildResponse("Error", "02", "No se pudo Eliminar el Pre Comprobante,  No esta en Borrador", o.get(), HttpStatus.BAD_REQUEST);
            }

            preComprobanteDao.delete(o.get());
            return this.buildResponse("Ok", "00", "Comprobante Eliminado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Error en el Servidor al intentar eliminar el comprobante");
        }
    }


    /*Crear y guardar Pre Comprobante */
    @Transactional
    private ResponseEntity<?> createAndSaveComprobante(PreComprobante preComprobante, ContactosDTO contacto) {

        preComprobante.setRazon_social(contacto.getRazonSocial());
        preComprobante.setDireccion_fiscal(contacto.getDireccionFiscal());
        preComprobante.setEmail_envio(contacto.getEmail());
        preComprobante.setLocalidad(contacto.getLocalidad());
        preComprobante.setContactoId(contacto.getId());
        preComprobante.setNro_documento(Long.valueOf(contacto.getNumeroDocumento()));
        preComprobante.setCondicion_iva_receptor(contacto.getIdCondicionIva());
        preComprobante.setTipo_documento(contacto.getIdAfipTipoDocumento());
        preComprobante.setEstado(estadoComprobanteDao.findById(ESTADO_BORRADOR).get());
        preComprobante.setFechaCreacion(new Date());
        preComprobante.setFechaModificacion(new Date());

        for (ItemFacturado item : preComprobante.getItems()) {
            Articulo art = null;
            if (item.getArticulo().getId() != 999999) {
                Optional<Articulo> o = articuloDao.findById(item.getArticulo().getId());
                if (o.isEmpty()) {
                    return this.buildResponse("nOK", "02", "No se encontro uno de los articulos cargados", preComprobante, HttpStatus.BAD_REQUEST);
                }
                art = o.get();
                if (preComprobante.isOficial() && o.get().getIdCuentaContable() == null) {
                    return this.buildResponse("nOK", "02", "No se puede guardar el Pre Comprobante, "
                            + "el mismo fue marcado como oficial pero uno de los articulos "
                            + "no tiene asociada una cuenta corriente oficial", preComprobante, HttpStatus.BAD_REQUEST);
                }

                if (!preComprobante.isOficial() && o.get().getIdCuentaContableAux() == null) {
                    return this.buildResponse("nOK", "02", "No se puede guardar el Pre Comprobante, "
                            + "el mismo fue marcado como no oficial pero uno de los articulos "
                            + "no tiene asociada una cuenta corriente no oficial", preComprobante, HttpStatus.BAD_REQUEST);
                }
            }
            
            item.setArticulo(art);
        }
        log.info("pre comprobante{}", preComprobante);
        preComprobante = preComprobanteDao.save(preComprobante);
        return preComprobante != null
                ? this.buildResponse("OK", "00", "Se Guardo Correctamente el PRE comprobante", preComprobante, HttpStatus.OK)
                : this.buildResponse("nOK", "02", "No se Guardo el comprobante", preComprobante, HttpStatus.BAD_REQUEST);
    }

    /* Obtener Contacto de Modulo de Contacto y Parsealdo a ContactoDTO */
    private ContactosDTO getContacto(Long id) {
        try {
            ResponseEntity<?> response = clienteFeign.getClienteById(id);
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

    private boolean checkStatusMsvcContacto() {
        try {
            ResponseEntity<String> statusContactoMsvc = clienteFeign.getStatusMsvc();
            return statusContactoMsvc.getStatusCodeValue() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private PreComprobante buildEditPreComprobante(Optional<PreComprobante> o, PreComprobante preComprobante) {
        PreComprobante edit = o.get();
        edit.setLocalidad(preComprobante.getLocalidad() == null ? o.get().getLocalidad() : preComprobante.getLocalidad());
        edit.setEmail_envio(preComprobante.getEmail_envio() == null ? o.get().getEmail_envio() : preComprobante.getEmail_envio());
        edit.setDireccion_fiscal(preComprobante.getDireccion_fiscal() == null ? o.get().getDireccion_fiscal() : preComprobante.getDireccion_fiscal());
        edit.setRazon_social(preComprobante.getRazon_social() == null ? o.get().getRazon_social() : preComprobante.getRazon_social());
        edit.setFecha_desde(preComprobante.getFecha_desde() == null ? o.get().getFecha_desde() : preComprobante.getFecha_desde());
        edit.setFecha_hasta(preComprobante.getFecha_hasta() == null ? o.get().getFecha_hasta() : preComprobante.getFecha_hasta());
        edit.setFecha_vto(preComprobante.getFecha_vto() == null ? o.get().getFecha_vto() : preComprobante.getFecha_vto());
        edit.setImporte_total(preComprobante.getImporte_total() == null ? o.get().getImporte_total() : preComprobante.getImporte_total());
        edit.setImporte_neto(preComprobante.getImporte_neto() == null ? o.get().getImporte_neto() : preComprobante.getImporte_neto());
        edit.setImporte_iva(preComprobante.getImporte_iva() == null ? o.get().getImporte_iva() : preComprobante.getImporte_iva());
        edit.setImporte_gravado(preComprobante.getImporte_gravado() == null ? o.get().getImporte_gravado() : preComprobante.getImporte_gravado());
        edit.setImporte_excento(preComprobante.getImporte_excento() == null ? o.get().getImporte_excento() : preComprobante.getImporte_excento());
        edit.setImporte_no_gravado(preComprobante.getImporte_no_gravado() == null ? o.get().getImporte_no_gravado() : preComprobante.getImporte_no_gravado());
        edit.setIdAfipTipoComprobante(preComprobante.getIdAfipTipoComprobante() == null ? o.get().getIdAfipTipoComprobante() : preComprobante.getIdAfipTipoComprobante());
        edit.setCbte_fecha(preComprobante.getCbte_fecha() == null ? o.get().getCbte_fecha() : preComprobante.getCbte_fecha());
        edit.setConcepto(preComprobante.getConcepto() == null ? o.get().getConcepto() : preComprobante.getConcepto());
        edit.setPuntoVenta(preComprobante.getPuntoVenta() == null ? o.get().getPuntoVenta() : preComprobante.getPuntoVenta());
        return edit;
    }

}
