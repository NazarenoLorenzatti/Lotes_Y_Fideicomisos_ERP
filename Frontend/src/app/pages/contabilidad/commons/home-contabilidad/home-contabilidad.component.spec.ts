import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeContabilidadComponent } from './home-contabilidad.component';

describe('HomeContabilidadComponent', () => {
  let component: HomeContabilidadComponent;
  let fixture: ComponentFixture<HomeContabilidadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomeContabilidadComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HomeContabilidadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
