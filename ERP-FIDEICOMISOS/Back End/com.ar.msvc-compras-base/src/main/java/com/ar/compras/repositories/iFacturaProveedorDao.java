package com.ar.compras.repositories;

import com.ar.compras.entities.FacturaProveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iFacturaProveedorDao extends JpaRepository<FacturaProveedor, Long>{
    
    public boolean existsByNumeroFactura(String numero);
}
