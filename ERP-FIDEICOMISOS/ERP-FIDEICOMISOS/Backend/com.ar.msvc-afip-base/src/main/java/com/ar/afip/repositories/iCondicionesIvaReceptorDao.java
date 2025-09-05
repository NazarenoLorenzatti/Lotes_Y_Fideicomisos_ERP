package com.ar.afip.repositories;

import com.ar.afip.entities.CondicionIvaRecepetor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCondicionesIvaReceptorDao extends JpaRepository<CondicionIvaRecepetor, Long>{
    
    public Optional<CondicionIvaRecepetor> findByDescripcion(String decripcion);
    
    public Optional<CondicionIvaRecepetor> findByIdAfip(Integer idAfip);
}
