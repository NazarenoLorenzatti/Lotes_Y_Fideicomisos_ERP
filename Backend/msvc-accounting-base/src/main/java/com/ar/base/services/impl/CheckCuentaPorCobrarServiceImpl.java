package com.ar.base.services.impl;

import com.ar.base.entities.AsientoContable;
import com.ar.base.services.iCuentaPorCobrarService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CheckCuentaPorCobrarServiceImpl {
     private final iCuentaPorCobrarService cuentaPorCobrarService;

    public CheckCuentaPorCobrarServiceImpl(iCuentaPorCobrarService cuentaPorCobrarService) {
        this.cuentaPorCobrarService = cuentaPorCobrarService;
    }
    
    public void liberarCuentaPorAnulacion(List<AsientoContable> asientos){
        cuentaPorCobrarService.liberarCuentaPorAnulacion(asientos);
    }

    public void aplicarMovimientoSobreCuenta(AsientoContable asiento) {
        switch (asiento.getTipoOperacion()) {
            case FACTURA_CLIENTE, NOTA_DEBITO -> 
                cuentaPorCobrarService.updateDebeCuentasPorCobrar(asiento);

            case RECIBO -> 
                cuentaPorCobrarService.updateHaberCuentasPorCobrar(asiento);

            case NOTA_CREDITO, CONTRA_RECIBO -> 
                cuentaPorCobrarService.anulacionSaldo(asiento.getAsientoOrigen().getId());
        }
    }
}
