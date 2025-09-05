package com.ar.base.services.impl;

import com.ar.base.DTOs.CuentaContableDTO;
import com.ar.base.entities.CuentaContable;
import com.ar.base.repositories.iCuentaContableDao;
import com.ar.base.repositories.iMovimientoContableDao;
//import com.ar.base.repositories.iDetalleAsientoDao;
import com.ar.base.responses.BuildResponsesServicesImpl;
import com.ar.base.services.iCuentaContableService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CuentaContableServiceImpl extends BuildResponsesServicesImpl implements iCuentaContableService {

    @Autowired
    private iCuentaContableDao cuentaContableDao;
    
    @Autowired
    private iMovimientoContableDao movimientoDao;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    @Transactional
    public ResponseEntity<?> guardarCuentaContable(CuentaContable cuentaContable) {
        try {
            if (cuentaContable == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            if (cuentaContable.getCodigo() == null || cuentaContable.getNombre() == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede guardar una cuenta contable sin Codigo o Nombre", null, HttpStatus.BAD_REQUEST);
            }
            this.setCuentaContableHija(cuentaContable);
            this.cuentaContableDao.save(cuentaContable);
            return this.buildResponse("OK", CODIGO_OK, "Cuenta Contable Guardad", cuentaContable, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> editarCuentaContable(CuentaContable cuentaContable) {
        try {
            if (cuentaContable == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            Optional<CuentaContable> optional = cuentaContableDao.findById(cuentaContable.getId());
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro una cuenta contable con el ID informado", null, HttpStatus.BAD_REQUEST);
            }
            cuentaContable = this.buildCuentaContableEdit(optional, cuentaContable);
            cuentaContable = cuentaContableDao.save(cuentaContable);
            return this.buildResponse("OK", CODIGO_OK, "Cuenta Contable Guardad", cuentaContable, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> eliminarCuentaContable(Long id) {
        try {
            Optional<CuentaContable> optional = cuentaContableDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro una cuenta contable con el ID informado", null, HttpStatus.BAD_REQUEST);
            }
            if (optional.get().getHijas().isEmpty() || movimientoDao.existsByCuenta(optional.get())) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se puede Eliminar una cuenta contable con Una cuenta Hija o con Movimientos Registrados", null, HttpStatus.BAD_REQUEST);
            }
            cuentaContableDao.delete(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Cuenta Contable Eliminada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> cambiarEstadoCuentaContable(Long id) {
        try {
            Optional<CuentaContable> optional = cuentaContableDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro una cuenta contable con el ID informado", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setActiva(!optional.get().isActiva());
            cuentaContableDao.save(optional.get());
            return this.buildResponse("OK", CODIGO_OK, "Cuenta Contable Eliminada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getCuentaContable(Long id) {
        try {
            Optional<CuentaContable> optional = cuentaContableDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontro una cuenta contable con el ID informado", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Cuenta Contable Encontrada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir la cuenta contable");
        }
    }

    @Override
    public ResponseEntity<?> listarCuentasContables() {
        try {
            List<CuentaContable> todas = cuentaContableDao.findAll();

            // Paso 1: identificar IDs de todas las cuentas que son hijas
            Set<Long> idsHijas = todas.stream()
                    .flatMap(c -> c.getHijas() != null ? c.getHijas().stream() : Stream.empty())
                    .map(CuentaContable::getId)
                    .collect(Collectors.toSet());

            // Paso 2: filtrar solo las que no son hijas de nadie (raíz)
            List<CuentaContable> cuentasRaiz = todas.stream()
                    .filter(c -> !idsHijas.contains(c.getId()))
                    .collect(Collectors.toList());

            // Paso 3: mapear recursivamente a DTO
            List<CuentaContableDTO> dtoList = cuentasRaiz.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            return this.buildResponse("OK", CODIGO_OK, "Lista de Cuentas Contables sin duplicados", dtoList, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar listar las cuentas");
        }
    }

    private CuentaContable buildCuentaContableEdit(Optional<CuentaContable> optional, CuentaContable cuentaContable) {
        CuentaContable edit = optional.get();
        edit.setNombre(cuentaContable.getNombre() == null ? edit.getNombre() : cuentaContable.getNombre());
        return edit;
    }

    private void setCuentaContableHija(CuentaContable cuentaHija) {
        if (cuentaHija.getPadre() == null) {
            return;
        }

        Optional<CuentaContable> cuentaPadre = cuentaContableDao.findById(cuentaHija.getPadre().getId());
        if (cuentaPadre.isEmpty()) {
            return;
        }

        List<CuentaContable> list = cuentaPadre.get().getHijas();
        list.add(cuentaHija);
        cuentaPadre.get().setHijas(list);
//        cuentaContableDao.save(cuentaPadre.get());
        cuentaHija.setPadre(cuentaPadre.get());
        cuentaHija.setHijas(null);
    }

    private CuentaContableDTO mapToDTO(CuentaContable entity) {
        CuentaContableDTO dto = new CuentaContableDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setTipo(entity.getTipo());
        dto.setActiva(entity.isActiva());
        dto.setConciliable(entity.isConciliable());
        dto.setOficial(entity.isOficial());

        if (entity.getHijas() != null && !entity.getHijas().isEmpty()) {
            List<CuentaContableDTO> hijasDTO = entity.getHijas().stream()
                    .map(this::mapToDTO) // recursivo
                    .collect(Collectors.toList());
            dto.setHijas(hijasDTO);
        } else {
            dto.setHijas(Collections.emptyList());
        }

        return dto;
    }

}
