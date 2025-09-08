package com.ar.base.services;

import com.ar.base.DTOs.AplicacionManualRequest;
import com.ar.base.entities.Contacto;
import com.ar.base.entities.MovimientoCuentaCorriente;
import org.springframework.http.ResponseEntity;

public interface iCuentaCorrienteService {

    public ResponseEntity<?> getCuentaCorriente(Contacto contacto);

    public Double calcularSaldoFactura(Long facturaId);

    public ResponseEntity<?> registrarMovimiento(Long clienteId, MovimientoCuentaCorriente movimiento);

    public ResponseEntity<?> applyMovimiento(AplicacionManualRequest request);
    
    public ResponseEntity<?> getAplicaciones(Long idMovimientoOrigen, Long idMovimientoDestino);

    public ResponseEntity<?> anularAplicacion(String nroComprobante);
    
}
