package com.ar.afip.repositories;

import com.ar.afip.entities.TipoComprobante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iTipoComprobanteDao extends JpaRepository<TipoComprobante, Long>{
    
    public Optional<TipoComprobante> findByDescripcion(String descripcion);
    
     public Optional<TipoComprobante> findByIdAfip(int idAfip);
}
