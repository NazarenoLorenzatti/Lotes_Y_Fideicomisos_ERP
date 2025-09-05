package com.ar.base.services;

import com.ar.base.entities.CuentaContable;
import com.ar.base.entities.*;

public interface iConciliacionService {
    public void conciliarMovimientos(CuentaPorCobrar cxc, CuentaContable cuenta);
    public void conciliarMovimientos(CuentaPorPagar cxp, CuentaContable cuenta);
}
