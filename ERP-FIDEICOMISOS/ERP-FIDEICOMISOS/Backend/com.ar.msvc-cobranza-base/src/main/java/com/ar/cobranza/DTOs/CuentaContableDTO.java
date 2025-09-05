/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ar.cobranza.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CuentaContableDTO {

    private Long id;
    private String codigo; // Ej: 1.1.1.01
    private String nombre; // Ej: Caja Sucursal 1

    private boolean activa = true;

    private boolean conciliable = true;

    private Boolean oficial = true;
}
