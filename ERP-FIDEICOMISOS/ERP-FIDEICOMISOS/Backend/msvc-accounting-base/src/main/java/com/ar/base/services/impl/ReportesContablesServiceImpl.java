package com.ar.base.services.impl;

import com.ar.base.DTOs.LibroDiarioDTO;
import com.ar.base.DTOs.LibroMayorDTO;
import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.CuentaContable;
import com.ar.base.entities.MovimientoContable;
import com.ar.base.repositories.iAsientoContableDao;
import com.ar.base.repositories.iCuentaContableDao;
import com.ar.base.repositories.iMovimientoContableDao;
import com.ar.base.services.iReportesContablesService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReportesContablesServiceImpl implements iReportesContablesService{

    @Autowired
    private iAsientoContableDao asientoDao;

    @Autowired
    private iMovimientoContableDao movimientoDao;

    @Autowired
    private iCuentaContableDao cuentaDao;

    public ResponseEntity<?> generarLibroDiario(Date desde, Date hasta) {
        List<AsientoContable> asientos = asientoDao.findByFechaBetweenOrderByFechaAsc(desde, hasta);

        List<LibroDiarioDTO> libro = new ArrayList<>();

        for (AsientoContable asiento : asientos) {
            for (MovimientoContable mov : asiento.getMovimientos()) {
                LibroDiarioDTO fila = new LibroDiarioDTO();
                fila.setFecha(asiento.getFecha());
                fila.setNumeroAsiento(asiento.getId());
                fila.setDescripcion(asiento.getDescripcion());
                fila.setCuenta(mov.getCuenta().getNombre());
                fila.setDebe(mov.getDebe());
                fila.setHaber(mov.getHaber());
                fila.setConciliado(mov.isConciliado());
                libro.add(fila);
            }
        }
        return ResponseEntity.ok(libro);
    }

    public ResponseEntity<?> generarLibroMayor(Date desde, Date hasta) {
        List<CuentaContable> cuentas = cuentaDao.findAllByActiva(true);
        List<LibroMayorDTO> resultado = new ArrayList<>();

        for (CuentaContable cuenta : cuentas) {
            List<MovimientoContable> movimientos = movimientoDao
                    .findByCuentaAndFechaBetweenOrderByFechaAsc(cuenta, desde, hasta);

            double saldo = 0;

            for (MovimientoContable mov : movimientos) {
                double debe = mov.getDebe();
                double haber = mov.getHaber();
                saldo += (debe - haber);

                LibroMayorDTO fila = new LibroMayorDTO();
                fila.setCuenta(cuenta.getNombre());
                fila.setFecha(mov.getFecha());
                fila.setNumeroAsiento(mov.getAsiento().getId());
                fila.setDescripcion(mov.getAsiento().getDescripcion());
                fila.setDebe(debe);
                fila.setHaber(haber);
                fila.setSaldo(saldo);
                resultado.add(fila);
            }
        }

        return ResponseEntity.ok(resultado);
    }
}
