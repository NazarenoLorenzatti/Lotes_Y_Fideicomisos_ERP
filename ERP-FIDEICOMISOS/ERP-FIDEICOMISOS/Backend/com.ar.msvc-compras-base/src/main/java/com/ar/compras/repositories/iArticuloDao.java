package com.ar.compras.repositories;

import com.ar.compras.entities.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iArticuloDao extends JpaRepository<Articulo, Long>{
    
}
