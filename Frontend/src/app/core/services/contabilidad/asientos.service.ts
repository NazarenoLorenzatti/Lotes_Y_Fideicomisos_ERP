import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8104/api/asientos';

@Injectable({
  providedIn: 'root'
})
export class AsientosService {

  constructor(private http: HttpClient) { }

  registrarAsiento(body: any) {
    const endpoint = `${base_url}/registrar`;
    return this.http.post(endpoint, body);
  }

  revertirAsiento(body: any, id: number) {
    const endpoint = `${base_url}/${id}/revertir`;
    return this.http.post(endpoint, body);
  }

  conciliarMovimientos(body: any) {
    const endpoint = `${base_url}/conciliar`;
    return this.http.post(endpoint, body);
  }

  anularConciliaciones(id: number) {
    let body = {};
    const endpoint = `${base_url}/${id}/anular-conciliaciones`;
    return this.http.post(endpoint, body);
  }

  getAsiento(id: number) {
    const endpoint = `${base_url}/get-asiento/${id}`;
    return this.http.get(endpoint);
  }

  getAsientoByReferencia(referenciaExterna: string) {
    const endpoint = `${base_url}/get-asiento/referencia/${referenciaExterna}`;
    return this.http.get(endpoint);
  }

  getMovimiento(id: number) {
    const endpoint = `${base_url}/get-movimiento/${id}`;
    return this.http.get(endpoint);
  }

  getMovimientosPorAsiento(idAsiento: number) {
    const endpoint = `${base_url}/get-movimientos/${idAsiento}`;
    return this.http.get(endpoint);
  }

  getConciliacion(id: number) {
    const endpoint = `${base_url}/get-conciliacion/${id}`;
    return this.http.get(endpoint);
  }

  listarAsientos() {
    const endpoint = `${base_url}/listar-asientos`;
    return this.http.get(endpoint);
  }

  listarMovimientos() {
    const endpoint = `${base_url}/listar-movimientos`;
    return this.http.get(endpoint);
  }

  listarConciliaciones() {
    const endpoint = `${base_url}/listar-conciliaciones`;
    return this.http.get(endpoint);
  }
}
