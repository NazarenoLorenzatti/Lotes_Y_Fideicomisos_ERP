package com.ar.invoices.repositories;

import com.ar.invoices.entities.ComprobanteOficial;
import com.ar.invoices.entities.PreComprobante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iComprobanteOficialDao extends JpaRepository<ComprobanteOficial, Long>{
    
     public Optional<ComprobanteOficial> findByPreComprobante(PreComprobante preComprobante);
     
     public List<ComprobanteOficial> findAllBySaldado(boolean saldado);
}
