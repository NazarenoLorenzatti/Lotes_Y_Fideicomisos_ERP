import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { SplitButtonModule } from 'primeng/splitbutton';
import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { ProgressBarModule } from 'primeng/progressbar';
import { MultiSelectModule } from 'primeng/multiselect';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InputMaskModule } from 'primeng/inputmask';
import { InputSwitchModule } from 'primeng/inputswitch';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { ListaContactosComponent } from './lista-contactos/lista-contactos.component';

@NgModule({
  declarations: [
    ListaContactosComponent
  ],
  providers: [
    DatePipe ,
    MessageService,
    ConfirmationService,
  ],
  exports: [
    ButtonModule,
    SplitButtonModule,
    TableModule,
    ProgressBarModule,
    MultiSelectModule,
    DropdownModule,
    InputTextModule,
    TagModule,
    ToastModule,
    FormsModule,
    ReactiveFormsModule,
    InputMaskModule,
    InputSwitchModule,
    DialogModule,
    InputNumberModule,
    CalendarModule,
    OrganizationChartModule,
    ListaContactosComponent
  ],
  imports: [
    ButtonModule,
    SplitButtonModule,
    CommonModule,
    TableModule,
    ProgressBarModule,
    MultiSelectModule,
    DropdownModule,
    InputTextModule,
    TagModule,
    ToastModule,
    FormsModule,
    ReactiveFormsModule,
    InputMaskModule,
    InputSwitchModule,
    DialogModule,
    InputNumberModule,
    CalendarModule,
    OrganizationChartModule
  ]
})
export class SharedModule { }
