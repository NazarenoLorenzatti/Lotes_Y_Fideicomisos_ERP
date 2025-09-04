package com.ar.base.services;

import com.ar.base.entities.AsientoContable;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface iAsientoContableService {

    public ResponseEntity<?> registrarAsiento(AsientoContable asientoContable);

    public ResponseEntity<?> registrarAsientoReversion(Long id, AsientoContable asientoReversion);

    public ResponseEntity<?> conciliarMovimientos(List<Long> movimientosIds, String tipoOperacion, String descripcion);

    public ResponseEntity<?> anularConciliacion(Long consiliacionId);

    public ResponseEntity<?> getAsiento(Long id);

    public ResponseEntity<?> listarAsientos();

    public ResponseEntity<?> getMovimiento(Long id);

    public ResponseEntity<?> listarMovimientos();

    public ResponseEntity<?> getConciliacion(Long id);

    public ResponseEntity<?> listarConciliaciones();

    public ResponseEntity<?> listarMovimientosPorAsiento(Long idAsiento);

    public ResponseEntity<?> buscarConciliacionPorMoviento(Long idMovimiento);

//    public ResponseEntity<?> guardarMovimientoContable(MovimientoContable movimientoContable);
}
