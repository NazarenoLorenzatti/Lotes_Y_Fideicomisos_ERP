import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MinutaPagoComponent } from './minuta-pago.component';

describe('MinutaPagoComponent', () => {
  let component: MinutaPagoComponent;
  let fixture: ComponentFixture<MinutaPagoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MinutaPagoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MinutaPagoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
