package com.ar.base.repositories;

import com.ar.base.entities.EventoFallido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoFallidoDao extends JpaRepository<EventoFallido, Long>{
    
    public List<EventoFallido> findByReprocesadoFalse();
}
