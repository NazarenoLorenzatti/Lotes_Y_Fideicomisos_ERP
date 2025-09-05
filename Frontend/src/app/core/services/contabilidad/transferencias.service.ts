import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8104/api/transferencias-internas';

@Injectable({
  providedIn: 'root'
})
export class TransferenciasService {

  constructor(private http: HttpClient) { }

  registrar(body: any) {
    const endpoint = `${base_url}/registrar`;
    return this.http.post(endpoint, body);
  }

  aprobar(id: number) {
    const endpoint = `${base_url}/aprobar/${id}`;
    return this.http.get(endpoint);
  }

  anular(id: number) {
    const endpoint = `${base_url}/anular/${id}`;
    return this.http.delete(endpoint);
  }

  obtener(id: number) {
    const endpoint = `${base_url}/get/${id}`;
    return this.http.get(endpoint);
  }

  listar(id: number) {
    const endpoint = `${base_url}/listar`;
    return this.http.get(endpoint);
  }

}
