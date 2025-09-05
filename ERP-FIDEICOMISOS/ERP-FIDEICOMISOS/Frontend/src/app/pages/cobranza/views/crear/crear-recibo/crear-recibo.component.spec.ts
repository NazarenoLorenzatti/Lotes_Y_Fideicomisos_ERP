import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearReciboComponent } from './crear-recibo.component';

describe('CrearreciboComponent', () => {
  let component: CrearReciboComponent;
  let fixture: ComponentFixture<CrearReciboComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CrearReciboComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CrearReciboComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
