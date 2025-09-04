import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContraReciboComponent } from './contra-recibo.component';

describe('ContraReciboComponent', () => {
  let component: ContraReciboComponent;
  let fixture: ComponentFixture<ContraReciboComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContraReciboComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ContraReciboComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
