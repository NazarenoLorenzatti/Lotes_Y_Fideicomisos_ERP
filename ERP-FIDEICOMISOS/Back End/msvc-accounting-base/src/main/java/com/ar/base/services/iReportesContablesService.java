package com.ar.base.services;

import com.ar.base.DTOs.LibroDiarioDTO;
import com.ar.base.DTOs.LibroMayorDTO;
import java.util.Date;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface iReportesContablesService {

    public ResponseEntity<?> generarLibroDiario(Date desde, Date hasta);

    public ResponseEntity<?> generarLibroMayor(Date desde, Date hasta);
}
