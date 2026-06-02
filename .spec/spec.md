# Vioolverhuur-administratie — Spec

Last updated: 2026-06-02 (after PRD v1 cycle)

## Architecture

Three-tier web app: Angular 19 SPA → Spring Boot 3 REST API → PostgreSQL 16, all run via
Docker Compose. The SPA calls the API over `/api/*` paths relative to its own origin; there
is no in-app authentication — endpoints are open and any access control is delegated to an
external reverse proxy in front of the app (see ADR-008), which is not part of this repo.

- Angular SPA (nginx container) — instrument CRUD + reference-list editors
- Spring Boot API (JRE container) — business logic, number generators, jOOQ queries
- PostgreSQL 16 (postgres:16-alpine container) — single schema, Flyway-managed
- docker-compose orchestrates all three with named volumes for DB + file storage

## Stack

**Backend**
- Spring Boot 3.4.5, Java 21 (Gradle toolchain) — see ADR-002
- jOOQ 3.19.22 (nu.studer.jooq plugin 9.0) — type-safe SQL, no ORM — see ADR-003
- Flyway 11.8.2 + flyway-database-postgresql — schema migrations — see ADR-003
- PostgreSQL JDBC driver 42.7.5 — see ADR-004
- Testcontainers 1.20.6 (postgresql + junit-jupiter) — integration tests — see ADR-004
- JUnit 5 + AssertJ (via spring-boot-starter-test)
- Spring dependency-management plugin 1.1.7; app version 0.1.0

**Frontend**
- Angular 19.2 standalone + signals, no NgModule — see ADR-005
- Angular Material 19.2 + CDK 19.2
- RxJS 7.8, TypeScript 5.6, zone.js 0.15, tslib 2.8
- Karma 6.4 + Jasmine 5.4 (ChromeHeadless)
- tsconfig: strict + strictTemplates, ES2022 target

## Data Model

Tables (Flyway V1 baseline + V2 unique constraints, schema `public`):

```
instr_type
  id_instr_type   BIGINT PK (identity)
  omschrijving    VARCHAR(255)
  forfait_acc     NUMERIC(20,2)

inkoopbron
  id_inkoopbron   BIGINT PK (identity)
  omschrijving    VARCHAR(255)
  jn_rapporteren  BOOLEAN NOT NULL DEFAULT FALSE

instrument
  id_instrument       BIGINT PK (identity)
  aanschafnr          VARCHAR(255) UNIQUE (nullable; multiple NULLs allowed — V2)
  huurnr              INTEGER UNIQUE (nullable; multiple NULLs allowed — V2)
  datum_in            DATE
  id_adres_in         BIGINT (ADRES seam — nullable, no FK constraint yet)
  inkoop_instr        NUMERIC(20,2)
  inkoop_acc          NUMERIC(20,2)
  inkoop_factuur      VARCHAR(255)
  id_inkoopbron       BIGINT FK → inkoopbron
  id_adres_taxateur   BIGINT (ADRES seam)
  verkoop_btw         NUMERIC(20,2)
  omschrijv_in        TEXT
  datum_uit           DATE
  verkoop_instr       NUMERIC(20,2)
  id_adres_uit        BIGINT (ADRES seam)
  reparaties          TEXT
  maat                VARCHAR(255)
  antique             BOOLEAN NOT NULL DEFAULT FALSE
  anno                VARCHAR(255)
  id_instr_type       BIGINT FK → instr_type
  foto                VARCHAR(255)
  datum_taxatie       DATE
```

Frontend interfaces mirror the DB (camelCase; snake_case mapped in the service/DTO layer):

```typescript
IInstrument       { id?; aanschafnr; huurnr; datumIn; idAdresIn; inkoopInstr;
                    inkoopAcc; inkoopFactuur; idInkoopbron; idAdresTaxateur;
                    verkoopBtw; omschrijvIn; datumUit; verkoopInstr; idAdresUit;
                    reparaties; maat; antique; anno; idInstrType; foto; datumTaxatie }
IInstrumentRow    { id; huurnr: number|null; aanschafnr; type; maat; inkoopAdres;
                    verkoopAdres; aanschafdatum: string|null; verkoopdatum: string|null }
IInstrumentType   { id?; omschrijving; forfaitAccessoires }
IInkoopbron       { id?; omschrijving; rapporteren }
IInstrumentSearchParams { q; archief; max: number|null }
```

## API Surface

### /api/instrument
- `GET  /api/instrument?q=&archief=&max=`  → `List<InstrumentSearchRow>` (terms AND-ed, case-insensitive substring; `max` omitted = unlimited)
- `GET  /api/instrument/{id}`              → `InstrumentRow` (404 `InstrumentNotFoundException`)
- `PUT  /api/instrument`                   → `InstrumentRow` (null id = insert; non-null id = full-replace update of all 21 columns)
- `PUT  /api/instrument/aanschafnummer`    → `InstrumentRow` with `aanschafnr` set, NOT persisted (400 if datumIn/idInkoopbron missing)
- `PUT  /api/instrument/huurnummer`        → `InstrumentRow` with `huurnr` set, NOT persisted
- `DELETE /api/instrument/{id}`            → 204 (404 if absent)
- `@ExceptionHandler`: `DataIntegrityViolationException` → 409 (duplicate number); `IllegalStateException` → 409 (sequence > 99)

### /api/instrumenttype
- `GET /api/instrumenttype`                → `List<InstrumentTypeRow>` (ordered by omschrijving)
- `PUT /api/instrumenttype`                → `List<InstrumentTypeRow>` (three-pass save, returns reloaded list)

### /api/inkoopbron
- `GET /api/inkoopbron`                    → `List<InkoopbronRow>` (ordered by omschrijving)
- `PUT /api/inkoopbron`                    → `List<InkoopbronRow>` (three-pass save, returns reloaded list)

## Key Patterns

**Backend**
- jOOQ parameter-bound SQL only — no string concatenation (ADR-003)
- Three layers: `@RestController` → `@Service @Transactional` → jOOQ repository (`@Transactional(readOnly=true)` on search/find/generate)
- Three-pass reference saveAll: delete (id≠null && markedDeleted) → update (id≠null && !markedDeleted) → insert (id==null); `markedDeleted` is request-only, never persisted
- Aanschafnr generator: format `L.ddm.myy.NN`; `MAX(seq)+1` per 10-char prefix; >99 → `IllegalStateException`; self-exclusion on regenerate
- Huurnr generator: `yy*100 + volgnr`; `MAX+1` per year; >99 → `IllegalStateException`; self-exclusion on regenerate
- UNIQUE constraints (V2) make a concurrent duplicate hard-fail as 409 instead of silently persisting (ADR-006)
- `Clock` bean injected for testable date logic; full-replace update writes all 21 columns; `save` re-reads via `findById`
- Per-controller `@ExceptionHandler` (no `@ControllerAdvice`); `@ResponseStatus` on `InstrumentNotFoundException`
- I-prefixed interfaces on `instrumenttype` + `inkoopbron` domains; NOT on the instrument domain
- Fail-loud error handling — no swallowed exceptions (ADR-006)

**Frontend**
- Signals + computed throughout; `OnPush` change detection on every component
- Live search: `toObservable(_params)` → `debounceTime(300)` → `distinctUntilChanged` → `switchMap(search$)` → `catchError` (sets `searchFailed`, returns `[]`) → `startWith([])` → `toSignal`
- Typed reactive `FormGroup<IInstrumentForm>`; values read via `getRawValue()`
- Three-pass reference list save with transient `isNew`/`markedDeleted` flags; `visibleRows` computed filters `markedDeleted`
- Functional `errorInterceptor`: logs status+url, shows `MatSnackBar`, rethrows (does not swallow); maps 401/403/404/0/500/502/503 to `NL.errors`
- Central `NL` labels constant in `nl.labels.ts` (`as const`)
- Currency normalization directive on blur (accepts `,` or `.`); overwrite-confirm dialog via `firstValueFrom`
- `delete()` resets form + clears `currentId` + emits `deleted` output; delete button only rendered when `nested`
- Routes lazy-loaded via `loadComponent`; `withComponentInputBinding()` passes route `id` as `@Input`

## Directory Structure

```
backend/src/main/java/nl/uampyyg/viool/
  Application.java
  config/          WebConfig (CORS → localhost:4200, Clock bean)
  instrument/      InstrumentController, InstrumentService, InstrumentRepository, NummerGenerator
    dto/           InstrumentRow, InstrumentSearchRow
  instrumenttype/  Controller, I*Service/Impl, I*Repository/Impl, dto/
  inkoopbron/      Controller, I*Service/Impl, I*Repository/Impl, dto/
  jooq/            (generated at build time — build/generated-sources/jooq)

backend/src/main/resources/
  application.yml  (DB creds via ${DB_URL/DB_USERNAME/DB_PASSWORD:default})
  db/migration/    V1__instrument_baseline.sql, V2__unique_instrument_numbers.sql

backend/src/test/java/nl/uampyyg/viool/
  AbstractIntegrationTest.java, BaselineSchemaIT.java
  instrument/      InstrumentServiceIT, InstrumentSearchIT, NummerGeneratorTest
  referentie/      ReferentieServiceIT

frontend/src/app/
  app.component.ts, app.config.ts, app.routes.ts
  core/http/       error.interceptor.ts
  core/i18n/       nl.labels.ts
  shared/          currency.directive.ts, shell/shell.component.ts
  features/instrument/      instrument.model.ts, instrument.service.ts, list, detail, confirm-overwrite.dialog
  features/instrumenttype/  instrumenttype.service.ts, list
  features/inkoopbron/      inkoopbron.service.ts, list
```

## Infrastructure

- docker-compose, three services:
  - `db`: postgres:16-alpine — volume `db-data`; `POSTGRES_PASSWORD` required (no default)
  - `backend`: 2-stage Dockerfile (temurin 21 jdk→jre), non-root user `viool`, port `${BACKEND_PORT:-8080}`, volume `file-storage` at `/app/storage` (file-storage seam — ADR-007, not yet used), depends_on db healthy
  - `frontend`: 2-stage Dockerfile (node:22 build → nginx:1.27-alpine serve), nginx SPA fallback (`try_files … /index.html`), port `${FRONTEND_PORT:-4200}`, depends_on backend
- Backend env overrides: `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` (from `POSTGRES_*`); `VIOOL_STORAGE_DIR=/app/storage`
- Authentication: none in-app for v1 — endpoints are open behind an external reverse proxy; a seam is reserved for later in-app auth (ADR-008)
- jOOQ codegen: Gradle start/wait/migrate/stop lifecycle against an ephemeral `postgres:16` container (port 15432), Flyway-migrated (V1→V2) before generation
```
