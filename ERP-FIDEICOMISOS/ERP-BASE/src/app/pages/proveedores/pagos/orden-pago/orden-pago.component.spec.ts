import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdenPagoComponent } from './orden-pago.component';

describe('OrdenPagoComponent', () => {
  let component: OrdenPagoComponent;
  let fixture: ComponentFixture<OrdenPagoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrdenPagoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrdenPagoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
