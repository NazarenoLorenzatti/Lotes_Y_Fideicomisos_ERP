package com.ar.base.services;

import com.ar.base.entities.AsientoContable;
import java.util.List;

public interface iCuentaPorCobrarService {

    public void updateDebeCuentasPorCobrar(AsientoContable asientoContable);

    public void updateHaberCuentasPorCobrar(AsientoContable asientoContable);

    public void anulacionSaldo(Long idAsientoOriginal);
    
    public void liberarCuentaPorAnulacion(List<AsientoContable> asientos);
}
