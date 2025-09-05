package com.ar.invoices.repositories;

import com.ar.invoices.entities.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iArticuloDao extends JpaRepository<Articulo, Long>{
    
    public boolean existsByCodigo(String codigo);
    public boolean existsByDescripcion(String codigo);
}
