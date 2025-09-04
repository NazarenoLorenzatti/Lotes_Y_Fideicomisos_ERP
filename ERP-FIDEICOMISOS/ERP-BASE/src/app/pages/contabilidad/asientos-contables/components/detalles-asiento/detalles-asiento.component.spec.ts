import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetallesAsientoComponent } from './detalles-asiento.component';

describe('DetallesAsientoComponent', () => {
  let component: DetallesAsientoComponent;
  let fixture: ComponentFixture<DetallesAsientoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetallesAsientoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DetallesAsientoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
