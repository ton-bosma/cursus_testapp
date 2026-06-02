import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { InstrumentDetailComponent } from './instrument-detail.component';
import { IInstrument } from './instrument.model';

const SAMPLE: IInstrument = {
  id: 7,
  aanschafnr: 'A-100',
  huurnr: 42,
  datumIn: '2024-01-01',
  idAdresIn: null,
  inkoopInstr: 100,
  inkoopAcc: null,
  inkoopFactuur: null,
  idInkoopbron: 3,
  idAdresTaxateur: null,
  verkoopBtw: null,
  omschrijvIn: null,
  datumUit: null,
  verkoopInstr: null,
  idAdresUit: null,
  reparaties: null,
  maat: null,
  antique: false,
  anno: null,
  idInstrType: null,
  foto: null,
  datumTaxatie: null,
};

describe('InstrumentDetailComponent', () => {
  let fixture: ComponentFixture<InstrumentDetailComponent>;
  let component: InstrumentDetailComponent;
  let httpMock: HttpTestingController;

  function setup(id?: string): void {
    fixture = TestBed.createComponent(InstrumentDetailComponent);
    component = fixture.componentInstance;
    if (id !== undefined) {
      component.id = id;
    }
    fixture.detectChanges();
    // Resolve dropdown loads (order: instrumenttype, inkoopbron).
    httpMock.expectOne('/api/instrumenttype').flush([]);
    httpMock.expectOne('/api/inkoopbron').flush([]);
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InstrumentDetailComponent, NoopAnimationsModule],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('creates without an id (new instrument)', () => {
    setup();
    expect(component).toBeTruthy();
    expect(component['currentId']()).toBeUndefined();
  });

  it('loads an existing instrument by id', () => {
    setup('7');
    const req = httpMock.expectOne('/api/instrument/7');
    expect(req.request.method).toBe('GET');
    req.flush(SAMPLE);
    expect(component.form.getRawValue().aanschafnr).toBe('A-100');
    expect(component['currentId']()).toBe(7);
  });

  it('saves via PUT and reloads the returned row', () => {
    setup();
    component.form.patchValue({ maat: '4/4' });
    component['save']();
    const req = httpMock.expectOne('/api/instrument');
    expect(req.request.method).toBe('PUT');
    req.flush({ ...SAMPLE, id: 99, maat: '4/4' });
    expect(component['currentId']()).toBe(99);
    expect(component.form.getRawValue().maat).toBe('4/4');
  });

  it('blocks aanschafnr generation without datum-in + inkoopbron', () => {
    setup();
    void component['generateAanschafnr']();
    httpMock.expectNone('/api/instrument/aanschafnummer');
  });

  it('generates aanschafnr when preconditions are met and field is empty', fakeAsync(() => {
    setup();
    component.form.patchValue({ datumIn: '2024-01-01', idInkoopbron: 3, aanschafnr: null });
    void component['generateAanschafnr']();
    tick();
    const req = httpMock.expectOne('/api/instrument/aanschafnummer');
    expect(req.request.method).toBe('PUT');
    req.flush({ ...SAMPLE, aanschafnr: 'GEN-1' });
    expect(component.form.getRawValue().aanschafnr).toBe('GEN-1');
  }));

  it('asks for confirmation before overwriting an existing aanschafnr', fakeAsync(() => {
    const dialog = TestBed.inject(MatDialog);
    const openSpy = spyOn(dialog, 'open').and.returnValue({
      afterClosed: () => of(false),
    } as ReturnType<MatDialog['open']>);

    setup();
    component.form.patchValue({ datumIn: '2024-01-01', idInkoopbron: 3, aanschafnr: 'EXISTING' });
    void component['generateAanschafnr']();
    tick();
    expect(openSpy).toHaveBeenCalled();
    httpMock.expectNone('/api/instrument/aanschafnummer');
  }));

  it('generates huurnr via PUT when field is empty', fakeAsync(() => {
    setup();
    void component['generateHuurnr']();
    tick();
    const req = httpMock.expectOne('/api/instrument/huurnummer');
    expect(req.request.method).toBe('PUT');
    req.flush({ ...SAMPLE, huurnr: 123 });
    expect(component.form.getRawValue().huurnr).toBe(123);
  }));
});
