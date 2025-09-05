package com.ar.base.services.impl;

import com.ar.base.entities.AplicacionPago;
import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.ConciliacionContable;
import com.ar.base.entities.CuentaContable;
import com.ar.base.entities.CuentaPorCobrar;
import com.ar.base.entities.CuentaPorPagar;
import com.ar.base.entities.DetalleConciliacion;
import com.ar.base.entities.MovimientoContable;
import com.ar.base.repositories.iConciliacionContableDao;
import com.ar.base.repositories.iDetalleConciliacionDao;
import com.ar.base.repositories.iMovimientoContableDao;
import com.ar.base.services.iAsientoContableService;
import com.ar.base.services.iConciliacionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConciliacionServiceImpl implements iConciliacionService {

    @Autowired
    private iMovimientoContableDao movimientosDao;
    
    @Autowired
    private iConciliacionContableDao conciliacionDao;

    @Override
    public void conciliarMovimientos(CuentaPorCobrar cxc, CuentaContable cuenta) {
        List<Long> idMovimientos = new ArrayList<>();

        movimientosDao.findByAsientoAndCuenta(cxc.getAsiento(), cuenta)
                .ifPresent(m -> idMovimientos.add(m.getId()));

        for (AplicacionPago apply : cxc.getAplicaciones()) {
            movimientosDao.findByAsientoAndCuenta(apply.getAsiento(), cuenta)
                    .ifPresent(m -> idMovimientos.add(m.getId()));
        }

        this.conciliarMovimientos(idMovimientos, "AUTOMATICO", "SALDO CANCELADO");
    }

    @Override
    public void conciliarMovimientos(CuentaPorPagar cxp, CuentaContable cuenta) {
        List<Long> idMovimientos = new ArrayList<>();

        movimientosDao.findByAsientoAndCuenta(cxp.getAsiento(), cuenta)
                .ifPresent(m -> idMovimientos.add(m.getId()));

        for (AplicacionPago apply : cxp.getAplicaciones()) {
            movimientosDao.findByAsientoAndCuenta(apply.getAsiento(), cuenta)
                    .ifPresent(m -> idMovimientos.add(m.getId()));
        }

        this.conciliarMovimientos(idMovimientos, "AUTOMATICO", "SALDO CANCELADO");
    }

    private void conciliarMovimientos(List<Long> movimientosIds, String tipoOperacion, String descripcion) {
        try {
            if (movimientosIds.isEmpty()) {
                return;
            }

            List<MovimientoContable> movimientos = movimientosDao.findAllById(movimientosIds);

            // Validaciones
            Long cuentaId = movimientos.get(0).getCuenta().getId();
            boolean mismaCuenta = movimientos.stream()
                    .allMatch(m -> m.getCuenta().getId().equals(cuentaId));

            if (!mismaCuenta) {
                return;
            }

            double saldo = movimientos.stream()
                    .mapToDouble(m -> m.getDebe() - m.getHaber())
                    .sum();

            if (Math.abs(saldo) > 0.01) {
                return;
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
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
