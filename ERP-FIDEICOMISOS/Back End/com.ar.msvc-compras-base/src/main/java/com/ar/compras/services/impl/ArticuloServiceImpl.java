package com.ar.compras.services.impl;

import com.ar.compras.ClientsFeign.ClientFeignCuentasContables;
import com.ar.compras.DTOs.CuentaContableDTO;
import com.ar.compras.entities.Articulo;
import com.ar.compras.repositories.iArticuloDao;
import com.ar.compras.responses.BuildResponsesServicesImpl;
import com.ar.compras.services.iArticuloService;
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
public class ArticuloServiceImpl extends BuildResponsesServicesImpl implements iArticuloService {

    @Autowired
    private ClientFeignCuentasContables clientFeignCuentas;

    @Autowired
    private iArticuloDao articuloDao;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    public ResponseEntity<?> newArticulo(Articulo articulo) {
        try {
            if (articulo == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna informacion para Guardar", null, HttpStatus.BAD_REQUEST);
            }

            if (articulo.getIdCuentaContable() == null || articulo.getNroCuentaContable() == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede guardar un articulo sin cuenta Corriente", null, HttpStatus.BAD_REQUEST);
            }
            CuentaContableDTO cuenta = this.getCuentaContable(articulo.getIdCuentaContable());

            if (!Objects.equals(cuenta.getOficial(), articulo.getOficial())) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede Asociar  un articulo a una cuenta contable que no sea Oficial o Auxiliar como la caja", articulo, HttpStatus.BAD_REQUEST);
            }

            if (!cuenta.isActiva() || !cuenta.isConciliable()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede Asociar  un articulo a una cuenta contable Desactivada o no Conciliable", articulo, HttpStatus.BAD_REQUEST);
            }
            
            articulo.setIdCuentaContable(cuenta.getId());
            articulo.setNroCuentaContable(cuenta.getCodigo());
            articulo.setNombreCuentaContable(cuenta.getNombre());
            articulo = articuloDao.save(articulo);
            return this.buildResponse("ok", CODIGO_OK, "Se guardo el Articulo", articulo, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar un nuevo articulo");
        }
    }

    @Override
    public ResponseEntity<?> editArticulo(Articulo articulo) {
        try {
            Optional<Articulo> optional = articuloDao.findById(articulo.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro el Articulo para editar", null, HttpStatus.BAD_REQUEST);
            }
            articulo = this.buildEditArticulo(optional, articulo);
            articulo = articuloDao.save(articulo);
            return this.buildResponse("ok", CODIGO_OK, "Se Edito el Articulo", articulo, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Editar el Articulo");
        }
    }

    @Override
    public ResponseEntity<?> archivarDesarchivarArticulo(Long idArticulo) {
        try {
            Optional<Articulo> optional = articuloDao.findById(idArticulo);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro la Caja", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setArchivar(!optional.get().getArchivar());
            articuloDao.save(optional.get());
            return this.buildResponse("ok", "00", "Se Cambio el estado de la caja de Cobranza", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Cambiar el estado de Caja/Banco de cobranza");
        }
    }

    @Override
    public ResponseEntity<?> listarArticulos() {
        try {
            List<Articulo> list = articuloDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "SIn cajas Cargadas", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("ok", "00", "Lista de Cajas de Cobranza", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar Obtener la lista de Cajas/Bancos de cobranza");
        }
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

    private Articulo buildEditArticulo(Optional<Articulo> optional, Articulo articulo) {
        Articulo edit = optional.get();
        edit.setIdCuentaContable(articulo.getIdCuentaContable() == null ? edit.getIdCuentaContable() : articulo.getIdCuentaContable());
        edit.setOficial(articulo.getOficial() == null ? edit.getOficial() : articulo.getOficial());
        edit.setNombreArticulo(articulo.getNombreArticulo() == null ? edit.getNombreArticulo() : articulo.getNombreArticulo());
        edit.setNombreCuentaContable(articulo.getNombreCuentaContable() == null ? edit.getNombreCuentaContable() : articulo.getNombreCuentaContable());
        edit.setNroCuentaContable(articulo.getNroCuentaContable() == null ? edit.getNroCuentaContable() : articulo.getNroCuentaContable());
        edit.setTipoEgreso(articulo.getTipoEgreso() == null ? edit.getTipoEgreso() : articulo.getTipoEgreso());
        return edit;
    }
}
