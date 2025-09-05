package com.ar.invoices.repositories;

import com.ar.invoices.entities.EstadoComprobante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iEstadoComprobanteDao extends JpaRepository<EstadoComprobante, Long>{
    
    public Optional<EstadoComprobante> findByDescripcion(String descripcion);
}
