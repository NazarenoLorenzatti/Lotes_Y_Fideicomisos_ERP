package com.ar.compras.repositories;

import com.ar.compras.entities.CajaPagos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCajaPagoDao extends JpaRepository<CajaPagos, Long>{
    
}
