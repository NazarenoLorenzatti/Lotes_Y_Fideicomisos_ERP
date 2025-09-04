package com.ar.compras.repositories;

import com.ar.compras.entities.Obra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iObraDao extends JpaRepository<Obra, Long>{
    
}
