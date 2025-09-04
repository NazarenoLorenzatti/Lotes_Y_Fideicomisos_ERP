import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CajasPagosComponent } from './cajas-pagos.component';

describe('CajasPagosComponent', () => {
  let component: CajasPagosComponent;
  let fixture: ComponentFixture<CajasPagosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CajasPagosComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CajasPagosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
