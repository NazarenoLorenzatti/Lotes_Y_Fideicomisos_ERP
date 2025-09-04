import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8102/cuentas';

@Injectable({
  providedIn: 'root'
})
export class CuentaCorrienteService {

  constructor(private http: HttpClient) { }

  buscarCuentaCorriente(body: any) {
    const endpoint = `${base_url}/cuenta-corriente`;
    return this.http.post(endpoint, body);
  }

  registrarMovimiento(body: any, id: number) {
    const endpoint = `${base_url}/registrar-movimiento/${id}`;
    return this.http.post(endpoint, body);
  }

  eliminarAplicacion(id: number) {
    const endpoint = `${base_url}/delete/${id}`;
    return this.http.delete(endpoint);
  }

  registrarAplicacion(body: any, id: number) {
    const endpoint = `${base_url}/aplicacion`;
    return this.http.post(endpoint, body);
  }

  obtenerAplicacionesOrigen(id: number) {
    const endpoint = `${base_url}/get/aplicaciones/origen/${id}`;
    return this.http.get(endpoint);
  }

    obtenerAplicacionesDestino(id: number) {
    const endpoint = `${base_url}/get/aplicaciones/destino/${id}`;
    return this.http.get(endpoint);
  }

}
