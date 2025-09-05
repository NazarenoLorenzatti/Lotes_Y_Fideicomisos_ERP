package com.ar.afip.repositories;

import com.ar.afip.entities.CuitEmisor;
import com.ar.afip.entities.PuntosDeVenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iPuntosDeVentaDao extends JpaRepository<PuntosDeVenta, Long>{
    
    public Optional<PuntosDeVenta> findByNombrePtoVenta(String nombre);
    
    public Optional<PuntosDeVenta> findByNroPtoVentaAndCuit(int nroPuntoVenta, CuitEmisor cuit);
    
    public List<PuntosDeVenta> findByCuit(CuitEmisor cuit);
    
}
