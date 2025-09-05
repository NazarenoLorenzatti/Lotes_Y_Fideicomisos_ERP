import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8102/contactos';

@Injectable({
  providedIn: 'root'
})
export class ContactoService {

  constructor(private http: HttpClient) { }

  obtenerContacto(id: number) {
    const endpoint = `${base_url}/find/${id}`;
    return this.http.get(endpoint);
  }

  listarContactos() {
    const endpoint = `${base_url}/find-all`;
    return this.http.get(endpoint);
  }

  guardarContacto(body: any) {
    const endpoint = `${base_url}/saved`;
    return this.http.post(endpoint, body);
  }

  editarContacto(body: any) {
    const endpoint = `${base_url}/saved`;
    return this.http.put(endpoint, body);
  }

  confirmarContacto(id: number) {
    const endpoint = `${base_url}/confirm/${id}`;
    return this.http.get(endpoint);
  }

  archivarContacto(id: number) {
    const endpoint = `${base_url}/archivar/${id}`;
    return this.http.get(endpoint);
  }

  eliminarContacto(id: number) {
    const endpoint = `${base_url}/delete/${id}`;
    return this.http.delete(endpoint);
  }

  bajaTemprana(id: number) {
    const endpoint = `${base_url}/baja-temprana/${id}`;
    return this.http.get(endpoint);
  }

  baja(id: number) {
    const endpoint = `${base_url}/baja/${id}`;
    return this.http.get(endpoint);
  }

  listarEstados() {
    const endpoint = `${base_url}/estados`;
    return this.http.get(endpoint);
  }
}
