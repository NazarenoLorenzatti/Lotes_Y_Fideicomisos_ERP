package com.ar.afip.repositories;

import com.ar.afip.entities.Ciudades;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCiudadesDao extends JpaRepository<Ciudades, Long>{
    
}
