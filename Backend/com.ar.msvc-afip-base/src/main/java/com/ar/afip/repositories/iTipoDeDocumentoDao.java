package com.ar.afip.repositories;

import com.ar.afip.entities.TipoDeDocumento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iTipoDeDocumentoDao extends JpaRepository<TipoDeDocumento, Long>{
    
    public Optional<TipoDeDocumento> findByDescripcion(String descripcion);
    
     public Optional<TipoDeDocumento> findByIdAfip(int idAfip);
}
