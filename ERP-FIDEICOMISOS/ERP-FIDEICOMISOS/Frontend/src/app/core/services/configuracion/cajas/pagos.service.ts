import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8105/compras/cajas';

@Injectable({
  providedIn: 'root'
})
export class PagosService {

  constructor(private http: HttpClient) { }

  guardarCajaDePago(body: any) {
    const endpoint = `${base_url}/guardar`;
    return this.http.post(endpoint, body);
  }

  editarCajaDePago(body: any) {
    const endpoint = `${base_url}/editar`;
    return this.http.put(endpoint, body);
  }

  eliminarCajaDePago(id: number) {
    const endpoint = `${base_url}/eliminar/${id}`;
    return this.http.delete(endpoint);
  }

  cambiarEstadoCajaDePago(id: number) {
    let body = {};
    const endpoint = `${base_url}/cambiar-estado/${id}`;
    return this.http.put(endpoint, body);
  }

  obtenerCajaDePago(id: number) {
    const endpoint = `${base_url}/get/${id}`;
    return this.http.get(endpoint);
  }

  listarCajasDePago(id: number) {
    const endpoint = `${base_url}/listar`;
    return this.http.get(endpoint);
  }
}
