package com.ar.base.repositories;

import com.ar.base.entities.AplicacionMovimiento;
import com.ar.base.entities.MovimientoCuentaCorriente;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface iAplicacionMovimientoDao extends JpaRepository<AplicacionMovimiento, Long> {

    @Query("SELECT SUM(a.importeAplicado) FROM AplicacionMovimiento a WHERE a.movimientoDestino = :factura")
    public Double sumImporteAplicadoByMovimientoDestino(@Param("factura") MovimientoCuentaCorriente factura);
    
    public List<AplicacionMovimiento> findAllByMovimientoOrigen(MovimientoCuentaCorriente mov);
    
    public List<AplicacionMovimiento> findAllByMovimientoDestino(MovimientoCuentaCorriente mov);
}
