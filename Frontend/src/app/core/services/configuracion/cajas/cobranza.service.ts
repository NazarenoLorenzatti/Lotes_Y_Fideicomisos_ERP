import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8103/cajas';

@Injectable({
  providedIn: 'root'
})
export class CobranzaService {

    constructor(private http: HttpClient) { }
  
    guardarCajaDeCobranza(body: any) {
      const endpoint = `${base_url}/guardar`;
      return this.http.post(endpoint, body);
    }
  
    editarCajaDeCobranza(body: any) {
      const endpoint = `${base_url}/editar`;
      return this.http.put(endpoint, body);
    }
  
    eliminarCajaDeCobranza(id: number) {
      const endpoint = `${base_url}/eliminar/${id}`;
      return this.http.delete(endpoint);
    }
  
    cambiarEstadoCajaDeCobranza(id: number) {
      let body = {};
      const endpoint = `${base_url}/cambiar-estado/${id}`;
      return this.http.put(endpoint, body);
    }
  
    obtenerCajaDeCobranza(id: number) {
      const endpoint = `${base_url}/get/${id}`;
      return this.http.get(endpoint);
    }
  
    listarCajasDeCobranza() {
      const endpoint = `${base_url}/listar/cobranza`;
      return this.http.get(endpoint);
    }
}
