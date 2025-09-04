package com.ar.base.services;

import com.ar.base.entities.AsientoContable;

public interface iCuentaPorPagarService {

    public void updateDebeCuentasPorPagar(AsientoContable asientoContable);

    public void updateHaberCuentasPorPagar(AsientoContable asientoContable);

    public void anulacionSaldo(Long idAsientoOriginal);
}
