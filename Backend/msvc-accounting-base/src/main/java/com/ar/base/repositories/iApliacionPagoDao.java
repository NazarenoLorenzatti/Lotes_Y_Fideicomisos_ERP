package com.ar.base.repositories;

import com.ar.base.entities.AplicacionPago;
import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.CuentaPorCobrar;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iApliacionPagoDao extends JpaRepository<AplicacionPago, Long> {

    public Optional<AplicacionPago> findByAsiento(AsientoContable asiento);
}
