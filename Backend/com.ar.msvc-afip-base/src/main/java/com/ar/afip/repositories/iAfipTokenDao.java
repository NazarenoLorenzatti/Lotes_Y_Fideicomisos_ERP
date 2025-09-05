package com.ar.afip.repositories;

import com.ar.afip.entities.AfipToken;
import com.ar.afip.entities.CuitEmisor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iAfipTokenDao extends JpaRepository<AfipToken, Long>{
    
    public Optional<AfipToken> findTopByServiceOrderByExpirationDesc(String service);
    
    public Optional<AfipToken> findTopByCuitAndServiceOrderByExpirationDesc(CuitEmisor cuit, String service);
}
