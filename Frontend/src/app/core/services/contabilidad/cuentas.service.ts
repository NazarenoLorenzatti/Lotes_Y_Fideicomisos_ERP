import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8104/api/cuentas';

@Injectable({
  providedIn: 'root'
})
export class CuentasService {

  constructor(private http: HttpClient) { }

  guardar(body: any) {
    const endpoint = `${base_url}/save`;
    return this.http.post(endpoint, body);
  }

  editar(body: any) {
    const endpoint = `${base_url}/edit`;
    return this.http.put(endpoint, body);
  }

  eliminar(id: number) {
    const endpoint = `${base_url}/delete/${id}`;
    return this.http.delete(endpoint);
  }

  cambiarEstado(id: number) {
    const endpoint = `${base_url}/set-estado/${id}`;
    return this.http.get(endpoint);
  }

  obtener(id: number) {
    const endpoint = `${base_url}/get/${id}`;
    return this.http.get(endpoint);
  }

  listar() {
    const endpoint = `${base_url}/get-all`;
    return this.http.get(endpoint);
  }
}
