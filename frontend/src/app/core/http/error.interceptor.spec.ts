import { TestBed } from '@angular/core/testing';
import {
  HttpClient,
  HttpErrorResponse,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { errorInterceptor } from './error.interceptor';

describe('errorInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatSnackBarModule, NoopAnimationsModule],
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should propagate HTTP errors (not swallow them)', (done) => {
    httpClient.get('/api/test').subscribe({
      next: () => fail('expected an error, not success'),
      error: (err: unknown) => {
        expect(err).toBeInstanceOf(HttpErrorResponse);
        done();
      },
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
  });

  it('should propagate 404 errors', (done) => {
    httpClient.get('/api/missing').subscribe({
      next: () => fail('expected an error, not success'),
      error: (err: unknown) => {
        expect(err).toBeInstanceOf(HttpErrorResponse);
        done();
      },
    });

    const req = httpMock.expectOne('/api/missing');
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });
  });
});
