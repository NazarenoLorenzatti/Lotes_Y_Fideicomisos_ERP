import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8101/comprobantes';

@Injectable({
  providedIn: 'root'
})
export class FacturacionService {

  constructor(private http: HttpClient) { }

  crearPreComprobante(body: any, id: number) {
    const endpoint = `${base_url}/crear-precomprobante/${id}`;
    return this.http.post(endpoint, body);
  }

  editarPreComprobante(body: any) {
    const endpoint = `${base_url}/editar-precomprobante`;
    return this.http.put(endpoint, body);
  }

  obtenerPreComprobante(id: number) {
    const endpoint = `${base_url}/obtener/pre/${id}`;
    return this.http.get(endpoint);
  }

  confirmarPreComprobante(id: number) {
    let body = {};
    const endpoint = `${base_url}/confirmar-precomprobante/${id}`;
    return this.http.post(endpoint, body);
  }

  confirmarPreComprobanteAsociado(id: number, idAsociado: number) {
    let body = {};
    const endpoint = `${base_url}/confirmar-precomprobante/${id}/${idAsociado}`;
    return this.http.post(endpoint, body);
  }

  listarPreComprobantes() {
    const endpoint = `${base_url}/listar-precomprobantes`;
    return this.http.get(endpoint);
  }

  listarPreComprobantesPorEstado(body: any) {
    const endpoint = `${base_url}/listar-precomprobantes`;
    return this.http.post(endpoint, body);
  }

  listarComprobantes() {
    const endpoint = `${base_url}/listar-comprobantes`;
    return this.http.get(endpoint);
  }

  listarComprobantesAuxiliares() {
    const endpoint = `${base_url}/listar-comprobantesaux`;
    return this.http.get(endpoint);
  }

  eliminarPreComprobantes(id: number) {
    const endpoint = `${base_url}/eliminar-precomprobante/${id}`;
    return this.http.delete(endpoint);
  }

  obtenerComprobanteOficial(id: number) {
    const endpoint = `${base_url}/buscar-comprobante/${id}`;
    return this.http.get(endpoint);
  }

  obtenerComprobanteAuxiliar(id: number) {
    const endpoint = `${base_url}/buscar-comprobante-aux/${id}`;
    return this.http.get(endpoint);
  }
}
