package com.ar.cobranza.repositories;

import com.ar.cobranza.entities.ReciboOficial;
import com.ar.cobranza.entities.PreRecibo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iReciboOficialDao extends JpaRepository<ReciboOficial, Long>{
    
     public Optional<ReciboOficial> findByPreRecibo(PreRecibo preComprobante);
     
}
