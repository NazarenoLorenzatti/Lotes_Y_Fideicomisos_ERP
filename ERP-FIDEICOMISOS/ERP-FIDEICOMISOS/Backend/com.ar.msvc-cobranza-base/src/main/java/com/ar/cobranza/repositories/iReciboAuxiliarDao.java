package com.ar.cobranza.repositories;

import com.ar.cobranza.entities.ReciboAuxiliar;
import com.ar.cobranza.entities.PreRecibo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iReciboAuxiliarDao extends JpaRepository<ReciboAuxiliar, Long>{
    
    public Optional<ReciboAuxiliar> findByPreRecibo(PreRecibo preComprobante);
}
