package com.ar.compras.services;

import com.ar.compras.entities.MinutaDePago;
import org.springframework.http.ResponseEntity;

public interface iMinutaDePagoService {

    public ResponseEntity<?> buildMinutaDePago(MinutaDePago minuta);

    public ResponseEntity<?> editMinutaDePago(MinutaDePago minuta);

    public ResponseEntity<?> deleteMinutaDePago(Long id);

    public ResponseEntity<?> confirmMinutaDePago(Long id);

    public ResponseEntity<?> declineMinutaDePago(Long id);

    public ResponseEntity<?> payMinutaDePago(Long id);

    public ResponseEntity<?> getMinutaDePago(Long id);

    public ResponseEntity<?> getAllMinutaDePago();
}
