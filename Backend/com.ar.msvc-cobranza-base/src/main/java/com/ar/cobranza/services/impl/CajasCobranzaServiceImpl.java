package com.ar.cobranza.services.impl;

import com.ar.cobranza.ClientsFeign.ClientFeignCuentasContables;
import com.ar.cobranza.DTOs.CuentaContableDTO;
import com.ar.cobranza.entities.CajaCobranza;
import com.ar.cobranza.repositories.iCajaCobranzaDao;
import com.ar.cobranza.responses.BuildResponsesServicesImpl;
import com.ar.cobranza.services.iCajasCobranzaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CajasCobranzaServiceImpl extends BuildResponsesServicesImpl implements iCajasCobranzaService {

    @Autowired
    private iCajaCobranzaDao cajaCobranzaDao;

    @Autowired
    private ClientFeignCuentasContables clientFeignCuentas;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    public ResponseEntity<?> newCaja(CajaCobranza caja) {
        try {
            if (caja == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }

            if (caja.getIdCuentaContable() == null || caja.getNroCuentaContable() == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede guardar una caja De cobranza sin cuenta Corriente", null, HttpStatus.BAD_REQUEST);
            }
            CuentaContableDTO cuenta = this.getCuentaContable(caja.getIdCuentaContable());

            if (!Objects.equals(cuenta.getOficial(), caja.getOficial())) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede Asociar  una caja a una cuenta contable que no sea Oficial o Auxiliar como la caja", caja, HttpStatus.BAD_REQUEST);
            }

            if (!cuenta.isActiva() || !cuenta.isConciliable()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede Asociar  una caja a una cuenta contable Desactivada o no Conciliable", caja, HttpStatus.BAD_REQUEST);
            }

            caja.setIdCuentaContable(cuenta.getId());
            caja.setNroCuentaContable(cuenta.getCodigo());
            caja.setNombreCuentaContable(cuenta.getNombre());
            caja = cajaCobranzaDao.save(caja);
            return this.buildResponse("ok", CODIGO_OK, "Se guardo la nueva caja de Cobranza", caja, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar una nueva Caja/Banco de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> editCaja(CajaCobranza caja) {
        try {
            Optional<CajaCobranza> optional = this.findCaja(caja.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la Caja para editar", null, HttpStatus.BAD_REQUEST);
            }
            caja = this.buildEditCaja(optional, caja);
            caja = cajaCobranzaDao.save(caja);
            return this.buildResponse("ok", CODIGO_OK, "Se Edito la caja de Cobranza", caja, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Editar Caja/Banco de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> deleteCaja(Long idCaja) {
        try {
            Optional<CajaCobranza> optional = this.findCaja(idCaja);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la Caja para editar", null, HttpStatus.BAD_REQUEST);
            }

            if (!optional.get().getPreRecibosImputados().isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede eliminar una Caja con recibos imputados", null, HttpStatus.BAD_REQUEST);
            }

            cajaCobranzaDao.delete(optional.get());
            return this.buildResponse("ok", CODIGO_OK, "Se Edito la caja de Cobranza", optional.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Editar Caja/Banco de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> getCaja(Long idCaja) {
        try {
            Optional<CajaCobranza> optional = this.findCaja(idCaja);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la Caja", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("ok", "00", "Se Encontro la caja de Cobranza", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Obtener la Caja/Banco de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> cambiarEstadoCaja(Long idCaja) {
        try {
            Optional<CajaCobranza> optional = this.findCaja(idCaja);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la Caja", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setActiva(!optional.get().getActiva());
            cajaCobranzaDao.save(optional.get());
            return this.buildResponse("ok", "00", "Se Cambio el estado de la caja de Cobranza", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Cambiar el estado de Caja/Banco de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> listarCajas() {
        try {
            List<CajaCobranza> list = cajaCobranzaDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "SIn cajas Cargadas", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("ok", "00", "Lista de Cajas de Cobranza", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Obtener la lista de Cajas/Bancos de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> listarCajasCobranza() {
        try {
            List<CajaCobranza> list = cajaCobranzaDao.findAllByCajaCobranza(Boolean.TRUE);
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "SIn cajas Cargadas", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("ok", "00", "Lista de Cajas de Cobranza", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Obtener la lista de Cajas/Bancos de cobranza");
        }
    }

    private CajaCobranza buildEditCaja(Optional<CajaCobranza> o, CajaCobranza caja) {
        CajaCobranza edit = o.get();
        edit.setActiva(caja.getActiva() == null ? edit.getActiva() : caja.getActiva());
        edit.setCajaCobranza(caja.getCajaCobranza() == null ? edit.getCajaCobranza() : caja.getCajaCobranza());
        edit.setIdCuentaContable(caja.getIdCuentaContable() == null ? edit.getIdCuentaContable() : caja.getIdCuentaContable());
        edit.setOficial(caja.getOficial() == null ? edit.getOficial() : caja.getOficial());
        edit.setTipo(caja.getTipo() == null ? edit.getTipo() : caja.getTipo());
        return edit;
    }

    private Optional<CajaCobranza> findCaja(Long idCaja) {
        if (idCaja == null) {
            return Optional.empty();
        }
        return cajaCobranzaDao.findById(idCaja);
    }

    private CuentaContableDTO getCuentaContable(Long idCuentaContable) {
        try {
            ResponseEntity<?> response = clientFeignCuentas.obtenerCuenta(idCuentaContable);
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

            for (Map.Entry<String, Object> entry : bodyResponse.entrySet()) {
                List<Map<String, Object>> responseList = (List<Map<String, Object>>) entry.getValue();
                if (responseList != null && !responseList.isEmpty()) {
                    Map<String, Object> contactoMap = responseList.get(0);
                    ObjectMapper mapper = new ObjectMapper();
                    CuentaContableDTO dto = mapper.convertValue(contactoMap, CuentaContableDTO.class);
                    return dto;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
