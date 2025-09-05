package com.ar.base.DTOs;

import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

@Data
public class LibroDiarioDTO {
    private LocalDate fecha;
    private Long numeroAsiento;
    private String descripcion;
    private String cuenta;
    private Double debe;
    private Double haber;
    private boolean conciliado;
}