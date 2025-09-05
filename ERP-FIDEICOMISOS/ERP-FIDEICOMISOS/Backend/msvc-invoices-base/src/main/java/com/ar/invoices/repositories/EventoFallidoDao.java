package com.ar.invoices.repositories;

import com.ar.invoices.entities.EventoFallido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoFallidoDao extends JpaRepository<EventoFallido, Long>{
    
    public List<EventoFallido> findByReprocesadoFalse();
}
