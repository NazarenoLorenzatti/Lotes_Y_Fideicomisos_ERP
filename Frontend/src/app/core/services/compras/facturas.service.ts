import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8105/compras/articulos';

@Injectable({
  providedIn: 'root'
})
export class FacturasService {

  constructor(private http: HttpClient) { }

  guardarFactura(body: any, id: number) {
    const endpoint = `${base_url}/guardar/${id}`;
    return this.http.post(endpoint, body);
  }

  editarFactura(body: any) {
    const endpoint = `${base_url}/editar`;
    return this.http.put(endpoint, body);
  }

  eliminarFactura(id: number) {
    const endpoint = `${base_url}/eliminar/${id}`;
    return this.http.delete(endpoint);
  }

  cambiarEstado(formdata: FormData) {
    const endpoint = `${base_url}/cambiar-estado`;
    return this.http.post(endpoint, formdata);
  }

  obtenerFactura(id: number) {
    const endpoint = `${base_url}/obtener/${id}`;
    return this.http.get(endpoint);
  }

  agregarDetalles(body: any) {
    const endpoint = `${base_url}/agregar`;
    return this.http.post(endpoint, body);
  }

  listarFacturas() {
    const endpoint = `${base_url}/obtener`;
    return this.http.get(endpoint);
  }

}
