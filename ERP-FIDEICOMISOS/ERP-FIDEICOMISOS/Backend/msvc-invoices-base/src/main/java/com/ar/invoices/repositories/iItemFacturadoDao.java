package com.ar.invoices.repositories;

import com.ar.invoices.entities.ItemFacturado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iItemFacturadoDao extends JpaRepository<ItemFacturado, Long>{
    
}
