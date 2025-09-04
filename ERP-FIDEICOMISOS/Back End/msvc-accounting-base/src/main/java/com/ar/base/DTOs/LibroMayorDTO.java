package com.ar.base.DTOs;

import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

@Data
public class LibroMayorDTO {

    private String cuenta;
    private LocalDate fecha;
    private Long numeroAsiento;
    private String descripcion;
    private Double debe;
    private Double haber;
    private Double saldo;
}
