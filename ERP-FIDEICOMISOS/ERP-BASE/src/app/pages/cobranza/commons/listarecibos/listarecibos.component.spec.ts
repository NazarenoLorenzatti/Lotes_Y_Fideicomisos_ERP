import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaRecibosComponent } from './listarecibos.component';

describe('ListarecibosComponent', () => {
  let component: ListaRecibosComponent;
  let fixture: ComponentFixture<ListaRecibosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListaRecibosComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ListaRecibosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
