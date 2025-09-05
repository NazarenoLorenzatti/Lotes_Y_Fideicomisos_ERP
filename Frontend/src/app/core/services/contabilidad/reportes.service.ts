import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

const base_url = 'http://localhost:8104/api/contabilidad/reportes';

@Injectable({
  providedIn: 'root'
})
export class ReportesService {

  constructor(private http: HttpClient) { }

  generarLibroDiario(formData: FormData, id: number) {
    const endpoint = `${base_url}/get/${id}`;
    return this.http.post(endpoint, formData);
  }

  generarLibroMayor(formData: FormData) {
    const endpoint = `${base_url}/get-all`;
    return this.http.post(endpoint, formData);
  }
}
