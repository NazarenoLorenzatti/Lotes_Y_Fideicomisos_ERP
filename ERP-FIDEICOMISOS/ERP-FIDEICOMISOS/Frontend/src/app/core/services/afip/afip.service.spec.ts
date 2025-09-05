import { TestBed } from '@angular/core/testing';

import { AfipService } from './afip.service';

describe('AfipService', () => {
  let service: AfipService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AfipService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
