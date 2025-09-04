package com.ar.base.services;

import com.ar.base.entities.AsientoContable;

public interface iCuentaPorCobrarService {

    public void updateDebeCuentasPorCobrar(AsientoContable asientoContable);

    public void updateHaberCuentasPorCobrar(AsientoContable asientoContable);

    public void anulacionSaldo(Long idAsientoOriginal);
}
