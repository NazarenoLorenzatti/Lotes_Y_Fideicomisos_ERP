package com.ar.base.services.impl;

import com.ar.base.entities.*;
import com.ar.base.entities.CuentaPorCobrar.EstadoCuenta;
import com.ar.base.repositories.iCuentaPorCobrarDao;
import com.ar.base.repositories.iMovimientoContableDao;
import com.ar.base.services.iConciliacionService;
import com.ar.base.services.iCuentaPorCobrarService;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CuentaPorCobrarServiceImpl implements iCuentaPorCobrarService {

    @Autowired
    private iCuentaPorCobrarDao cuentaPorCobrarDao;

    @Autowired
    private iConciliacionService conciliacionService;

    @Autowired
    private iMovimientoContableDao movimientosDao;

    private static final String CODIGO_CUENTA_CONTABLE = "1.100.000";
    private static final String CODIGO_CUENTA_CONTABLE_AUX = "91.100.000";

    @Override
    public void updateDebeCuentasPorCobrar(AsientoContable asientoContable) {
        for (MovimientoContable mov : asientoContable.getMovimientos()) {
            if (mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE) || mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE_AUX)) {

                double importeFactura = mov.getDebe();

                // Buscar saldos a favor existentes del cliente
                List<CuentaPorCobrar> saldosAFavor = cuentaPorCobrarDao.findSaldosFavorByEntidadId(asientoContable.getContactoId());

                for (CuentaPorCobrar saldo : saldosAFavor) {
                    double saldoDisponible = -saldo.getSaldoPendiente(); // saldo negativo representa dinero a favor
                    double aAplicar = Math.min(saldoDisponible, importeFactura);

                    if (aAplicar > 0) {
                        AplicacionPago aplicacion = new AplicacionPago();
                        this.buildAplicacion(aplicacion, asientoContable, saldo);
                        aplicacion.setImporte(aAplicar);
                        saldo.getAplicaciones().add(aplicacion);
                        saldo.setSaldoPendiente(saldo.getSaldoPendiente() + aAplicar); // reduce el saldo a favor
                        if (saldo.getSaldoPendiente() >= 0) {
                            saldo.setEstado(EstadoCuenta.CANCELADA);
                        }
                        saldo = cuentaPorCobrarDao.save(saldo);
                        importeFactura -= aAplicar;
                    }

                    if (saldo.getEstado().equals(EstadoCuenta.CANCELADA)) {
                        this.conciliarMovimientos(saldo, mov.getCuenta());
                    }

                    if (importeFactura <= 0) {
                        break;
                    }
                }

                if (importeFactura > 0) {
                    CuentaPorCobrar cxc = new CuentaPorCobrar();
                    cxc.setAsiento(asientoContable);
                    cxc.setComprobanteId(asientoContable.getComprobanteId());
                    cxc.setTipoEntidad(asientoContable.getTipoEntidad());
                    cxc.setEntidadId(asientoContable.getContactoId());
                    cxc.setFechaEmision(asientoContable.getFecha());
                    cxc.setImporteOriginal(importeFactura);
                    cxc.setSaldoPendiente(importeFactura);
                    cxc.setEstado(EstadoCuenta.PENDIENTE);
                    cxc.setReferencia(asientoContable.getReferenciaExterna());
                    cuentaPorCobrarDao.save(cxc);
                }
            }
        }
    }

    @Override
    public void updateHaberCuentasPorCobrar(AsientoContable asientoContable) {
        for (MovimientoContable mov : asientoContable.getMovimientos()) {
            if ((mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE)
                    || mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE_AUX))
                    && mov.getHaber() > 0) {

                CuentaContable cuenta = mov.getCuenta();

                // Buscar CxC pendientes o parciales
                List<CuentaPorCobrar> cxcList = cuentaPorCobrarDao.findPendientesByEntidadIdAndEstado(
                        asientoContable.getContactoId(), EstadoCuenta.PENDIENTE);
                if (cxcList.isEmpty()) {
                    cxcList = cuentaPorCobrarDao.findPendientesByEntidadIdAndEstado(
                            asientoContable.getContactoId(), EstadoCuenta.PARCIAL);
                }

                double restante = mov.getHaber();

                for (CuentaPorCobrar cxc : cxcList) {
                    if (restante <= 0) {
                        break;
                    }

                    double aAplicar = Math.min(cxc.getSaldoPendiente(), restante);
                    cxc.setSaldoPendiente(cxc.getSaldoPendiente() - aAplicar);
                    if (cxc.getSaldoPendiente() <= 0) {
                        cxc.setEstado(EstadoCuenta.CANCELADA);
                    }

                    AplicacionPago aplicacion = new AplicacionPago();
                    this.buildAplicacion(aplicacion, asientoContable, cxc);
                    aplicacion.setImporte(aAplicar);
                    cxc.getAplicaciones().add(aplicacion);

                    cuentaPorCobrarDao.save(cxc);
                    restante -= aAplicar;

                    if (cxc.getEstado().equals(EstadoCuenta.CANCELADA)) {
                        this.conciliarMovimientos(cxc, cuenta);
                    }
                }

                // Si hay saldo a favor restante, se registra como CxC negativa
                if (restante > 0) {
                    CuentaPorCobrar saldoAFavor = new CuentaPorCobrar();
                    saldoAFavor.setAsiento(asientoContable);
                    saldoAFavor.setComprobanteId(asientoContable.getComprobanteId());
                    saldoAFavor.setTipoEntidad(asientoContable.getTipoEntidad());
                    saldoAFavor.setEntidadId(asientoContable.getContactoId());
                    saldoAFavor.setFechaEmision(asientoContable.getFecha());
                    saldoAFavor.setImporteOriginal(-restante);
                    saldoAFavor.setSaldoPendiente(-restante);
                    saldoAFavor.setEstado(EstadoCuenta.SALDO_A_FAVOR);
                    saldoAFavor.setReferencia(asientoContable.getReferenciaExterna());
                    cuentaPorCobrarDao.save(saldoAFavor);
                }
            }
        }
    }

    @Override
    public void anulacionSaldo(Long idAsientoOriginal) {
        AsientoContable asiento = new AsientoContable();
        asiento.setId(idAsientoOriginal);
        List<CuentaPorCobrar> cuentas = cuentaPorCobrarDao.findByAsiento(asiento);
        for (CuentaPorCobrar cxc : cuentas) {
            cxc.setEstado(EstadoCuenta.ANULADA);
            cxc.setSaldoPendiente(0.0);
            cuentaPorCobrarDao.save(cxc);
        }
    }

    private void conciliarMovimientos(CuentaPorCobrar cxc, CuentaContable cuenta) {
        this.conciliacionService.conciliarMovimientos(cxc, cuenta);
    }

    private void buildAplicacion(AplicacionPago aplicacion, AsientoContable asientoContable, CuentaPorCobrar cxc) {
        aplicacion.setAsiento(asientoContable);
        aplicacion.setCuentaPorCobrar(cxc);
        aplicacion.setDescripcion(asientoContable.getDescripcion());
        aplicacion.setReferenciaExterna(asientoContable.getReferenciaExterna());
        aplicacion.setFechaAplicacion(new Date());
    }

}
