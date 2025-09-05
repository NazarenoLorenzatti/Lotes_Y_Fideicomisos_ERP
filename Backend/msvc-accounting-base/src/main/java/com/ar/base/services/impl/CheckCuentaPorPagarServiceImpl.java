package com.ar.base.services.impl;

import com.ar.base.entities.AsientoContable;
import com.ar.base.services.iCuentaPorCobrarService;
import com.ar.base.services.iCuentaPorPagarService;
import org.springframework.stereotype.Service;

@Service
public class CheckCuentaPorPagarServiceImpl {
     private final iCuentaPorPagarService cuentaPorPagarService;

    public CheckCuentaPorPagarServiceImpl(iCuentaPorPagarService cuentaPorPagarService) {
        this.cuentaPorPagarService = cuentaPorPagarService;
    }

    public void aplicarMovimientoSobreCuenta(AsientoContable asiento) {
        switch (asiento.getTipoOperacion()) {
            case FACTURA_CLIENTE, NOTA_DEBITO -> 
                cuentaPorPagarService.updateDebeCuentasPorPagar(asiento);

            case RECIBO -> 
                cuentaPorPagarService.updateHaberCuentasPorPagar(asiento);

            case NOTA_CREDITO -> 
                cuentaPorPagarService.anulacionSaldo(asiento.getAsientoOrigen().getId());
        }
    }
}
