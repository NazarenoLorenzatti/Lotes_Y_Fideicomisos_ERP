package com.ar.compras.repositories;

import com.ar.compras.entities.OrdenDePago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iOrdenDePagoDao extends JpaRepository<OrdenDePago, Long>{
    
}
