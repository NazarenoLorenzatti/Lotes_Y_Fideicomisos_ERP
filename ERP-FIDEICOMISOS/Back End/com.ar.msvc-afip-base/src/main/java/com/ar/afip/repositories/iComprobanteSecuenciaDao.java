package com.ar.afip.repositories;

import com.ar.afip.entities.ComprobanteSecuencia;
import com.ar.afip.entities.CuitEmisor;
import com.ar.afip.entities.PuntosDeVenta;
import com.ar.afip.entities.TipoComprobante;
import feign.Param;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author nlore
 */
public interface iComprobanteSecuenciaDao extends JpaRepository<ComprobanteSecuencia, Long> {

    public Optional<ComprobanteSecuencia> findByTipoComprobanteAndPuntoVentaAndCuit(TipoComprobante tipoComprobante, PuntosDeVenta puntoVenta, CuitEmisor cuit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM ComprobanteSecuencia cs WHERE cs.tipoComprobante = :tipo AND cs.puntoVenta = :punto AND cs.cuit = :cuit")
    public Optional<ComprobanteSecuencia> findAndLock(@Param("tipo") TipoComprobante tipo, @Param("punto") PuntosDeVenta punto, @Param("cuit") CuitEmisor cuit);
    
    
    
}
