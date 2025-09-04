package com.ar.base.services.impl;

import com.ar.base.entities.*;
import com.ar.base.entities.AsientoContable.Estado;
import com.ar.base.entities.AsientoContable.TipoAsiento;
import com.ar.base.repositories.*;
import com.ar.base.responses.BuildResponsesServicesImpl;
import com.ar.base.services.iAsientoContableService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsientosContablesServiceImpl extends BuildResponsesServicesImpl implements iAsientoContableService {

    @Autowired
    private iAsientoContableDao asientosDao;

    @Autowired
    private iMovimientoContableDao movimientosDao;

    @Autowired
    private iConciliacionContableDao conciliacionDao;

    @Autowired
    private iCuentaContableDao cuentaContableDao;

    @Autowired
    private CheckCuentaPorCobrarServiceImpl cuentasPorCobrar;

    @Autowired
    private CheckCuentaPorPagarServiceImpl cuentasPorPagar;

    @Autowired
    private iDetalleConciliacionDao detalleConciliacionDao;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    private static final Long ENTIDAD_CLIENTE = 1L;
    private static final Long ENTIDAD_PROVEEDOR = 2L;

    /**
     * Registrar asiento solo para Comprobantes tipo Facturas, recibos y notas
     * de debito que no implican una Reversion de un movimiento
     *
     * @param asientoContable
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<?> registrarAsiento(AsientoContable asientoContable) {
        try {
            if (asientoContable == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            asientoContable.setFecha(LocalDate.now());
            for (MovimientoContable mov : asientoContable.getMovimientos()) {
                CuentaContable cuentaOrigen = cuentaContableDao.findById(mov.getCuenta().getId())
                        .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

                mov.setCuenta(cuentaOrigen);
                mov.setAsiento(asientoContable);
                mov.setCuenta(mov.getCuenta());
            }
            asientoContable = this.asientosDao.save(asientoContable);
            ConciliacionContable conciliacion = this.intentarConciliacionAutomatica(asientoContable);
            asientoContable.setConciliacion(conciliacion);
            asientoContable.setEstado(Estado.CONCILIADO);
            asientoContable = this.asientosDao.save(asientoContable);
            if (asientoContable.getTipoEntidad().getId().equals(ENTIDAD_CLIENTE)) {
                this.checkCuentaPorCobrar(asientoContable);
            } else if (asientoContable.getTipoEntidad().getId().equals(ENTIDAD_PROVEEDOR)) {
                this.checkCuentaPorPagar(asientoContable);
            }

            return this.buildResponse("OK", CODIGO_OK, "Asiento contable Registrado", asientoContable, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error registrando asiento", e);
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    /**
     * Registrar una reversion de un asiento por medio de un contracomprobante
     * como puede ser nota de credito y/o contrarecibo
     *
     * @param id
     * @param asientoReversion
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<?> registrarAsientoReversion(Long id, AsientoContable asientoReversion) {
        try {
            Optional<AsientoContable> optional = asientosDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }

            if (optional.get().getEstado() == Estado.ANULADO) {
                return this.buildResponse("nOK", CODIGO_NOK, "El asiento Ya se encuentra Revertido", null, HttpStatus.BAD_REQUEST);
            }

            if (optional.get().getConciliacion() != null) {
                this.anularConciliacion(optional.get().getConciliacion().getId());
            }

            this.buildReversion(optional.get(), asientoReversion);
            asientoReversion = this.asientosDao.save(asientoReversion);
            asientoReversion.setTipoAsiento(TipoAsiento.ASIENTO_REVERSION);
            ConciliacionContable conciliacion = this.conciliarAsientoReversado(asientoReversion);
            if (conciliacion == null) {
                return this.buildResponse("nOK", CODIGO_NOK, "El asiento no se pudo revertir", null, HttpStatus.BAD_REQUEST);
            }
            asientoReversion.setConciliacion(conciliacion);
            asientoReversion = this.asientosDao.save(asientoReversion);
            if (asientoReversion.getTipoEntidad().getId().equals(ENTIDAD_CLIENTE)) {
                this.checkCuentaPorCobrar(asientoReversion);
            } else if (asientoReversion.getTipoEntidad().getId().equals(ENTIDAD_PROVEEDOR)) {
                this.checkCuentaPorPagar(asientoReversion);
            }
            asientoReversion.getAsientoOrigen().setEstado(Estado.ANULADO);
            asientoReversion.getAsientoOrigen().setAnulado_por(asientoReversion.getReferenciaExterna());
            asientosDao.save(asientoReversion.getAsientoOrigen());
            return this.buildResponse("OK", CODIGO_OK, "Asiento de Reversion Guardada", asientoReversion, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getAsiento(Long id) {
        try {
            Optional<AsientoContable> optional = asientosDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Asiento Contable Encontrada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el Asiento contable");
        }
    }

    @Override
    public ResponseEntity<?> listarAsientos() {
        try {
            List<AsientoContable> list = asientosDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.OK);
            }
            return this.buildResponse("OK", CODIGO_OK, "Asiento Contable Encontrada", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el Movimiento contable");
        }
    }

    @Override
    public ResponseEntity<?> getMovimiento(Long id) {
        try {
            Optional<MovimientoContable> optional = movimientosDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "movimiento Contable Encontrada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el Movimiento contable");
        }
    }

    @Override
    public ResponseEntity<?> listarMovimientos() {
        try {
            List<MovimientoContable> list = movimientosDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "movimientos Contables Encontrados", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir los Movimientos contables");
        }
    }

    @Override
    public ResponseEntity<?> listarMovimientosPorAsiento(Long idAsiento) {
        try {
            List<MovimientoContable> list = movimientosDao.findByAsientoId(idAsiento);
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "movimientos Contables Encontrados", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir los Movimientos contables");
        }
    }

    @Override
    public ResponseEntity<?> getConciliacion(Long id) {
        try {
            Optional<ConciliacionContable> optional = conciliacionDao.findById(id);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Conciliacion Contable Encontrada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir la Conciliacion contable");
        }
    }

    @Override
    public ResponseEntity<?> listarConciliaciones() {
        try {
            List<ConciliacionContable> list = conciliacionDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Conciliaciones Contables Encontradas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir los Conciliaciones contables");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<?> anularConciliacion(Long conciliacionId) {
        try {
            Optional<ConciliacionContable> optional = conciliacionDao.findById(conciliacionId);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }
            optional.get().setEstado(ConciliacionContable.Estado.ANULADA);
            for (DetalleConciliacion detalle : optional.get().getDetalles()) {
                detalle.setEstado(DetalleConciliacion.Estado.ANULADA);
                detalle.getMovimiento().setConciliado(false);
            }
            return this.buildResponse("OK", CODIGO_OK, "Conciliacion Anulada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir los Conciliaciones contables");
        }
    }

    @Override
    public ResponseEntity<?> buscarConciliacionPorMoviento(Long idMovimiento) {
        try {
            List<DetalleConciliacion> list = detalleConciliacionDao.findByMovimientoId(idMovimiento);
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", list, HttpStatus.OK);
            }
            return this.buildResponse("OK", CODIGO_OK, "Detalles de conciliacion", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir los Movimientos contables");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> conciliarMovimientos(List<Long> movimientosIds, String tipoOperacion, String descripcion) {
        try {
            if (movimientosIds.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "Request Vacia", null, HttpStatus.BAD_REQUEST);
            }

            List<MovimientoContable> movimientos = movimientosDao.findAllById(movimientosIds);

            // Validaciones
            Long cuentaId = movimientos.get(0).getCuenta().getId();
            boolean mismaCuenta = movimientos.stream()
                    .allMatch(m -> m.getCuenta().getId().equals(cuentaId));

            if (!mismaCuenta) {
                return this.buildResponse("nOK", CODIGO_NOK, "Los movimientos deben Pertenecer a la misma cuenta", null, HttpStatus.BAD_REQUEST);
            }

            double saldo = movimientos.stream()
                    .mapToDouble(m -> m.getDebe() - m.getHaber())
                    .sum();

            if (Math.abs(saldo) > 0.01) {
                return this.buildResponse("nOK", CODIGO_NOK, "Los movimientos no se compensan entre si", null, HttpStatus.BAD_REQUEST);
            }

            // Crear conciliación
            ConciliacionContable conciliacion = new ConciliacionContable();
            conciliacion.setFecha(LocalDateTime.now());
            conciliacion.setEstado(ConciliacionContable.Estado.ACTIVA);
            conciliacion.setTipoOperacion(tipoOperacion);
            conciliacion.setDescripcion(descripcion);

            List<DetalleConciliacion> detalles = movimientos.stream().map(mov -> {
                DetalleConciliacion d = new DetalleConciliacion();
                d.setMovimiento(mov);
                d.setImporte(Math.abs(mov.getDebe() - mov.getHaber())); // o mov.getDebe() + mov.getHaber()
                d.setConciliacion(conciliacion);
                mov.setConciliado(true);
                return d;
            }).collect(Collectors.toList());

            conciliacion.setDetalles(detalles);
            conciliacion.getDetalles().get(0).getMovimiento().getAsiento().setEstado(AsientoContable.Estado.CONCILIADO);
            conciliacionDao.save(conciliacion);

            return this.buildResponse("OK", CODIGO_OK, "Movimientos Conciliados", conciliacion, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    public void buildReversion(AsientoContable asientoOrigen, AsientoContable asientoReversion) {
        asientoReversion.setFecha(LocalDate.now());
        asientoReversion.setDescripcion("Reversión de " + asientoOrigen.getDescripcion());
        asientoReversion.setReferenciaExterna("REV-" + asientoOrigen.getReferenciaExterna());
        asientoReversion.setEstado(Estado.CONCILIADO);
        asientoReversion.setContactoId(asientoOrigen.getContactoId());
        asientoReversion.setTipoEntidad(asientoOrigen.getTipoEntidad());
        asientoReversion.setAsientoOrigen(asientoOrigen);  // si hay relación bidireccional

        List<MovimientoContable> movimientoReversion = new ArrayList<>();
        for (MovimientoContable movOriginal : asientoOrigen.getMovimientos()) {
            MovimientoContable movNuevo = new MovimientoContable();
            movNuevo.setCuenta(movOriginal.getCuenta());
            movNuevo.setDebe(movOriginal.getHaber()); // Invertir
            movNuevo.setHaber(movOriginal.getDebe()); // Invertir
            movNuevo.setAsiento(asientoReversion);
            movNuevo.setTipoEntidad(asientoOrigen.getTipoEntidad());
            movNuevo.setEntidadId(asientoOrigen.getContactoId());
            movimientoReversion.add(movNuevo);
        }

        asientoReversion.setMovimientos(movimientoReversion);

    }

    @Transactional
    private ConciliacionContable intentarConciliacionAutomatica(AsientoContable asiento) {
        List<DetalleConciliacion> detallesConciliacion = new ArrayList<>();
        for (MovimientoContable nuevoMov : asiento.getMovimientos()) {
            if (!nuevoMov.getCuenta().isConciliable() || nuevoMov.getAsiento().getEstado() == Estado.ANULADO) {
                continue;
            }
            boolean nuevoEsDebe = nuevoMov.getDebe() > 0;
            Double importe = nuevoMov.getDebe() > 0 ? nuevoMov.getDebe() : nuevoMov.getHaber();
            List<MovimientoContable> candidatos = movimientosDao
                    .findByCuentaAndEntidadIdAndConciliado(nuevoMov.getCuenta(), nuevoMov.getEntidadId(), false)
                    .stream()
                    .filter(m -> m.getAsiento().getEstado() != Estado.ANULADO)
                    .collect(Collectors.toList());

            for (MovimientoContable candidato : candidatos) {
                boolean candidatoEsDebe = candidato.getDebe() > 0;
                // ❗ Solo conciliá si son opuestos
                if (nuevoEsDebe == candidatoEsDebe) {
                    continue;
                }

                Double importeCandidato = candidato.getDebe() > 0 ? candidato.getDebe() : candidato.getHaber();

                if (Objects.equals(importeCandidato, importe)) {
                    ConciliacionContable conciliacion = new ConciliacionContable();
                    conciliacion.setFecha(LocalDateTime.now());
                    conciliacion.setTipoOperacion("AUTO");
                    conciliacion.setDescripcion("Conciliación automática");
                    conciliacion.setEstado(ConciliacionContable.Estado.REALIZADA);

                    DetalleConciliacion d1 = new DetalleConciliacion();
                    d1.setConciliacion(conciliacion);
                    d1.setImporte(importe);
                    d1.setMovimiento(candidato); // movimiento ya existente, sin conciliar
                    d1.setEstado(DetalleConciliacion.Estado.ACTIVA);

                    DetalleConciliacion d2 = new DetalleConciliacion();
                    d2.setConciliacion(conciliacion);
                    d2.setImporte(importe);
                    d2.setMovimiento(nuevoMov); // ❗ este es el nuevo movimiento que estás registrando ahora
                    d2.setEstado(DetalleConciliacion.Estado.ACTIVA);

                    candidato.setConciliado(true);
                    nuevoMov.setConciliado(true); // marcás ambos como conciliado
                    detallesConciliacion.add(d1);
                    detallesConciliacion.add(d2);

                    for (DetalleConciliacion d : detallesConciliacion) {
                        d.getMovimiento().getAsiento().setEstado(AsientoContable.Estado.CONCILIADO);
                    }
                    conciliacion.setDetalles(detallesConciliacion);
                    conciliacion.setOriginada_por(asiento.getReferenciaExterna());
                    conciliacion = conciliacionDao.save(conciliacion);
                    return conciliacion;
                }
            }
        }
        return null;
    }

    @Transactional
    public ConciliacionContable conciliarAsientoReversado(AsientoContable asientoReversion) {
        AsientoContable asientoOriginal = asientoReversion.getAsientoOrigen();
        if (asientoOriginal == null || asientoOriginal.getEstado() == Estado.ANULADO || asientoReversion.getEstado() == Estado.ANULADO) {
            log.warn("No se puede conciliar: asiento origen nulo o alguno está anulado.");
            return null;
        }

        List<MovimientoContable> movimientosOriginal = asientoOriginal.getMovimientos();
        List<MovimientoContable> movimientosReversion = asientoReversion.getMovimientos();

        if (movimientosOriginal.size() != movimientosReversion.size()) {
            log.warn("No se puede conciliar: distinto número de movimientos entre original y reversión.");
            return null;
        }

        List<DetalleConciliacion> detallesConciliacion = new ArrayList<>();

        for (int i = 0; i < movimientosOriginal.size(); i++) {
            MovimientoContable original = movimientosOriginal.get(i);
            MovimientoContable reverso = movimientosReversion.get(i);

            boolean cuentasIguales = Objects.equals(original.getCuenta().getId(), reverso.getCuenta().getId());
            boolean montosInvertidos
                    = Objects.equals(original.getDebe(), reverso.getHaber())
                    && Objects.equals(original.getHaber(), reverso.getDebe());

            if (!cuentasIguales || !montosInvertidos) {
                log.warn("No se puede conciliar: movimientos no coinciden (cuenta o importe invertido)");
                return null;
            }

            // Buscar los movimientos contables asociados
            Optional<MovimientoContable> movOriginal = movimientosDao.findByAsientoAndCuenta(asientoOriginal, original.getCuenta());
            Optional<MovimientoContable> movReverso = movimientosDao.findByAsientoAndCuenta(asientoReversion, reverso.getCuenta());

            if (movOriginal.isEmpty() || movReverso.isEmpty()) {
                log.warn("No se puede conciliar: no se encontraron movimientos contables asociados.");
                return null;
            }

            // Crear los detalles de conciliación
            Double importe = original.getDebe() > 0 ? original.getDebe() : original.getHaber();

            DetalleConciliacion d1 = new DetalleConciliacion();
            d1.setMovimiento(movOriginal.get());
            d1.setImporte(importe);

            DetalleConciliacion d2 = new DetalleConciliacion();
            d2.setMovimiento(movReverso.get());
            d2.setImporte(importe);

            movOriginal.get().setConciliado(true);
            movReverso.get().setConciliado(true);

            detallesConciliacion.add(d1);
            detallesConciliacion.add(d2);
        }

        // Crear la conciliación principal
        ConciliacionContable conciliacion = new ConciliacionContable();
        conciliacion.setFecha(LocalDateTime.now());
        conciliacion.setDescripcion("Conciliación por reversión de asiento " + asientoOriginal.getId());
        conciliacion.setTipoOperacion("REVERSION");
        conciliacion.setEstado(ConciliacionContable.Estado.REALIZADA);

        for (DetalleConciliacion d : detallesConciliacion) {
            d.setConciliacion(conciliacion);
        }

        this.anularConciliacionOrigen(asientoOriginal, asientoReversion);

        conciliacion.setDetalles(detallesConciliacion);
        conciliacion = conciliacionDao.save(conciliacion);
        asientoOriginal.getReversiones().add(asientoReversion);
        log.info("Conciliación realizada entre asiento {} y su reversión {}", asientoOriginal.getId(), asientoReversion.getId());
        asientoOriginal.setConciliacion(conciliacion);
        return conciliacionDao.save(conciliacion);
    }

    private void anularConciliacionOrigen(AsientoContable asientoOriginal, AsientoContable asientoReversion) {

        if (asientoOriginal.getConciliacion() == null) {
            return;
        }

        AsientoContable asientoAnteriorConciliado = null;
        ConciliacionContable conciliacion = asientoOriginal.getConciliacion();
        conciliacion.setEstado(ConciliacionContable.Estado.ANULADA);
        for (DetalleConciliacion det : conciliacion.getDetalles()) {
            det.setEstado(DetalleConciliacion.Estado.ANULADA);
            MovimientoContable mov = det.getMovimiento();
            mov.setConciliado(false);
            if (!Objects.equals(mov.getAsiento().getId(), asientoOriginal.getId())) {
                asientoAnteriorConciliado = mov.getAsiento();
            }
            movimientosDao.save(mov);
        }

        if (asientoAnteriorConciliado != null) {
            conciliacionDao.delete(asientoAnteriorConciliado.getConciliacion());
            asientoAnteriorConciliado.setEstado(asientoAnteriorConciliado.getEstado() != Estado.ANULADO ? Estado.APROBADO : Estado.ANULADO);
            asientoAnteriorConciliado.getMovimientos().forEach(m -> m.setConciliado(false));
        }

        conciliacion.setAnulada_por(asientoReversion.getReferenciaExterna());
        conciliacionDao.save(conciliacion);
        asientoOriginal.setEstado(Estado.ANULADO);
        // Si tenés mappedBy
        this.asientosDao.save(asientoOriginal); // Solo si querés actualizar el estado
    }

    private void checkCuentaPorCobrar(AsientoContable asiento) {
        cuentasPorCobrar.aplicarMovimientoSobreCuenta(asiento);
    }

    private void checkCuentaPorPagar(AsientoContable asiento) {
        cuentasPorPagar.aplicarMovimientoSobreCuenta(asiento);
    }

}
