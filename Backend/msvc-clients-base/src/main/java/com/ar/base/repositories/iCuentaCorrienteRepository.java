package com.ar.base.repositories;

import com.ar.base.entities.Contacto;
import com.ar.base.entities.CuentaCorriente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iCuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Long>{
    
    public Optional<CuentaCorriente> findByContacto(Contacto contacto);
}
