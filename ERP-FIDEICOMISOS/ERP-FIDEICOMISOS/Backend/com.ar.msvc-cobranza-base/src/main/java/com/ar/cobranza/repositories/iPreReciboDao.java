package com.ar.cobranza.repositories;

import com.ar.cobranza.entities.EstadoRecibo;
import com.ar.cobranza.entities.PreRecibo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iPreReciboDao extends JpaRepository<PreRecibo, Long>{
 
    public List<PreRecibo> findAllByEstado(EstadoRecibo estado);
}
