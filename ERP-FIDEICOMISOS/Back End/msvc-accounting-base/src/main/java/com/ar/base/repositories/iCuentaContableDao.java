package com.ar.base.repositories;

import com.ar.base.entities.CuentaContable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCuentaContableDao extends JpaRepository<CuentaContable, Long>{
    
    public List<CuentaContable> findAllByActiva(boolean activa);
    
    public Optional<CuentaContable> findByCodigo(String codigo);
    
}
