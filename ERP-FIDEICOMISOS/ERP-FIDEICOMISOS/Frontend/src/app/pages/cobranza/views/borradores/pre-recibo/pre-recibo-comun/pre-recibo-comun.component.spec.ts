import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreReciboComunComponent } from './pre-recibo-comun.component';

describe('PreReciboComunComponent', () => {
  let component: PreReciboComunComponent;
  let fixture: ComponentFixture<PreReciboComunComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreReciboComunComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PreReciboComunComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
