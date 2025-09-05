import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8103/recibos';

@Injectable({
  providedIn: 'root'
})
export class RecibosService {

  constructor(private http: HttpClient) { }

  crearPreRecibo(body: any, id: number) {
    const endpoint = `${base_url}/crear-pre-recibo/${id}`;
    return this.http.post(endpoint, body);
  }

  editarPreRecibo(body: any) {
    const endpoint = `${base_url}/editar-pre-recibo`;
    return this.http.put(endpoint, body);
  }

  confirmarPreRecibo(id: number) {
    let body = {};
    const endpoint = `${base_url}/confirmar-prerecibo/${id}`;
    return this.http.post(endpoint, body);
  }

  contraRecibo(id: number) {
    let body = {};
    const endpoint = `${base_url}/contrarecibo/${id}`;
    return this.http.post(endpoint, body);
  }

  confirmarPreReciboAsociado(id: number, idAsociado: number) {
    let body = {};
    const endpoint = `${base_url}/confirmar-prerecibo/${id}${idAsociado}`;
    return this.http.post(endpoint, body);
  }

  listarPreRecibos() {
    const endpoint = `${base_url}/listar-pre-recibos`;
    return this.http.get(endpoint);
  }
  
  listarPreRecibosPorEstado(body:any) {
    const endpoint = `${base_url}/listar-pre-recibos`;
    return this.http.post(endpoint, body);
  }

  listarRecibos() {
    const endpoint = `${base_url}/listar-recibos`;
    return this.http.get(endpoint);
  }

  listarRecibosAuxiliares() {
    const endpoint = `${base_url}/listar-recibos/aux`;
    return this.http.get(endpoint);
  }

  eliminarPreRecibos(id: number) {
    const endpoint = `${base_url}/eliminar-pre-recibo/${id}`;
    return this.http.delete(endpoint);
  }

  obtenerPreRecibo(id: number){
    const endpoint = `${base_url}/pre-recibo/obtener/${id}`;
    return this.http.get(endpoint);
  }

  buscarRecibo(id: number) {
    const endpoint = `${base_url}/buscar-recibo/${id}`;
    return this.http.get(endpoint);
  }

  buscarReciboAux(id: number) {
    const endpoint = `${base_url}/buscar-recibo-aux/${id}`;
    return this.http.get(endpoint);
  }
}
