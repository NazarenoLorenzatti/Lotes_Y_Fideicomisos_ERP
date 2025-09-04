package com.ar.base.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tipos_de_entidades")
public class TiposDeEntidades {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
}
