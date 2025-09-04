import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { AfipService } from '../../../../core/services/afip/afip.service';
import { TipoDocumentos } from '../../../../core/Dtos/tipo-documentos.model';
import { CondicionesIva } from '../../../../core/Dtos/condiciones.model';
import { ContactoService } from '../../../../core/services/contactos/contacto.service';
import { ActivatedRoute, Router } from '@angular/router';

interface Ciudad {
  name: string;
  code: string;
}

@Component({
  selector: 'app-crear',
  templateUrl: './crear.component.html',
  styleUrl: './crear.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class CrearComponent implements OnInit {
  public ciudades: Ciudad[] | undefined;
  public tiposDocumentos!: TipoDocumentos[];
  public condicionesIva!: CondicionesIva[];
  public isCliente: boolean = true;
  public isProveedor: boolean = false;
  public dniMask: string = '9999999';
  private messageService = inject(MessageService);
  private contactosService = inject(ContactoService);
  private afipService = inject(AfipService);
  public formGroup!: FormGroup;
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  public id!: number;
  public contacto: any;
  public loading = true;

  ngOnInit() {
    this.getCondicionesIva();
    this.getCiudades();
    this.getTiposDocumentos();
    this.getIDContacto();
    this.formGroup = new FormGroup({
      selectedCity: new FormControl<Ciudad | null>(null, Validators.required),
      selectedTipoDocumento: new FormControl<TipoDocumentos | null>(null, Validators.required),
      selectedCondicionIva: new FormControl<CondicionesIva | null>(null, Validators.required),
      email: new FormControl<string | null>(null),
      dni: new FormControl<string | null>(null, Validators.required),
      nombre: new FormControl<string | null>(null, Validators.required),
      apellido: new FormControl<string | null>(null, Validators.required),
      direccion: new FormControl<string | null>(null, Validators.required),
      altura: new FormControl<string | null>(null, Validators.required),
      email_auxiliar: new FormControl<string | null>(null),
      celular: new FormControl<string | null>(null, Validators.required),
      celular_auxiliar: new FormControl<string | null>(null),
      telefono: new FormControl<string | null>(null, Validators.required),
      telefono_auxiliar: new FormControl<string | null>(null),
      isCliente: new FormControl<boolean>(false, Validators.required),
      isProveedor: new FormControl<boolean>(false, Validators.required)
    });
  }

  private getIDContacto(): void {
    this.route.paramMap.subscribe((params) => {
      this.id = Number(params.get('id'));
      if(this.id != 0){
        this.findContactoById(this.id);
      } else {
        this.loading = false;
      }
    });
  }

  findContactoById(id: number) {
    this.contactosService.obtenerContacto(id).subscribe({
      next: (response: any) => this.handleContactoData(response),
      error: (error: any) => this.handleContactoError(error),
    });
  }

  handleContactoData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.contacto = response.response.response[0];
      this.loading = false;
      const ciudad = this.ciudades?.find(c => c.name === this.contacto.localidad);
      const tipoDoc = this.tiposDocumentos.find(t => t.idAfip === this.contacto.idAfipTipoDocumento);
      const tipoCond = this.condicionesIva.find(i => i.idAfip === this.contacto.idCondicionIva);
      this.formGroup.patchValue({
        selectedCity: ciudad || null,
        selectedTipoDocumento: tipoDoc || null,
        selectedCondicionIva: tipoCond || null,
        email: this.contacto.email,
        dni: this.contacto.numeroDocumento,
        nombre: this.contacto.nombre,
        apellido: this.contacto.apellido,
        direccion: this.contacto.direccionFiscal,
        altura: this.contacto.altura,
        email_auxiliar: this.contacto.email_auxiliar,
        celular: this.contacto.celular,
        celular_auxiliar: this.contacto.celular_auxiliar,
        telefono: this.contacto.telefono,
        telefono_auxiliar: this.contacto.telefono_auxiliar,
        isCliente: this.contacto.isCliente,
        isProveedor: this.contacto.isProveedor
      });
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handleContactoError(error: any) {
    const errorMessage = error.error?.metadata[0]?.informacion || 'Ocurrió un error inesperado.';
    console.error('Error:', error);
    this.showMessage(errorMessage, 'Error', 'error');
  }

  checkDniChanges() {
    const dniControl = this.formGroup.get('dni');
    let tipoCondicion = this.formGroup.get('selectedCondicionIva')?.value.descripcion
    if (tipoCondicion == 'IVA Responsable Inscripto') {
      this.dniMask = '99-99999999-9';
      dniControl?.setValidators([Validators.required, Validators.pattern(/^\d{2}-\d{8}-\d{1}$/)]);
    } else {
      this.dniMask = '99999999';
      dniControl?.setValidators([Validators.required, Validators.pattern(/^\d{7,8}$/)]);
    }
    dniControl?.updateValueAndValidity();
  }


  getCondicionesIva() {
    this.afipService.listarCondicionesIva().subscribe({
      next: (response: any) => this.handleResponseCondiciones(response),
      error: (error: any) => console.error(error),
    });
  }

  getTiposDocumentos() {
    this.afipService.listarTiposDocumentos().subscribe({
      next: (response: any) => this.handleResponseTiposDocumentos(response),
      error: (error: any) => console.error(error),
    });
  }

  getCiudades() {
    this.afipService.listarCiudades().subscribe({
      next: (response: any) => this.handleResponseCities(response),
      error: (error: any) => console.error(error),
    });
  }

  handleResponseCities(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      const rawCities = response.response.response[0];
      this.ciudades = rawCities.map((c: any) => ({
        name: c.nombre,
        code: c.codigoPostal
      }));
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, 'Error', 'error');
    }
  }

  handleResponseTiposDocumentos(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      const rawDocs = response.response.response[0];
      this.tiposDocumentos = rawDocs.map((c: any) => ({
        id: c.id,
        idAfip: c.idAfip,
        descripcion: c.descripcion
      }));
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, 'Error', 'error');
    }
  }

  handleResponseCondiciones(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      const rawCondicionesIva = response.response.response[0];
      this.condicionesIva = rawCondicionesIva.map((c: any) => ({
        id: c.id,
        idAfip: c.idAfip,
        descripcion: c.descripcion,
      }));
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, 'Error', 'error');
    }
  }

  onSubmit() {
    this.contactosService.guardarContacto(this.createBody()).subscribe({
      next: (response: any) => this.handleResponseForm(response),
      error: (error: any) => this.showMessage(error?.message, 'Error', 'error'),
    })
  }

  handleResponseForm(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.showMessage(response?.metadata?.[0]?.informacion, 'Contacto Creado', 'success')
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, 'Error', 'error');
    }
  }

  createBody() {
    let body = {
      razonSocial: this.formGroup.get('nombre')?.value + " " + this.formGroup.get('apellido')?.value,
      nombre: this.formGroup.get('nombre')?.value,
      apellido: this.formGroup.get('apellido')?.value,
      tipoDocumento: this.formGroup.get('selectedTipoDocumento')?.value.descripcion,
      idAfipTipoDocumento: this.formGroup.get('selectedTipoDocumento')?.value.idAfip,
      numeroDocumento: this.formGroup.get('dni')?.value,
      direccionFiscal: this.formGroup.get('direccion')?.value,
      altura: this.formGroup.get('altura')?.value,
      pcia: "Santa Fe",
      localidad: this.formGroup.get('selectedCity')?.value.name,
      codigoPostal: this.formGroup.get('selectedCity')?.value.code,
      idCondicionIva: this.formGroup.get('selectedCondicionIva')?.value.idAfip,
      condicionIva: this.formGroup.get('selectedCondicionIva')?.value.descripcion,
      email: this.formGroup.get('email')?.value,
      email_auxiliar: this.formGroup.get('email_auxiliar')?.value,
      celular: this.formGroup.get('celular')?.value,
      celular_auxiliar: this.formGroup.get('celular_auxiliar')?.value,
      isCliente: this.formGroup.get('isCliente')?.value,
      isProveedor: this.formGroup.get('isProveedor')?.value
    }
    return body;
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }
}
