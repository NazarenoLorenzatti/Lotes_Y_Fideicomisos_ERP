import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8105/compras';
const op = '/orden-pago';
const minuta = '/pago';

@Injectable({
  providedIn: 'root'
})
export class PagosService {

  constructor(private http: HttpClient) { }

  crearOrdenDePago(body: any) {
    const endpoint = `${base_url}${op}/crear`;
    return this.http.post(endpoint, body);
  }

  editarOrdenDePago(body: any) {
    const endpoint = `${base_url}${op}/editar`;
    return this.http.put(endpoint, body);
  }

  cambiarEstadoOrdenDePago(formdata: FormData, id: number) {
    const endpoint = `${base_url}${op}/crear/${id}`;
    return this.http.put(endpoint, formdata);
  }

  obtenerOrdenDePago(id: number) {
    const endpoint = `${base_url}${op}/obtener/${id}`;
    return this.http.get(endpoint);
  }

  listarOrdenesDePago() {
    const endpoint = `${base_url}${op}/listar`;
    return this.http.get(endpoint);
  }

  /*
  ENPOINTS DE MINUTAS DE PAGO
  */

  crearMinutaDePago(body: any) {
    const endpoint = `${base_url}${minuta}/crear`;
    return this.http.post(endpoint, body);
  }

  editarinutaDePago(body: any) {
    const endpoint = `${base_url}${minuta}/editar`;
    return this.http.put(endpoint, body);
  }

  eliminarMinutaDePago(id: number) {
    const endpoint = `${base_url}${op}/eliminar/${id}`;
    return this.http.delete(endpoint);
  }

  confirmarMinutaDePago(id: number) {
    const endpoint = `${base_url}${op}/confirmar/${id}`;
    return this.http.get(endpoint);
  }

  rechazarMinutaDePago(id: number) {
    const endpoint = `${base_url}${op}/rechazar/${id}`;
    return this.http.get(endpoint);
  }

  pagarMinutaDePago(id: number) {
    const endpoint = `${base_url}${op}/pagar/${id}`;
    return this.http.get(endpoint);
  }

  obtenerMinutaDePago(id: number) {
    const endpoint = `${base_url}${op}/obtener/${id}`;
    return this.http.get(endpoint);
  }

  listarMinutasDePago() {
    const endpoint = `${base_url}${op}/listar`;
    return this.http.get(endpoint);
  }

}
