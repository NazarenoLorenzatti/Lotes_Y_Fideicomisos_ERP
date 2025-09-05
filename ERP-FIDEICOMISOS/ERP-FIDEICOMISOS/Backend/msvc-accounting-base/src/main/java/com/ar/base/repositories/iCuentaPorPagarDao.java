package com.ar.base.repositories;

import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.CuentaPorPagar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCuentaPorPagarDao extends JpaRepository<CuentaPorPagar, Long> {

    public List<CuentaPorPagar> findPendientesByEntidadIdAndEstado(Long entidadId, CuentaPorPagar.EstadoCuenta estado);

    public List<CuentaPorPagar> findByAsiento(AsientoContable asiento);
    
    public List<CuentaPorPagar> findSaldosFavorByEntidadId(Long entidadId);
    
}

    

