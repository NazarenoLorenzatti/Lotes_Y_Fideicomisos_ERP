/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ar.afip.repositories;

import com.ar.afip.entities.AlicuotasIVA;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface iAlicuotaIvaDao extends JpaRepository<AlicuotasIVA, Long>{
    
    public Optional<AlicuotasIVA> findByDescripcion(String decripcion);

}
