package com.ar.afip.repositories;

import com.ar.afip.entities.CuitEmisor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCuitEmisorDao extends JpaRepository<CuitEmisor, Long>{
    
    public Optional<CuitEmisor> findByCuit(String cuit);
}
