package com.ar.base.repositories;

import com.ar.base.entities.Estados;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iEstadosRepository extends JpaRepository<Estados, Long>{
    
    public Optional<Estados> findByNombreEstado(String nombreEstado);
}
