import { Component, Inject, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RecibosService } from '../../../../../core/services/cobranza/recibos.service';
import { CobranzaService } from '../../../../../core/services/configuracion/cajas/cobranza.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CuitEmisor, PuntosDeVenta } from '../../../../../core/Dtos/puntos-venta.model';
import { AfipService } from '../../../../../core/services/afip/afip.service';
import { MessageService } from 'primeng/api';
import { ContactoService } from '../../../../../core/services/contactos/contacto.service';
import { DatePipe } from '@angular/common';

interface Imputacion {
  importeImputado: number;
  cajaCobranza: any;
  fechaRealPago: String;
  tipo: string;
  nota: string;
  nroComprobante: string;
  bancoOrigen: string;
}

interface Tipo {
  name: string;
}

interface Moneda {
  name: string,
  cotizacion: number
}

@Component({
  selector: 'app-crear-recibo',
  templateUrl: './crear-recibo.component.html',
  styleUrl: './crear-recibo.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class CrearReciboComponent implements OnInit {


  @Input() contacto: any = {};
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private reciboService = inject(RecibosService);
  private cajaCobranza = inject(CobranzaService);
  private messageService = inject(MessageService);
  private contactosService = inject(ContactoService);
  private afipService = inject(AfipService);
  private datePipe = inject(DatePipe);

  public ptos_venta: PuntosDeVenta[] = [];
  public selectedPuntoVenta!: PuntosDeVenta;

  public tipos: Tipo[] = [];
  public seletedTipo!: Tipo;
  public cajas: any[] = [];
  public rawCajas: any[] = [];
  public selectedCajas!: any;
  public importeImputado: number = 0;
  public fechaRealPago: Date | null = null;
  public nota!: string;
  public nroComprobante!: string;
  public bancoOrigen!: string;
  public oficial: boolean = true;
  public imputaciones: Imputacion[] = [];
  public cuit!: CuitEmisor;
  public formGroup!: FormGroup;
  public visible = false;
  public monedas: Moneda[] = [];
  public selectedMonedas!: Moneda;

  ngOnInit(): void {
    this.getCajas();
    this.initForm();
    this.getPuntosDeVenta();
    this.tipos.push(
      { name: "EFECTIVO" }, { name: "TRANSFERENCIA" },
      { name: "CHEQUE" }, { name: "TARJETA_C" },
      { name: "TARJETA_D" }, { name: "RETENCION" })
  }

  initForm() {
    this.formGroup = new FormGroup({
      fecha: new FormControl<Date | null>(null, Validators.required),
      moneda: new FormControl<Moneda | null>(null, Validators.required),
      pto_venta: new FormControl<PuntosDeVenta | null>(null, Validators.required),
    });
    this.monedas.push({ name: "PES", cotizacion: 1.0 }, { name: "DOL", cotizacion: 1.0 });
  }

  getCajas() {
    this.cajaCobranza.listarCajasDeCobranza().subscribe({
      next: (response: any) => this.handleCajasOkData(response),
      error: (error: any) => this.handleError(error),
    });
  }

  getPuntosDeVenta() {
    this.afipService.listarPuntosDeVentaCuit("20372998327").subscribe({
      next: (response: any) => this.handlePuntosVentasData(response),
      error: (error: any) => this.handleError(error)
    });
  }

  handleCajasOkData(response: any): void {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.rawCajas = response.response.response[0];
      this.setCajasDisponibles();
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  setCajasDisponibles() {
    this.cajas = [];
    if (this.seletedTipo?.name != 'RETENCION') {
      this.rawCajas.filter((c: any) => c.oficial === this.oficial && c.activa && c.cajaCobranza && !c.retenciones)
        .forEach((c: any) => {
          this.cajas.push(c);
        });
    } else {
      this.rawCajas.filter((c: any) => c.oficial === this.oficial && c.activa && c.retenciones)
        .forEach((c: any) => {
          this.cajas.push(c);
        });
    }
  }

  onSeleccionarCliente(contacto: any) {
    this.findContactoById(contacto.id);
    this.showDialog();
  }

  findContactoById(id: number) {
    this.contactosService.obtenerContacto(id).subscribe({
      next: (response: any) => this.handleContactoData(response),
      error: (error: any) => this.handleError(error),
    });
  }

  handleContactoData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.contacto = response.response.response[0];
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  handlePuntosVentasData(response: any) {
    console.log(response)
    if (response?.metadata?.[0]?.codigo === '00') {
      const rawPtos = response.response.response[0];
      this.cuit = {
        id: rawPtos[0].cuit?.id,
        cuit: rawPtos[0].cuit?.cuit,
        ingresosBrutos: rawPtos[0].cuit?.ingresosBrutos,
        inicioActividades: rawPtos[0].cuit?.inicioActividades,
        razonSocial: rawPtos[0].cuit?.razonSocial,
        direccionFiscal: rawPtos[0].cuit?.direccionFiscal,
        nombreFantasia: rawPtos[0].cuit?.nombreFantasia,
      };
      rawPtos
        .filter((p: any) => p.habilitado === true && p.facturacionRecurrente !== true)
        .forEach((p: any) => {
          const pto: PuntosDeVenta = {
            id: p.id,
            nroPtoVenta: p.nroPtoVenta,
            nombrePtoVenta: p.nombrePtoVenta,
            facturacionRecurrente: p.facturacionRecurrente ?? false,
            habilitado: p.habilitado ?? true,
            cuit: this.cuit
          };
          this.ptos_venta.push(pto);
        });
    }
  }

  addItem() {
    if (this.selectedCajas && this.fechaRealPago && this.importeImputado) {
      const imp: Imputacion = {
        importeImputado: this.importeImputado,
        cajaCobranza: this.selectedCajas,
        fechaRealPago: this.datePipe.transform(this.fechaRealPago, 'dd-MM-yyyy') || '',
        tipo: this.seletedTipo.name,
        nota: this.nota,
        nroComprobante: this.nroComprobante,
        bancoOrigen: this.bancoOrigen
      }
      this.imputaciones.push(imp);
    } else {
      this.showMessage('Alerta', "Debe completar la informacion requerida", 'warn');
    }
  }

  eliminarItem(index: number): void {
    this.imputaciones.splice(index, 1);
  }

  handleError(error: any): void {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
  }

  guardarRecibo() {
    let body = {
      cuit_emisor: this.cuit.cuit,
      recibo_fecha: this.formatFecha(this.formGroup.get('fecha')?.value),
      fecha_desde: this.formatFecha(this.formGroup.get('fecha')?.value),
      fecha_hasta: this.formatFecha(this.formGroup.get('fecha')?.value),
      fecha_vto: this.formatFecha(this.formGroup.get('fecha')?.value),
      importe_neto: this.totalGeneral / 1.21,
      importe_iva: 0,
      importe_gravado: 0,
      importe_total: this.totalGeneral,
      concepto: 3,
      idAfipAlicuotaIva: 3,
      descripcionIva: "0%",
      moneda: this.formGroup.get('moneda')?.value.name,
      moneda_cotizacion: this.formGroup.get('moneda')?.value.cotizacion,// ES OBLIGATORIO EL USO DE MONEDA ENTONCES TENGO QUE ADAPTARLO
      oficial: this.oficial,
      puntoVenta: this.formGroup.get('pto_venta')?.value.nroPtoVenta,
      nombrePtoVenta: this.formGroup.get('pto_venta')?.value.nombrePtoVenta,
      idAfipTipoRecibo: this.contacto.idCondicionIva === 1 ? 4 : 9, // Si idCondicion es 1 es RI por eso factura A que es id 1 sino Factura B q es 6
      descripcionTipoRecibo: this.contacto.idCondicionIva === 1 ? 'RECIBO A' : 'RECIBO B',
      abrebiaturaTipoRecibo: this.contacto.idCondicionIva === 1 ? 'RA' : 'RB',
      imputaciones: this.imputaciones

    }
    this.reciboService.crearPreRecibo(body, this.contacto.id).subscribe({
      next: (response: any) => this.handlePreReciboData(response),
      error: (error: any) => this.handleError(error),
    });
  }

  handlePreReciboData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.showMessage(response?.metadata?.[0]?.info, 'Ok', 'success');
      this.cleanForms();
      this.visible = false;
      this.goToPreRecibo(response.response.response[0].id);
    } else {
      this.showMessage(response?.metadata?.[0]?.info, 'Error', 'error');
    }
  }

  goToPreRecibo(id: number) {
    this.router.navigate(["cobranza/views/pre-recibo", id]);
  }


  goTo(url: string) {
    this.router.navigate([url]);
  }

  showDialog() {
    this.visible = true;
  }

  formatFecha(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${year}${month}${day}`;
  }

  cleanForms() {
    this.formGroup.reset();
    this.formGroup.markAsPristine();
    this.formGroup.markAsUntouched();
    this.importeImputado = 0;
    this.selectedCajas = undefined;
    this.fechaRealPago = null;
    this.seletedTipo.name = '';
    this.nota = '';
    this.nroComprobante = '';
    this.bancoOrigen = '';
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  get totalGeneral(): number {
    return this.imputaciones.reduce((acc, imp) => acc + (Number(imp.importeImputado) || 0), 0);
  }

  get totalRetenciones(): number {
    return this.imputaciones
      .filter(imp => imp.tipo === 'RETENCION')
      .reduce((acc, imp) => acc + (Number(imp.importeImputado) || 0), 0);
  }

  get nuevoSaldo(): number {
    return this.contacto?.cuentaCorriente?.saldo + this.totalGeneral;
  }
}
