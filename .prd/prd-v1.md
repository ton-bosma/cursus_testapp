---
version: 1
status: built
date: 2026-06-01
author: Ton Bosma
previous: null
---

# PRD v1 — Fundament + Instrumenten-domein

> Vioolverhuur-administratie (greenfield herbouw). Het FO in `/fo` is de
> eisenbron; de legacy-broncode in `C:\prive\viool2` dient als referentie voor
> exacte business rules. Deze app wordt bewust nieuw gebouwd en hoeft legacy
> eigenaardigheden niet over te nemen.

## 1. Problem

Een vioolbouwer/-handelaar beheert verhuur, verkoop, taxatie, afschrijving en
facturatie van strijkinstrumenten. De bestaande applicatie draait op een
verouderde stack (AngularJS, JAX-RS/RESTEasy, MyBatis met `${}`-string-SQL,
embedded H2) met bekende schulden: SQL-injectierisico in zoek-/rapportqueries,
niet-selectieve updates die velden ongewild leegzetten, deletes zonder
integriteitscontrole, en een DDL die achterloopt op de code. De stack is moeilijk
te onderhouden en niet AI-vriendelijk.

Het instrument is het centrale registratie-object van het hele systeem: contracten,
taxaties, reparaties, afschrijvingen en documenten hangen eraan. Zonder een solide,
modern instrumentbeheer kan geen enkel ander domein opnieuw gebouwd worden. Daarom
start de herbouw met het fundament én het instrumenten-domein.

## 2. Solution

We bouwen een nieuwe single-tenant administratie op een moderne stack: **Spring
Boot 3 (Java 21, Gradle)** met **jOOQ + Flyway** tegen **PostgreSQL**, en een
**Angular (laatste LTS, standalone + signals) + Angular Material** frontend.
Cyclus 1 levert het draaiende projectskelet plus het volledige instrumenten-domein:
instrumenten aanmaken/zoeken/bewerken/archiveren, beheer van de referentielijsten
**instrumenttype** en **inkoopbron**, en de twee nummergeneratoren (aanschafnummer
en huurnummer) die de business rules uit het FO exact volgen — maar nu met
type-safe SQL en parameterbinding in plaats van string-substitutie.

## 3. Scope

| Deze PRD dekt | Deze PRD dekt NIET |
| --- | --- |
| Projectskelet: Spring Boot 3 + jOOQ + Flyway + Docker (compose) | Foto-/documentupload + thumbnails (latere cyclus) |
| Angular-app skelet: routing, Material-shell, HTTP-laag, NL-i18n-opzet | Favorieten-filter (fav1/fav2) |
| Instrument CRUD (aanmaken, wijzigen, archiveren/verwijderen) | Alle instrument-rapporten (huurnummers, winkel-voor/achter/uit, inventaris, consignatie, historisch) + PDF/Velocity |
| Multi-term zoeken (AND over termen, substring, case-insensitief) + archieffilter (`datum_uit is null`) | Detailpanelen van andere domeinen: contracten, taxaties, reparaties/identificatie |
| INSTR_TYPE inline-beheer (omschrijving + forfait accessoires) | Afschrijvingsberekening (`INSTRAFSCHR`) |
| INKOOPBRON inline-beheer (omschrijving + rapporteren-vlag) | Facturatie, contracten/termijnen, debiteurenbeheer |
| Aanschafnummer-generator `L.ddm.myy.NN` (FO §5.2) | Adres/relatiebeheer als eigen scherm (alleen minimale referentie-koppeling) |
| Huurnummer-generator `yy×100 + volgnr` (FO §5.1) | E-mail, sturing/instellingen, queries/templates |
| Flyway-baselineschema voor INSTRUMENT, INSTR_TYPE, INKOOPBRON | Authenticatie/autorisatie in de app (buiten de app geregeld) |

## 4. Architecture

#### Component-structuur (nieuw)

```
viool/
├── backend/                         (Spring Boot 3, Java 21, Gradle)
│   ├── build.gradle                 jOOQ-codegen + Flyway plugins
│   ├── src/main/resources/db/migration/
│   │   └── V1__instrument_baseline.sql   (INSTRUMENT, INSTR_TYPE, INKOOPBRON + sequences)
│   ├── src/main/java/.../instrument/
│   │   ├── InstrumentController        REST: /api/instrument
│   │   ├── InstrumentService           CRUD + zoeken + nummergeneratoren
│   │   ├── InstrumentRepository        jOOQ-queries
│   │   └── dto/                        InstrumentRow, zoek-/displayvelden
│   ├── src/main/java/.../instrumenttype/   Controller/Service/Repository (inline-beheer)
│   ├── src/main/java/.../inkoopbron/        Controller/Service/Repository (inline-beheer)
│   └── src/test/java/...                Testcontainers-PostgreSQL integratietests
├── frontend/                        (Angular laatste LTS, standalone)
│   └── src/app/
│       ├── instrument/              list + detail (standalone components, signals)
│       ├── instrumenttype/          inline tabel-editor
│       ├── inkoopbron/              inline tabel-editor
│       ├── core/                    http-interceptors, model-types, i18n (NL)
│       └── shared/                  Material-shell, currency-directive
└── compose/                         docker-compose: app + postgres
```

#### Key components

- **InstrumentService (backend)** — eigenaar van instrument-CRUD, het multi-term
  zoeken, en de twee nummergeneratoren. Hangt af van `InstrumentRepository` (jOOQ),
  `InstrumentTypeRepository` en `InkoopbronRepository` voor referentiedata.
- **Nummergeneratoren** — `aanschafnummer(L.ddm.myy.NN)` vereist `datumIn` +
  `idInkoopbron`; `huurnummer(yy×100+seq)` leidt het jaartal af. Beide berekenen het
  volgnummer met een expliciete, type-safe jOOQ-query (geen `${}`-substitutie),
  met uitsluiting van het eigen instrument.
- **InstrumentType-/Inkoopbron-beheer** — inline lijst-editor: de hele lijst wordt
  in één keer opgeslagen; server verwerkt in drie passes (delete gemarkeerde →
  update bestaande → insert nieuwe), conform FO §4.5/§4.6.
- **Angular instrument-list** — live zoeken (debounced), archief-toggle,
  max-resultaten (10/20/50/100/Alles), standalone component met signals.
- **Angular instrument-detail** — panelen In / Uit / Instrument met
  bedrag-normalisatie (2 decimalen) en de twee "genereer nummer"-acties met
  bevestigingsmodal bij overschrijven.

#### Data flow

```
Angular (signals/HttpClient)  ──REST/JSON──►  Spring Boot Controller
        ▲                                            │
        │                                            ▼
   gevalideerde DTO  ◄── Service (business rules) ── InstrumentService
                                                     │
                                                     ▼
                                          jOOQ Repository ──► PostgreSQL
                                                     ▲
                                            Flyway-migratie (schema)
```

#### Integration points

Greenfield — geen bestaande code om in te haken. Wel **functionele koppelpunten**
die we in v1 als seam vormgeven maar nog niet invullen:
- Instrument verwijst naar maximaal drie adressen (in/uit/taxateur) → in v1 alleen
  als nullable referentie-id's; volwaardig adresbeheer komt in een latere cyclus.
- `INSTRUCONTR` (contract-koppeling), `TAXATIE`, `REPARATIE`, `INSTRAFSCHR` →
  buiten v1; het schema reserveert deze FK's niet vroegtijdig.

## 5. Success Metrics

| Metric | Target |
| --- | --- |
| Instrument CRUD end-to-end werkend (UI → API → Postgres) | Volledig |
| Aanschafnummer-generator gedrag identiek aan FO §5.2 | Bewezen met testcases (incl. volgnummer-telling) |
| Huurnummer-generator gedrag identiek aan FO §5.1 | Bewezen met testcases (jaarrol + volgnummer) |
| Zoeken: AND-over-termen, case-insensitief, archieffilter | Bewezen met integratietests (Testcontainers) |
| SQL uitsluitend via jOOQ-binding (geen string-concatenatie) | 0 dynamisch-geconcateneerde queries |
| Backend integratietests draaien op Testcontainers-PostgreSQL | Groen in CI |
| jOOQ-codegen + Flyway in de Gradle-build | Reproduceerbaar vanaf schoon |

## 6. Out of Scope

Expliciet niet in deze PRD (komt in latere cycli): foto-/documentupload +
thumbnails; favorieten (fav1/fav2); alle instrument-rapporten en PDF-generatie;
contracten/termijnen, facturatie, taxaties, reparaties/identificatie,
afschrijvingsberekening; volwaardig adres-/relatiebeheer; e-mail; sturing/
instellingen; vrije queries/templates; in-app authenticatie/autorisatie.

## User/System Flow

```
Instrument zoeken                     Instrument aanmaken/bewerken
─────────────────                     ───────────────────────────
typ zoekterm ─► live (debounced)      "+"  ─► leeg detailscherm
   │              GET /api/instrument     │
   ▼                  ?q=&archief=&max=    ▼  vul In/Uit/Instrument
resultaattabel ◄── AND/substring      [genereer aanschafnr] ─► vereist datumIn+bron
   │                                      │        (modal bij overschrijven)
   ▼                                      ▼
klik rij ─► detail                    [genereer huurnr] ─► yy×100+seq
                                          │
                                          ▼
                                      opslaan ─► PUT /api/instrument ─► terug-geladen rij
```

## Dependencies & Risks

| Dependency/Risk | Impact | Mitigation |
| --- | --- | --- |
| jOOQ-codegen heeft een schema nodig tijdens de build | Build-complexiteit | Codegen tegen Flyway-gemigreerde schema (throwaway/Testcontainers Postgres) in de Gradle-build |
| jOOQ genereert Postgres-dialect; H2 zou afwijken | Onbetrouwbare tests | **Testcontainers-PostgreSQL** voor integratietests (besloten) |
| Legacy business rules subtiel (substring-telling aanschafnr, jaarrol huurnr) | Gedragsafwijking | Per regel verifiëren tegen `C:\prive\viool2` (`InstrumentService.java`, `SearchMapper.xml`) tijdens ticketing |
| FAV1/FAV2 ontbreken in legacy-DDL | Schema-onzekerheid | Favorieten buiten v1; schoon ontwerp in latere cyclus |
| Coding-standards gaan uit van Angular 14 | Inconsistente richtlijnen | Standards later bijwerken via `/coding-interview extend` voor moderne Angular |

## Testing Strategy

- **Backend integratietests** op **Testcontainers-PostgreSQL** (zelfde dialect als
  productie). Nadruk op: nummergeneratoren (randgevallen volgnummer/jaarrol),
  zoeklogica (AND/substring/archief), en de drie-pass lijst-save van type/bron.
- **Unit-tests** voor pure berekeningen (nummerformattering) zonder DB.
- **Frontend**: component-tests voor list (zoek/filter/paginering) en detail
  (bedrag-normalisatie, generatie-acties met overschrijf-modal).

## Privacy & Security

- **Auth**: buiten de applicatie (reverse proxy), conform legacy; v1 bouwt een
  seam (filter/interceptor-punt) zodat in-app auth later toe te voegen is.
- **SQL-injectie**: structureel weggenomen door jOOQ-parameterbinding — een
  expliciete verbetering t.o.v. de legacy `${}`-substitutie.
- Persoonsgegevens (adres-referenties) blijven in v1 minimaal; geen export/e-mail.

## Niet-functioneel

- **Applicatietaal: Nederlands** — UI-teksten en domeinvocabulaire in het
  Nederlands; i18n-opzet vanaf de start zodat teksten centraal staan.
- **Bedragen** `DECIMAL(20,2)`; **booleans** in DB als kolommen die in Java/TS
  echte booleans zijn (geen 0/1-`INT`-lekkage in het domeinmodel).
- **Deployment**: Docker (app + PostgreSQL via compose).
