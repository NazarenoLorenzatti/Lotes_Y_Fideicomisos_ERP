import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreComprobanteCommonComponent } from './pre-comprobante-common.component';

describe('PreComprobanteCommonComponent', () => {
  let component: PreComprobanteCommonComponent;
  let fixture: ComponentFixture<PreComprobanteCommonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreComprobanteCommonComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PreComprobanteCommonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
