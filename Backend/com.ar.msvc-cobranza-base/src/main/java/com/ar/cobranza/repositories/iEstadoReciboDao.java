package com.ar.cobranza.repositories;

import com.ar.cobranza.entities.EstadoRecibo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iEstadoReciboDao extends JpaRepository<EstadoRecibo, Long>{
    
    public Optional<EstadoRecibo> findByDescripcion(String descripcion);
}
