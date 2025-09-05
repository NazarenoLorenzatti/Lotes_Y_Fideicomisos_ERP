package com.ar.base.repositories;

import com.ar.base.entities.AsientoContable;
import com.ar.base.entities.ConciliacionContable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iAsientoContableDao extends JpaRepository<AsientoContable, Long>{
 
    public List<AsientoContable> findByFechaBetweenOrderByFechaAsc(Date desde, Date hasta);
    
    public Optional<AsientoContable> findByReferenciaExterna(String referenciaExterna);
    
    public Optional<AsientoContable> findByConciliacion(ConciliacionContable conciliacion);
}
