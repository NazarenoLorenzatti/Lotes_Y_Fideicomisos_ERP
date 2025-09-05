package com.ar.invoices.repositories;

import com.ar.invoices.entities.ComprobanteAuxiliar;
import com.ar.invoices.entities.PreComprobante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iComprobanteAuxiliarDao extends JpaRepository<ComprobanteAuxiliar, Long>{
    
    public Optional<ComprobanteAuxiliar> findByPreComprobante(PreComprobante preComprobante);
    
    public List<ComprobanteAuxiliar> findAllBySaldado(boolean saldado);
}
