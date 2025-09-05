import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CajasCobranzaComponent } from './cajas-cobranza.component';

describe('CajasCobranzaComponent', () => {
  let component: CajasCobranzaComponent;
  let fixture: ComponentFixture<CajasCobranzaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CajasCobranzaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CajasCobranzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
