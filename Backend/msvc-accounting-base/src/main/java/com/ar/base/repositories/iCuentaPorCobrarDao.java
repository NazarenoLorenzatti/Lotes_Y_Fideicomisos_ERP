package com.ar.base.repositories;

import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.CuentaPorCobrar;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCuentaPorCobrarDao extends JpaRepository<CuentaPorCobrar, Long> {

    public List<CuentaPorCobrar> findPendientesByEntidadIdAndEstado(Long entidadId, CuentaPorCobrar.EstadoCuenta estado);

    public Optional<CuentaPorCobrar> findByAsiento(AsientoContable asiento);
    
    public List<CuentaPorCobrar> findSaldosFavorByEntidadId(Long entidadId);
    
}
