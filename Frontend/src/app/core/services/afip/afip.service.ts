import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8100/afip/configurations';

@Injectable({
  providedIn: 'root'
})
export class AfipService {

  constructor(private http: HttpClient) { }

  puntoDeVenta(nro: number, cuit: string) {
    let body = {};
    const endpoint = `${base_url}/get-punto-venta/${nro}/${cuit}`;
    return this.http.post(endpoint, body);
  }

  listarPuntosDeVenta() {
    const endpoint = `${base_url}/listar-ptos-venta`;
    return this.http.get(endpoint);
  }

  listarPuntosDeVentaCuit(cuit: string) {
    const endpoint = `${base_url}/listar-ptos-venta/${cuit}`;
    return this.http.get(endpoint);
  }

  obtenerTipoComprobante(idAfip: number) {
    let body = {};
    const endpoint = `${base_url}/get-comprobante/${idAfip}`;
    return this.http.post(endpoint, body);
  }

  listarTiposComprobantes() {
    const endpoint = `${base_url}/listar-comprobantes`;
    return this.http.get(endpoint);
  }

  obtenerTipoDocuento(idAfip: number) {
    let body = {};
    const endpoint = `${base_url}/get-docuemtno/${idAfip}`;
    return this.http.post(endpoint, body);
  }

  listarTiposDocumentos() {
    const endpoint = `${base_url}/listar-documentos`;
    return this.http.get(endpoint);
  }

  obtenerCondicionIva(idAfip: number) {
    const endpoint = `${base_url}/get-condicion/${idAfip}`;
    return this.http.get(endpoint);
  }

  listarCondicionesIva() {
    const endpoint = `${base_url}/listar-condicion`;
    return this.http.get(endpoint);
  }

  obtenerAlicuotaIva(des: string) {
    const endpoint = `${base_url}/get-alicuota/${des}`;
    return this.http.get(endpoint);
  }

  listarAlicuotasIva() {
    const endpoint = `${base_url}/listar-alicuotas`;
    return this.http.get(endpoint);
  }

  testearConeccionAfip() {
    const endpoint = `${base_url}/test-connection`;
    return this.http.get(endpoint);
  }

  iniciarNumeracion() {
    const endpoint = `${base_url}/secuencias/init-numeracion`;
    return this.http.get(endpoint);
  }

  sincronizarNumeracion() {
    const endpoint = `${base_url}/secuencias/sincronizar`;
    return this.http.get(endpoint);
  }

  listarCiudades() {
    const endpoint = `${base_url}/listar-ciudades`;
    return this.http.get(endpoint);
  }

}
