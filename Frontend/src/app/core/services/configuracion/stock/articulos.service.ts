import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8101/articulos';

@Injectable({
  providedIn: 'root'
})
export class ArticulosService {

  constructor(private http: HttpClient) { }

  guardar(body: any) {
    const endpoint = `${base_url}/guardar`;
    return this.http.post(endpoint, body);
  }

  editar(body: any) {
    const endpoint = `${base_url}/editar`;
    return this.http.put(endpoint, body);
  }

  archivar(id: number) {
    const endpoint = `${base_url}/archivar/${id}`;
    return this.http.get(endpoint);
  }

  obtener(id: number) {
    const endpoint = `${base_url}/obtener/${id}`;
    return this.http.get(endpoint);
  }

  listar() {
    const endpoint = `${base_url}/listar`;
    return this.http.get(endpoint);
  }
}
