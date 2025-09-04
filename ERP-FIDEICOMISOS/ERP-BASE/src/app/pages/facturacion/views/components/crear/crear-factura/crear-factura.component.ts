import { Component, inject, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { ContactoService } from '../../../../../../core/services/contactos/contacto.service';
import { Articulo, Item } from '../../../../../../core/Dtos/Item-facturacion.model';
import { FacturasService } from '../../../../../../core/services/compras/facturas.service';
import { ArticulosService } from '../../../../../../core/services/configuracion/stock/articulos.service';
import { AfipService } from '../../../../../../core/services/afip/afip.service';
import { CuitEmisor, PuntosDeVenta } from '../../../../../../core/Dtos/puntos-venta.model';
import { FacturacionService } from '../../../../../../core/services/facturacion/facturacion.service';

interface Concepto {
  id: number,
  desc: string
}

interface Moneda {
  name: string,
}

/*interface TipoServicio {
  name: string;
}*/

@Component({
  selector: 'app-crear-factura',
  templateUrl: './crear-factura.component.html',
  styleUrl: './crear-factura.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class CrearFacturaComponent implements OnInit {

  @Input() preComprobante: any = {};
  @Input() contacto!: any;

  public formGroup!: FormGroup;
  private messageService = inject(MessageService);
  private facturaService = inject(FacturacionService);
  private articuloService = inject(ArticulosService);
  private afipService = inject(AfipService);
  private router = inject(Router);
  //private route = inject(ActivatedRoute);
  public loading = true;

  // Inputs Item
  cantidad: number = 0;
  //tipo: string = '';
  descripcion: string = '';
  precioSinIva: number = 0;
  precioConIva: number = 0;
  bonificacionPorcentaje: number = 0;
  bonificacion: number = 0;
  iva: number = 0;
  public articulos: Articulo[] = [];
  public itemsFacturar: any[] = [];
  public selectedArticulo: Articulo | undefined;
  public itemColumns: string[] = [];
  public ptos_venta: PuntosDeVenta[] = [];
  public selectedPuntoVenta!: PuntosDeVenta;
  public cuit!: CuitEmisor;
  public monedas: Moneda[] = [];
  public selectedMonedas!: Moneda;
  public conceptos: Concepto[] = [];
  public selectedConceptos!: Concepto;
  public alicuotasIva: any[] = [];
  public selectedAlicuotaIva!: any;
  public tiposComprobantes: any[] = [];
  public selectedTiposComprobantes!: any;
  public tiposServicios: any[] = [];
  public selectedTipoServicio!: any;
   private contactosService = inject(ContactoService);
  visible: boolean = false;

  ngOnInit(): void {
    this.initMonedasYConceptos()
    this.getPuntosDeVenta();
    this.getArticulos();
    this.getAlicuotas();
    this.getTiposComprobantes();
    this.initForm();
  }

  initMonedasYConceptos() {
    this.monedas.push({ name: "PES" }, { name: "DOL" });
    this.conceptos.push({ id: 1, desc: "Producto" }, { id: 1, desc: "Servicios" }, { id: 1, desc: "Productos y Servicios" })
    this.tiposServicios.push({ name: 'SERVICIO' }, { name: 'PRODUCTO' });
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

  showDialog() {
    this.visible = true;
  }

  initForm() {
    this.formGroup = new FormGroup({
      fecha: new FormControl<Date | null>(null, Validators.required),
      fecha_vto: new FormControl<Date | null>(null, Validators.required),
      fecha_desde: new FormControl<Date | null>(null, Validators.required),
      fecha_hasta: new FormControl<Date | null>(null, Validators.required),
      moneda: new FormControl<Moneda | null>(null, Validators.required),
      pto_venta: new FormControl<PuntosDeVenta | null>(null, Validators.required),
      tipo_comprobante: new FormControl<any | null>(null, Validators.required),
      oficial: new FormControl<boolean>(false, Validators.required)
    });
  }

  getAlicuotas() {
    this.afipService.listarAlicuotasIva().subscribe({
      next: (response: any) => this.handleAlicuotasData(response),
      error: (error: any) => {
        console.error('Error fetching Current Account data:', error);
        this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
      },
    });
  }

  getTiposComprobantes() {
    this.afipService.listarTiposComprobantes().subscribe({
      next: (response: any) => this.handleTiposComprobantes(response),
      error: (error: any) => {
        console.error('Error fetching Current Account data:', error);
        this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
      },
    });
  }

  getArticulos() {
    this.articuloService.listar().subscribe({
      next: (response: any) => this.handleArticuloData(response),
      error: (error: any) => {
        console.error('Error fetching Current Account data:', error);
        this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
      },
    });
  }

  getPuntosDeVenta() {
    this.afipService.listarPuntosDeVentaCuit("20372998327").subscribe({
      next: (response: any) => this.handlePuntosVentasData(response),
      error: (error: any) => {
        console.error('Error fetching Current Account data:', error);
        this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
      },
    });
  }

  handleArticuloData(response: any): void {
    if (response?.metadata?.[0]?.codigo === '00') {
      let rawArticulo = response?.response?.response[0];
      rawArticulo.filter((a: any) => a.archivar === false).forEach((a: any) => {
        this.articulos.push(a);
      });;
    }
  }

  handlePuntosVentasData(response: any) {
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

  handleAlicuotasData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.alicuotasIva = response.response.response[0];
    }
  }

  handleTiposComprobantes(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.tiposComprobantes = response.response.response[0];
    }
  }

  guardarComprobante() {
    let body = {
      cuit_emisor: this.cuit.cuit,
      cbte_fecha: this.formatFecha(this.formGroup.get('fecha')?.value),
      fecha_desde: this.formatFecha(this.formGroup.get('fecha_desde')?.value),
      fecha_hasta: this.formatFecha(this.formGroup.get('fecha_hasta')?.value),
      fecha_vto: this.formatFecha(this.formGroup.get('fecha_vto')?.value),
      importe_neto: this.totalNeto,
      importe_iva: this.totalNetoIva,
      importe_gravado: this.totalNetoGravado,
      importe_total: this.totalGeneral,
      concepto: this.setConcepto(),
      moneda: this.formGroup.get('moneda')?.value.name,
      moneda_cotizacion: 1.0,
      oficial: this.formGroup.get('oficial')?.value,
      puntoVenta: this.formGroup.get('pto_venta')?.value.nroPtoVenta,
      nombrePtoVenta: this.formGroup.get('pto_venta')?.value.nombrePtoVenta,
      idAfipTipoComprobante: this.formGroup.get('tipo_comprobante')?.value.idAfip,
      descripcionTipoComprobante: this.formGroup.get('tipo_comprobante')?.value.descripcion,
      abrebiaturaTipoComprobante: this.formGroup.get('tipo_comprobante')?.value.abrebiatura,
      items: this.itemsFacturar
    }
    console.log('Comprobante',body)
    this.facturaService.crearPreComprobante(body, this.contacto.id).subscribe({
      next: (response: any) => this.handlePreComprobante(response),
      error: (error: any) => {
        console.error('Error fetching Current Account data:', error);
        this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
      },
    });
  }

  handlePreComprobante(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.showMessage(response?.metadata?.[0]?.informacion, response?.metadata?.[0]?.respuesta, 'success')
      this.cleanForms();
      this.goToPreComprobante(response.response.response[0].id);
    } else {
      this.showMessage(response?.metadata?.[0]?.informacion, response?.metadata?.[0]?.respuesta, 'danger')
    }

  }
  addItem() {
    const articulo: Articulo = this.selectedArticulo ?? {
      id: 999999,
      codigo: "cargado manualmente",
      descripcion: "cargado manualmente",
      desIva: this.selectedAlicuotaIva.descripcion,
      idAlicuotaAfip: this.selectedAlicuotaIva.id_Afip,
      tipo: this.selectedTipoServicio.name,
      iva: this.selectedAlicuotaIva.value,
      monto_iva: this.precioConIva - this.precioSinIva,
      precioUnitario: this.precioSinIva,
      precioUnitarioConIva: this.precioConIva,
      importeGravado: this.precioSinIva,
      importeNoGravado: 0.0,
      idCuentaContable: 10,
      nroCuentaContable: '1.100.000',
      nombreCuentaContable: 'Deudores Por Ventas',
      idCuentaContableAux: 16,
      nroCuentaContableAux: '91.100.000',
      nombreCuentaContableAux: 'Deudores por Ventas Aux',
      archivar: false
    }
    const newItem: Item = {
      articulo: articulo,
      cantidad: this.cantidad,
      bonificacion: this.bonificacion,
      importeTotalIva: articulo.monto_iva * this.cantidad,
      importeTotalSinIva: (articulo.precioUnitario * this.cantidad) - this.bonificacion,
      importeTotalConIva: (articulo.precioUnitarioConIva * this.cantidad) - this.bonificacion,
      descripcionAlicuota: articulo.desIva,
      idAfipAlicuotaIva: articulo.idAlicuotaAfip,
      ImporteNetoNoGravado: 0.0,
      ImporteNetoNoExcento: 0.0,
      OtrosTributos: 0.0
    }
    this.itemsFacturar.push(newItem);
    this.setColumns();
  }

  setColumns() {
    if (this.itemsFacturar.length > 0) {
      this.itemColumns = Object.keys(this.itemsFacturar[0]);
      this.itemColumns.push('Importe Neto Total', 'Importe Neto No Gravado', 'Importe Neto No Excento', 'Importe Neto Iva', 'Total')
    }
  }

  eliminarItem(index: number): void {
    this.itemsFacturar.splice(index, 1);
    this.setColumns();
  }

  calcularBonificacion() {
    if (this.bonificacionPorcentaje != 0) {
      this.precioSinIva = this.selectedArticulo?.precioUnitario ?? this.precioSinIva;
      this.bonificacion = ((this.precioSinIva * this.cantidad) * this.bonificacionPorcentaje) / 100;
    }
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }

  goToPreComprobante(id: number) {
    this.router.navigate(["facturacion/views/pre-comprobante", id ]);
  }

    handleError(error: any): void {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error.error?.metadata[0]?.informacion, error.error?.metadata?.[0]?.respuesta, 'error')
  }

  cleanForms() {
    this.formGroup.reset();
    this.formGroup.markAsPristine();
    this.formGroup.markAsUntouched();
    this.itemsFacturar = [];
  }

  setConcepto(): Number {
    const tieneProducto = this.itemsFacturar.some((i: any) => i.articulo?.tipo === 'PRODUCTO');
    const tieneServicio = this.itemsFacturar.some((i: any) => i.articulo?.tipo === 'SERVICIO');
    if (tieneProducto && tieneServicio) {
      return 3; // Productos y Servicios
    } else if (tieneProducto) {
      return 1; // Solo Productos
    } else if (tieneServicio) {
      return 2; // Solo Servicios
    } else {
      return 3; // Ninguno o error
    }
  }

  formatFecha(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${year}${month}${day}`;
  }


  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }


  get totalGeneral(): number {
    return this.itemsFacturar.reduce((acc, item) => acc + (Number(item.importeTotalConIva) || 0), 0);
  }

  get totalNetoNoGravado(): number {
    return this.itemsFacturar.reduce((acc, item) => acc + (Number(item.ImporteNetoNoGravado) || 0), 0);
  }

  get totalNetoGravado(): number {
    return this.itemsFacturar.reduce((acc, item) => acc + (Number(item.importeTotalSinIva) || 0), 0);
  }

  get totalNetoIva(): number {
    return this.itemsFacturar.reduce((acc, item) => acc + (Number(item.importeTotalIva) || 0), 0);
  }

  get totalNeto(): number {
    return this.itemsFacturar.reduce((acc, item) => acc + (Number(item.importeTotalSinIva) || 0), 0);
  }


}
