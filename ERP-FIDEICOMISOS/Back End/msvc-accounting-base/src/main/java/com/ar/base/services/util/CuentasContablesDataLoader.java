package com.ar.base.services.util;

import com.ar.base.entities.CuentaContable;
import com.ar.base.repositories.iCuentaContableDao;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Componente para el Inicio de cuentas Contables estandar.
 * @author nlore
 */
@Component
public class CuentasContablesDataLoader implements ApplicationRunner {

    private final iCuentaContableDao cuentaRepository;

    public CuentasContablesDataLoader(iCuentaContableDao cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (cuentaRepository.count() == 0) {
            CuentaContable activos = crearCuenta("1.000.000", "Activos", CuentaContable.TipoCuenta.ACTIVO);
            CuentaContable pasivos = crearCuenta("2.000.000", "Pasivos", CuentaContable.TipoCuenta.PASIVO);
            CuentaContable patrimonio = crearCuenta("3.000.000", "Patrimonio Neto", CuentaContable.TipoCuenta.EGRESO);
            CuentaContable ingresos = crearCuenta("4.000.000", "Ingresos", CuentaContable.TipoCuenta.INGRESO);

            cuentaRepository.saveAll(List.of(activos, pasivos, patrimonio, ingresos));

            CuentaContable deudores = crearCuenta("1.100.000", "Deudores Por Ventas", CuentaContable.TipoCuenta.ACTIVO, activos);
            CuentaContable ventas = crearCuenta("4.401.000", "Ventas", CuentaContable.TipoCuenta.INGRESO, ingresos);
            CuentaContable cajaGeneral = crearCuenta("4.100.000", "Caja General", CuentaContable.TipoCuenta.INGRESO, ingresos);
            CuentaContable cajaCobranza = crearCuenta("4.100.100", "Caja Cobranza", CuentaContable.TipoCuenta.INGRESO, cajaGeneral);
            CuentaContable ivaVentas = crearCuenta("2.200.000", "Iva Ventas", CuentaContable.TipoCuenta.PASIVO, pasivos);

            cuentaRepository.saveAll(List.of(deudores, ventas, cajaGeneral, cajaCobranza, ivaVentas));
        }
    }

    private CuentaContable crearCuenta(String codigo, String nombre, CuentaContable.TipoCuenta tipo) {
        return crearCuenta(codigo, nombre, tipo, null);
    }

    private CuentaContable crearCuenta(String codigo, String nombre, CuentaContable.TipoCuenta tipo, CuentaContable padre) {
        CuentaContable cuenta = new CuentaContable();
        cuenta.setCodigo(codigo);
        cuenta.setNombre(nombre);
        cuenta.setTipo(tipo);
        cuenta.setActiva(true);
        cuenta.setConciliable(true);
        cuenta.setOficial(true);
        cuenta.setPadre(padre);
        return cuenta;
    }
}