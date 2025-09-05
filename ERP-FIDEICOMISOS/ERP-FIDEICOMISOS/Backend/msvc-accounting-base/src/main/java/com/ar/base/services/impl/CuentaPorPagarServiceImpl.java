package com.ar.base.services.impl;

import com.ar.base.entities.*;
import com.ar.base.entities.CuentaPorPagar.EstadoCuenta;
import com.ar.base.repositories.iCuentaPorPagarDao;
import com.ar.base.repositories.iMovimientoContableDao;
import com.ar.base.services.iConciliacionService;
import com.ar.base.services.iCuentaPorPagarService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CuentaPorPagarServiceImpl implements iCuentaPorPagarService {

    @Autowired
    private iCuentaPorPagarDao cuentaPorPagarDao;

    @Autowired
    private iConciliacionService conciliacionService;

//    @Autowired
//    private iMovimientoContableDao movimientosDao;

    private static final String CODIGO_CUENTA_CONTABLE = "2.100.000";
    private static final String CODIGO_CUENTA_CONTABLE_AUX = "92.100.000";

    @Override
    public void updateDebeCuentasPorPagar(AsientoContable asientoContable) {
        for (MovimientoContable mov : asientoContable.getMovimientos()) {
            if (mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE) || mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE_AUX)) {

                double importeFactura = mov.getDebe();

                // Buscar saldos a favor existentes del cliente
                List<CuentaPorPagar> saldosAFavor = cuentaPorPagarDao.findSaldosFavorByEntidadId(asientoContable.getContactoId());

                for (CuentaPorPagar saldo : saldosAFavor) {
                    double saldoDisponible = -saldo.getSaldoPendiente();
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
                        saldo = cuentaPorPagarDao.save(saldo);
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
                    CuentaPorPagar cxp = new CuentaPorPagar();
                    cxp.setAsiento(asientoContable);
                    cxp.setComprobanteId(asientoContable.getComprobanteId());
                    cxp.setTipoEntidad(asientoContable.getTipoEntidad());
                    cxp.setEntidadId(asientoContable.getContactoId());
                    cxp.setFechaEmision(asientoContable.getFecha());
                    cxp.setImporteOriginal(importeFactura);
                    cxp.setSaldoPendiente(importeFactura);
                    cxp.setEstado(EstadoCuenta.PENDIENTE);
                    cxp.setReferencia(asientoContable.getReferenciaExterna());
                    cuentaPorPagarDao.save(cxp);
                }
            }
        }
    }

    @Override
    public void updateHaberCuentasPorPagar(AsientoContable asientoContable
    ) {
        for (MovimientoContable mov : asientoContable.getMovimientos()) {
            if ((mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE)
                    || mov.getCuenta().getCodigo().equals(CODIGO_CUENTA_CONTABLE_AUX))
                    && mov.getHaber() > 0) {

                CuentaContable cuenta = mov.getCuenta();

                // Buscar CxC pendientes o parciales
                List<CuentaPorPagar> cxpList = cuentaPorPagarDao.findPendientesByEntidadIdAndEstado(
                        asientoContable.getContactoId(), EstadoCuenta.PENDIENTE);
                if (cxpList.isEmpty()) {
                    cxpList = cuentaPorPagarDao.findPendientesByEntidadIdAndEstado(
                            asientoContable.getContactoId(), EstadoCuenta.PARCIAL);
                }

                double restante = mov.getHaber();

                for (CuentaPorPagar cxp : cxpList) {
                    if (restante <= 0) {
                        break;
                    }

                    double aAplicar = Math.min(cxp.getSaldoPendiente(), restante);
                    cxp.setSaldoPendiente(cxp.getSaldoPendiente() - aAplicar);
                    if (cxp.getSaldoPendiente() <= 0) {
                        cxp.setEstado(EstadoCuenta.CANCELADA);
                    }

                    AplicacionPago aplicacion = new AplicacionPago();
                    this.buildAplicacion(aplicacion, asientoContable, cxp);
                    aplicacion.setImporte(aAplicar);
                    cxp.getAplicaciones().add(aplicacion);

                    cuentaPorPagarDao.save(cxp);
                    restante -= aAplicar;

                    if (cxp.getEstado().equals(EstadoCuenta.CANCELADA)) {
                        this.conciliarMovimientos(cxp, cuenta);
                    }
                }

                // Si hay saldo a favor restante, se registra como CxC negativa
                if (restante > 0) {
                    CuentaPorPagar saldoAFavor = new CuentaPorPagar();
                    saldoAFavor.setAsiento(asientoContable);
                    saldoAFavor.setComprobanteId(asientoContable.getComprobanteId());
                    saldoAFavor.setTipoEntidad(asientoContable.getTipoEntidad());
                    saldoAFavor.setEntidadId(asientoContable.getContactoId());
                    saldoAFavor.setFechaEmision(asientoContable.getFecha());
                    saldoAFavor.setImporteOriginal(-restante);
                    saldoAFavor.setSaldoPendiente(-restante);
                    saldoAFavor.setEstado(EstadoCuenta.SALDO_A_FAVOR);
                    saldoAFavor.setReferencia(asientoContable.getReferenciaExterna());
                    cuentaPorPagarDao.save(saldoAFavor);
                }
            }
        }
    }

    @Override
    public void anulacionSaldo(Long idAsientoOriginal
    ) {
        AsientoContable asiento = new AsientoContable();
        asiento.setId(idAsientoOriginal);
        List<CuentaPorPagar> cuentas = cuentaPorPagarDao.findByAsiento(asiento);
        for (CuentaPorPagar cxp : cuentas) {
            cxp.setEstado(EstadoCuenta.ANULADA);
            cxp.setSaldoPendiente(0.0);
            cuentaPorPagarDao.save(cxp);
        }
    }

    private void conciliarMovimientos(CuentaPorPagar cxp, CuentaContable cuenta) {
        this.conciliacionService.conciliarMovimientos(cxp, cuenta);
    }

    private void buildAplicacion(AplicacionPago aplicacion, AsientoContable asientoContable, CuentaPorPagar cxp) {
        aplicacion.setAsiento(asientoContable);
        aplicacion.setCuentaPorPagar(cxp);
        aplicacion.setDescripcion(asientoContable.getDescripcion());
        aplicacion.setReferenciaExterna(asientoContable.getReferenciaExterna());
        aplicacion.setFechaAplicacion(new Date());
    }

}
