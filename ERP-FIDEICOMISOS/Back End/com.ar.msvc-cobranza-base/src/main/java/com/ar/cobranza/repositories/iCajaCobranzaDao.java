package com.ar.cobranza.repositories;

import com.ar.cobranza.entities.CajaCobranza;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCajaCobranzaDao extends JpaRepository<CajaCobranza, Long>{
    
    public List<CajaCobranza> findAllByCajaCobranza(Boolean cobranza);
}
