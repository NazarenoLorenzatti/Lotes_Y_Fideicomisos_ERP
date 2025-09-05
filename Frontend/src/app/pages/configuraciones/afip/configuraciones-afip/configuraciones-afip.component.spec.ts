import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfiguracionesAfipComponent } from './configuraciones-afip.component';

describe('ConfiguracionesAfipComponent', () => {
  let component: ConfiguracionesAfipComponent;
  let fixture: ComponentFixture<ConfiguracionesAfipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfiguracionesAfipComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConfiguracionesAfipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
