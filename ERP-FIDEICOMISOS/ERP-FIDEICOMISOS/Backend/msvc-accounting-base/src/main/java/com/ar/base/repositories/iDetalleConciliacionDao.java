package com.ar.base.repositories;

import com.ar.base.entities.DetalleConciliacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iDetalleConciliacionDao extends JpaRepository<DetalleConciliacion, Long> {

    public List<DetalleConciliacion> findByMovimientoId(Long id);
}
