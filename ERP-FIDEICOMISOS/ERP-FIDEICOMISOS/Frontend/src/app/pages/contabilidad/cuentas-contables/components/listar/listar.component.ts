import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CuentasService } from '../../../../../core/services/contabilidad/cuentas.service';
import { MessageService, TreeNode } from 'primeng/api';

interface NodoCuenta {
  id: number;
  codigo: string;
  nombre: string;
  tipo: string;
  estado: string;
}

@Component({
  selector: 'app-listar',
  templateUrl: './listar.component.html',
  styleUrl: './listar.component.css',
  host: {
    'class': 'w-full h-full block'
  }
})
export class ListarComponent implements OnInit {

  public loading: boolean = true;
  private router = inject(Router);
  public searchValue: any = '';
  private cuentasServices = inject(CuentasService);
  private messageService = inject(MessageService);
  public listCuentas: any[] = [];

  public selectedNodes!: TreeNode[];
  public treesOficiales: TreeNode[] = [];
  public treesAuxiliares: TreeNode[] = [];

  ngOnInit(): void {
    this.obtenerListaCuentas();
  }

  obtenerListaCuentas() {
    this.cuentasServices.listar().subscribe({
      next: (response: any) => this.handleResponseOkData(response),
      error: (error: any) => this.handleResponseErrorData(error),
    });
  }

  handleResponseOkData(response: any) {
    if (response?.metadata?.[0]?.codigo === '00') {
      this.listCuentas = response.response.response[0];
      this.treesOficiales = this.parseToTree(this.listCuentas, true);
      this.treesAuxiliares = this.parseToTree(this.listCuentas, false);
      this.loading = false;
    }
  }

  handleResponseErrorData(error: any) {
    console.error('Error fetching Current Account data:', error);
    this.showMessage(error?.metadata?.[0]?.informacion, error?.metadata?.[0]?.respuesta, 'danger')
  }

  parseToTree(cuentas: any[], oficial: boolean): TreeNode[] {
    const cuentasFiltradas = cuentas.filter(c => c.oficial === oficial);

    const rootNode: TreeNode = {
      label: oficial ? 'Plan de Cuentas Oficial' : 'Plan de Cuentas Auxiliar',
      type: 'root',
      expanded: true,
      children: cuentasFiltradas.map(cuenta => this.buildNodeRecursive(cuenta))
    };
    return [rootNode];  // Retorna un array con el nodo raíz
  }

  buildNodeRecursive(cuenta: any): TreeNode {
    const node: TreeNode = {
      label: `${cuenta.nombre}`,
      type: 'cuenta',
      data: {
        id: cuenta.id,
        codigo: cuenta.codigo,
        tipo: cuenta.tipo,
        activa: cuenta.activa,
        oficial: cuenta.oficial
      },
      expanded: false,
      children: []
    };

    if (cuenta.hijas && cuenta.hijas.length > 0) {
      node.children = cuenta.hijas.map((hija: any) => this.buildNodeRecursive(hija));
    }

    return node;
  }

  private showMessage(message: string, summary: string, severity: string): void {
    this.messageService.add({ severity: severity, summary: summary, detail: message });
  }

  goToAsiento(id: number) {
    this.router.navigate(["contabilidad/asientos/detalles", id]);
  }

  goTo(url: string) {
    this.router.navigate([url]);
  }

}
