package com.ar.invoices.services.impl;

import com.ar.invoices.ClientsFeign.ClientFeignCuentasContables;
import com.ar.invoices.DTOs.CuentaContableDTO;
import com.ar.invoices.entities.Articulo;
import com.ar.invoices.repositories.iArticuloDao;
import com.ar.invoices.responses.BuildResponsesServicesImpl;
import com.ar.invoices.services.iArticuloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
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
    private iArticuloDao articuloDao;

    @Autowired
    private ClientFeignCuentasContables cuentasFeign;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    public ResponseEntity<?> guardarArticulo(Articulo art) {
        try {
            if (art == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo guardar el Articulo,  consulta Vacia", null, HttpStatus.BAD_REQUEST);
            }

            if (articuloDao.existsByCodigo(art.getCodigo()) || articuloDao.existsByDescripcion(art.getDescripcion())) {
                return this.buildResponse("nOK", CODIGO_NOK, "Ya existe un Articulo con ese codigo o descripcion", art, HttpStatus.BAD_REQUEST);
            }

            this.setCuentasArticulos(art);

            if (art.getNroCuentaContable() == null && art.getNroCuentaContableAux() == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se envio ninguna cuenta contable Asociada a este articulo", art, HttpStatus.BAD_REQUEST);
            }
            art = articuloDao.save(art);
            return this.buildResponse("Ok", CODIGO_OK, "Se guardo el nuevo articulo", art, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar el Articulo");
        }
    }

    @Override
    public ResponseEntity<?> editarArticulo(Articulo art) {
        try {
            if (art == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo guardar el Articulo,  consulta Vacia", null, HttpStatus.BAD_REQUEST);
            }

            Optional<Articulo> optional = articuloDao.findById(art.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo encontro el Articulo para editar", null, HttpStatus.BAD_REQUEST);
            }
            art = this.buildEditArticulo(optional, art);
            art = articuloDao.save(art);
            return this.buildResponse("Ok", CODIGO_NOK, "Se guardo el nuevo articulo", art, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar el Articulo");
        }
    }

    @Override
    public ResponseEntity<?> archivarArticulo(Long id) {
        try {
            Optional<Articulo> optional = articuloDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo encontro el Articulo para Archivar", null, HttpStatus.BAD_REQUEST);
            }
            if (optional.get().getArchivar()) {
                return this.buildResponse("nOK", CODIGO_NOK, "El articulo ya se encuentra archivado", optional.get(), HttpStatus.BAD_REQUEST);
            }
            optional.get().setArchivar(Boolean.TRUE);
            return this.buildResponse("Ok", CODIGO_NOK, "Se guardo el nuevo articulo", optional.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar el Articulo");
        }
    }

    @Override
    public ResponseEntity<?> obtenerArticulo(Long id) {
        try {
            Optional<Articulo> optional = articuloDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo encontro el Articulo para Archivar", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", CODIGO_NOK, "Articulo encontrado", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar el Articulo");
        }
    }

    @Override
    public ResponseEntity<?> listarArticulos() {
        try {
            List<Articulo> list = articuloDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se pudo encontraron articulos para listar", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("Ok", CODIGO_OK, "Lista de articulos", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar el Articulo");
        }
    }

    private void setCuentasArticulos(Articulo art) {
        if (art.getIdCuentaContable() != null) {
            CuentaContableDTO dto = this.getCuentaContable(art.getIdCuentaContable());
            art.setIdCuentaContable(dto.getId());
            art.setNroCuentaContable(dto.getCodigo());
            art.setNombreCuentaContable(dto.getNombre());
        }
        if (art.getIdCuentaContableAux() != null) {
            CuentaContableDTO dto = this.getCuentaContable(art.getIdCuentaContable());
            art.setIdCuentaContableAux(dto.getId());
            art.setNroCuentaContableAux(dto.getCodigo());
            art.setNombreCuentaContableAux(dto.getNombre());
        }

    }

    private Articulo buildEditArticulo(Optional<Articulo> optional, Articulo art) {
        Articulo edit = new Articulo();
        edit.setCodigo(art.getCodigo() == null ? optional.get().getCodigo() : art.getCodigo());
        edit.setDescripcion(art.getDescripcion() == null ? optional.get().getDescripcion() : art.getDescripcion());
        edit.setIva(art.getIva() == null ? optional.get().getIva() : art.getIva());
        edit.setDesIva(art.getDesIva() == null ? optional.get().getDesIva() : art.getDesIva());
        edit.setPrecioUnitario(art.getPrecioUnitario() == null ? optional.get().getPrecioUnitario() : art.getPrecioUnitario());
        edit.setPrecioUnitarioConIva(art.getPrecioUnitarioConIva() == null ? optional.get().getPrecioUnitarioConIva() : art.getPrecioUnitarioConIva());
        edit.setIdCuentaContable(art.getIdCuentaContable() == null ? optional.get().getIdCuentaContable() : art.getIdCuentaContable());
        edit.setIdCuentaContableAux(art.getIdCuentaContableAux() == null ? optional.get().getIdCuentaContableAux() : art.getIdCuentaContableAux());
        edit.setNombreCuentaContable(art.getNombreCuentaContable() == null ? optional.get().getNombreCuentaContable() : art.getNombreCuentaContable());
        edit.setNombreCuentaContableAux(art.getNombreCuentaContableAux() == null ? optional.get().getNombreCuentaContableAux() : art.getNombreCuentaContableAux());
        edit.setNroCuentaContable(art.getNroCuentaContable() == null ? optional.get().getNroCuentaContable() : art.getNroCuentaContable());
        edit.setNroCuentaContableAux(art.getNroCuentaContableAux() == null ? optional.get().getNroCuentaContableAux() : art.getNroCuentaContableAux());
        edit.setTipo(art.getTipo() == null ? optional.get().getTipo() : art.getTipo());
        return edit;
    }

    private CuentaContableDTO getCuentaContable(Long idCuentaContable) {
        try {
            ResponseEntity<?> response = cuentasFeign.obtenerCuenta(idCuentaContable);
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
