package com.ar.base.repositories;

import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.CuentaContable;
import com.ar.base.entities.MovimientoContable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iMovimientoContableDao extends JpaRepository<MovimientoContable, Long> {

    public List<MovimientoContable> findByAsientoId(Long asientoId);

    public List<MovimientoContable> findByCuentaAndEntidadIdAndConciliado(CuentaContable cuenta, Long entidadId, boolean conciliado);

    public Optional<MovimientoContable> findByAsientoAndCuenta(AsientoContable asiento, CuentaContable cuenta);

    public List<MovimientoContable> findByCuentaAndFechaBetweenOrderByFechaAsc(CuentaContable cuenta, Date desde, Date hasta);

    public boolean existsByCuenta(CuentaContable cuentaContable);
}
