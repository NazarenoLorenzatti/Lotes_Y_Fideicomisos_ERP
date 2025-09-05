import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreComprobanteComponent } from './pre-comprobante.component';

describe('PreComprobanteComponent', () => {
  let component: PreComprobanteComponent;
  let fixture: ComponentFixture<PreComprobanteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreComprobanteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PreComprobanteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
