package com.ar.base.repositories;

import com.ar.base.entities.CuentaCorriente;
import com.ar.base.entities.MovimientoCuentaCorriente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface iMovimientoCuentaCorrienteRepository extends JpaRepository<MovimientoCuentaCorriente, Long>{
    
    @Query("""
        SELECT m FROM MovimientoCuentaCorriente m
        WHERE m.cuentaCorriente = :cuentaCorriente
          AND m.tipoMovimiento = :tipo
          AND m.importe > (
              SELECT COALESCE(SUM(a.importeAplicado), 0)
              FROM AplicacionMovimiento a
              WHERE (a.movimientoOrigen = m AND m.tipoMovimiento = 'CREDITO')
                 OR (a.movimientoDestino = m AND m.tipoMovimiento = 'DEBITO')
          )
        ORDER BY m.fecha ASC, m.id ASC
    """)
   public List<MovimientoCuentaCorriente> findPendientesPorTipo(
        @Param("cuentaCorriente") CuentaCorriente cuentaCorriente,
        @Param("tipo") MovimientoCuentaCorriente.TipoMovimiento tipo
    );
}
