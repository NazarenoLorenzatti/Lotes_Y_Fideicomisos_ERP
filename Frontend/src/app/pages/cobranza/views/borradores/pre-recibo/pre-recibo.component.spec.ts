import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreReciboComponent } from './pre-recibo.component';

describe('PreReciboComponent', () => {
  let component: PreReciboComponent;
  let fixture: ComponentFixture<PreReciboComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreReciboComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PreReciboComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
