package com.ar.invoices.repositories;

import com.ar.invoices.entities.EstadoComprobante;
import com.ar.invoices.entities.PreComprobante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iPreComprobanteDao extends JpaRepository<PreComprobante, Long>{
 
       public List<PreComprobante> findByContactoId(Long idContacto);
       
       public List<PreComprobante> findAllByEstado(EstadoComprobante estado);
}
