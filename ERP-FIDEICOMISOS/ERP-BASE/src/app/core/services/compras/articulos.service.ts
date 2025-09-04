import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8105/compras/articulos';

@Injectable({
  providedIn: 'root'
})
export class ArticulosService {

  constructor(private http: HttpClient) { }

  guardarArticulo(body: any) {
    const endpoint = `${base_url}/guardar`;
    return this.http.post(endpoint, body);
  }

  editarArticulo(body: any) {
    const endpoint = `${base_url}/editar`;
    return this.http.put(endpoint, body);
  }

  archivarArticulo(id: number) {
    const endpoint = `${base_url}/editar/${id}`;
    return this.http.get(endpoint);
  }

  listarArticulos() {
    const endpoint = `${base_url}/listar`;
    return this.http.get(endpoint);
  }

}
