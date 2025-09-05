package com.ar.afip.services;

import org.springframework.http.ResponseEntity;

public interface iConfigurationAfipService {

    public ResponseEntity<?> getPtoDeVenta(Integer nroPtoDeVenta, String cuit);

    public ResponseEntity<?> getTipoComprobante(Integer idAfip);

    public ResponseEntity<?> getTipoDeDocumento(Integer idAfip);

    public ResponseEntity<?> getCondicionIvaReceptor(Integer idAfip);

    public ResponseEntity<?> getAlicuotasIva(String descripcion);
    
    public ResponseEntity<?> getCuitEmisores(String cuit);

    public ResponseEntity<?> listarPtoDeVenta();
    
     public ResponseEntity<?> listarPtosDeVentaCuits(String cuit);

    public ResponseEntity<?> listarTipoComprobante();

    public ResponseEntity<?> listarTipoDeDocumento();

    public ResponseEntity<?> listarCondicionIvaReceptor();

    public ResponseEntity<?> listarAlicuotasIva();
    
    public ResponseEntity<?> listarCuitEmisores();
    
    public ResponseEntity<?> listarCiudades();
}
