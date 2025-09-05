import { ComponentFixture, TestBed } from '@angular/core/testing';

import {  ListPendientesComponent } from './list-pendientes.component';

describe('ListComponent', () => {
  let component: ListPendientesComponent;
  let fixture: ComponentFixture<ListPendientesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListPendientesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ListPendientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
