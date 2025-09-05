package com.ar.base.repositories;

import com.ar.base.entities.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iContactoRepository extends JpaRepository<Contacto, Long>{
    
}
