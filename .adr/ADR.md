# Architecture Decision Records

All architectural decisions for this project. One decision per section, numbered sequentially. Decisions are append-only — to reverse a decision, add a new one that supersedes it.

---

## ADR-001: Greenfield herbouw op nieuwe stack i.p.v. legacy uitbreiden

> In de context van een bestaande vioolverhuur-administratie op een verouderde
> stack (AngularJS, JAX-RS, MyBatis, embedded H2), facing onderhoudbaarheid en
> bekende schulden, besloten we de applicatie schoon te herbouwen met het FO als
> eisenbron, om een moderne en AI-vriendelijke codebase te krijgen, accepterend
> dat we functioneel gedrag opnieuw moeten implementeren en verifiëren.

**Status:** accepted
**Date:** 2026-06-01

**Context:** De legacy-app heeft gedocumenteerde schulden (SQL-injectie via
`${}`-substitutie, niet-selectieve updates, deletes zonder integriteitscontrole,
DDL die achterloopt). Een nieuwe app is gewenst, geen uitbreiding van de oude.

**Decision:** Volledige herbouw. Het FO in `/fo` is de functionele eisenbron; de
legacy-broncode in `C:\prive\viool2` dient uitsluitend als referentie voor exacte
business rules. Legacy-eigenaardigheden worden bewust niet overgenomen.

**Alternatives rejected:**
- Legacy uitbreiden/refactoren — houdt verouderde stack en schulden in stand; lage ROI.
- Nieuw zonder FO/legacy-referentie — verliest jaren aan vastgelegde business rules en randgevallen.

**Consequences:** Alle domeinen worden opnieuw gebouwd en geverifieerd tegen het
FO. Geen data-/codecontinuïteit met legacy; een eventuele datamigratie is een
apart, later traject. Vrijheid om het datamodel te verbeteren.

**Scope:** Hele repository (`backend/**`, `frontend/**`); `/fo` (eisen),
`C:\prive\viool2` (referentie, read-only).

**Revisit when:** Er een harde eis komt om de legacy-app live te blijven uitbreiden,
of wanneer een 1:1 datamigratie uit de H2-database in scope komt.

---

## ADR-002: Spring Boot 3 / Java 21 i.p.v. de Nexus JAX-RS/JavaEE-stijl

> In de context van een losstaande, single-tenant administratie, facing de keuze
> tussen het Nexus JavaEE-platform en een zelfstandig framework, besloten we
> Spring Boot 3 op Java 21 (Gradle) te gebruiken, om maximaal ecosysteem en
> onderhoudsgemak te krijgen, accepterend dat we afwijken van de Nexus-conventies.

**Status:** accepted
**Date:** 2026-06-01

**Context:** Deze app maakt geen deel uit van de Nexus-suite. De Nexus JAX-RS/
JavaEE-conventies (JBoss, MyBatis) voegen voor een standalone app meer
complexiteit toe dan ze opleveren.

**Decision:** Spring Boot 3, Java 21, build met Gradle (huisregel: altijd Gradle,
nooit Maven).

**Alternatives rejected:**
- Nexus JAX-RS/RESTEasy + JavaEE — zinvol binnen de suite, overbodige koppeling voor een losstaande app.
- Quarkus — prima alternatief, maar Spring Boot heeft breder ecosysteem en is hier vertrouwder.

**Consequences:** Spring-idiomen (DI, web, test-slices) worden de standaard. Niet
herbruikbaar binnen de Nexus-deploymentketen zonder aanpassing. Coding-standards
voor de backend richten zich op Spring Boot, niet op de Nexus JavaEE-stijl.

**Scope:** `backend/**`, `backend/build.gradle`.

**Revisit when:** De app alsnog binnen de Nexus-suite/uitleverstroom moet draaien.

---

## ADR-003: jOOQ + Flyway voor persistence, geen JPA/ORM

> In de context van een SQL-zwaar domein (nummerlogica met LPAD/substring,
> rapport-selecties), facing de keuze van een persistencelaag, besloten we jOOQ
> met Flyway-migraties te gebruiken en JPA expliciet uit te sluiten, om type-safe,
> expliciete SQL te krijgen, accepterend een codegen-stap in de build.

**Status:** accepted
**Date:** 2026-06-01

**Context:** Het domein bevat berekeningen en selecties die in de legacy als ruwe
SQL bestaan. ORM-abstractie (JPA) verbergt juist de SQL die we expliciet en
controleerbaar willen houden. De gebruiker sluit JPA uit.

**Decision:** jOOQ als query-laag (type-safe DSL), Flyway voor schema-migraties.
jOOQ-codegen draait tegen het Flyway-gemigreerde schema in de Gradle-build.

**Alternatives rejected:**
- Spring Data JPA / Hibernate — verbergt SQL, ORM-magie ongewenst; expliciet afgewezen.
- MyBatis (zoals legacy) — XML-mappers met `${}`-substitutie waren juist de bron van SQL-injectie en onduidelijkheid.
- Plain JdbcTemplate — geen compile-time type-safety op kolommen/queries.

**Consequences:** De build krijgt een codegen-stap die een schema vereist (zie
ADR-004). Queries zijn compile-time gecontroleerd en parameter-gebonden (geen
SQL-injectie). Ontwikkelaars schrijven SQL bewust; geen lazy-loading/entiteit-
grafen.

**Scope:** `backend/**` (repositories), `backend/build.gradle` (jOOQ + Flyway
plugins), `backend/src/main/resources/db/migration/**`.

**Revisit when:** Het domein verschuift naar overwegend triviale CRUD zonder
complexe SQL, óf de codegen-stap structureel build-pijn geeft die niet op te
lossen is.

---

## ADR-004: PostgreSQL in productie, Testcontainers-PostgreSQL in tests

> In de context van jOOQ dat dialect-specifieke SQL genereert, facing de keuze van
> de database (productie én test), besloten we PostgreSQL als productie-DB te
> gebruiken en integratietests op Testcontainers-PostgreSQL te draaien, om dezelfde
> dialect in test en productie te garanderen, accepterend dat tests Docker nodig
> hebben en trager opstarten dan een in-memory DB.

**Status:** accepted
**Date:** 2026-06-01

**Context:** De legacy draaide H2 als echte DB. jOOQ genereert Postgres-dialect;
H2 als test-DB zou afwijken bij native/rapport-queries en valse zekerheid geven.

**Decision:** PostgreSQL voor productie. Integratietests gebruiken
Testcontainers-PostgreSQL (zelfde major-versie als productie). jOOQ-codegen draait
tegen een Flyway-gemigreerde (Testcontainers/throwaway) Postgres.

**Alternatives rejected:**
- H2 als test-DB — dialectverschil met jOOQ/Postgres maakt tests onbetrouwbaar.
- Embedded H2 als productie-DB (zoals legacy) — minder robuust voor een groeiende administratie; geen echte concurrency/backup-verhaal.
- MariaDB/MySQL — geen voordeel boven Postgres voor dit domein.

**Consequences:** CI en lokale tests vereisen een Docker-daemon. Tests zijn trager
maar trouw aan productie. Schema-migraties (Flyway) zijn de enige bron van het
schema, ook voor codegen.

**Scope:** `backend/src/main/resources/db/migration/**`, `backend/src/test/**`,
`compose/**` (postgres-service), CI-configuratie.

**Revisit when:** Testtijd door Testcontainers onhoudbaar wordt (dan: gedeelde
container/herbruikbare instance), of een andere DB-leverancier verplicht wordt.

---

## ADR-005: Modern Angular (standalone + signals) i.p.v. de Angular-14-standards

> In de context van een nieuwe frontend die jaren mee moet, facing de bestaande
> Angular-14/NgModule coding-standards, besloten we de laatste Angular LTS met
> standalone components en signals te gebruiken, om een toekomstvaste en
> AI-vriendelijke frontend te krijgen, accepterend dat de coding-standards later
> bijgewerkt moeten worden.

**Status:** accepted
**Date:** 2026-06-01

**Context:** De zojuist opgestelde coding-standards gaan uit van Angular 14 met
NgModules (afgeleid van `nxs-framework-webmodule`). Voor een greenfield-app is dat
verouderd.

**Decision:** Laatste Angular LTS, standalone components + signals, Angular
Material + CDK. Bootstrap (legacy) wordt niet gebruikt. De Angular-sectie van de
coding-standards wordt later bijgewerkt via `/coding-interview extend`.

**Alternatives rejected:**
- Angular 14 conform huidige standards — verouderd vanaf dag één; NgModule-overhead.
- Niet-Angular framework (React/Vue) — buiten de huisvaardigheden; Angular is de standaard.

**Consequences:** Frontend gebruikt standalone-API's en signals; geen NgModules.
De Angular coding-standards zijn tijdelijk inconsistent met de werkelijke stack
totdat ze ge-extend zijn. Material i.p.v. Bootstrap voor UI.

**Scope:** `frontend/**`, later `~/.claude/skills/coding-standards/rules/angular-*.md`
(via `/coding-interview extend`).

**Revisit when:** De Angular-standards zijn bijgewerkt naar de moderne stijl (dan
vervalt de "later bijwerken"-consequentie), of een nieuwe Angular-major de
standalone/signals-aanpak wezenlijk verandert.

---

## ADR-006: Exceptions niet inslikken (fail-loud error-handling)

> In de context van een administratief systeem waar stille fouten tot verkeerde
> bedragen/nummers leiden, facing de neiging om exceptions weg te vangen, besloten
> we fouten standaard te loggen én door te geven of expliciet af te handelen, om
> bugs vroeg en zichtbaar te maken, accepterend dat code soms meer expliciete
> foutafhandeling vereist.

**Status:** accepted
**Date:** 2026-06-01

**Context:** De legacy verzamelde fouten soms stil (bv. verificatie-mails: fout per
relatie verzameld, proces gaat door). Voor financiële/administratieve correctheid
is stil falen onacceptabel zonder expliciete reden.

**Decision:** Geen lege `catch`-blokken en geen ingeslikte exceptions. Een exception
wordt gelogd (SLF4J/Angular-logging) én doorgegeven of betekenisvol afgehandeld.
Stil wegvangen mag uitsluitend met een korte, gedocumenteerde motivatie (commentaar
dat de "waarom" uitlegt). Backend vertaalt fouten naar duidelijke HTTP-statussen via
een centrale exception-handler.

**Alternatives rejected:**
- Best-effort/continue-on-error als default (zoals legacy verificatiebatch) — verbergt fouten, gevaarlijk bij bedragen/nummers.
- Globaal alles laten doorlekken zonder vertaling — slechte API/UX; daarom centrale handler i.p.v. niets.

**Consequences:** Meer expliciete foutpaden en een centrale error-handler (backend)
+ HTTP-interceptor (frontend). Code review let actief op lege catch-blokken. Sluit
aan op `general-quality.md` ("Don't swallow exceptions silently").

**Scope:** `backend/**` (centrale `@ControllerAdvice`/exception-handler),
`frontend/src/app/core/**` (HTTP-error-interceptor).

**Revisit when:** Een specifiek batch-/integratieproces aantoonbaar baat heeft bij
gecontroleerde continue-on-error; dan een aparte ADR met de motivatie.

---

## ADR-007: Bestandsopslag via abstractie met filesystem-default

> In de context van foto's/documenten per instrument (latere cyclus), facing de
> keuze waar bestanden landen, besloten we een opslag-abstractie te definiëren met
> een filesystem-implementatie als default, om deployment simpel te houden en
> object-storage later mogelijk te maken, accepterend dat de abstractie nu nog niet
> gebruikt wordt.

**Status:** accepted
**Date:** 2026-06-01

**Context:** De legacy bewaart bestanden op een filesystem-root (`/filesystem`,
submappen op `aanschafnr`). Foto/document-upload valt buiten v1, maar de richting
moet nu vastliggen om het later niet te verbouwen.

**Decision:** Een `FileStorage`-seam (interface) met een filesystem-implementatie
(Docker-volume) als default, georganiseerd op `aanschafnr` zoals legacy. Object-
storage (S3/MinIO) blijft een latere optie achter dezelfde interface.

**Alternatives rejected:**
- Bestanden in de database (BLOB) — bloat, lastige backups, slechte streaming.
- Direct object-storage nu — onnodige operationele last voor een single-tenant on-prem/Docker-setup.

**Consequences:** Upload-code (latere cyclus) programmeert tegen de interface, niet
tegen het filesystem. Migratie naar object-storage is een nieuwe implementatie, geen
herontwerp.

**Scope:** `backend/**` (storage-interface + filesystem-impl), `compose/**`
(volume-mapping). Activeert pas in de cyclus met foto/document-upload.

**Revisit when:** Multi-tenant of horizontale schaal vereist gedeelde opslag, of
bestandsvolumes het lokale filesystem ontgroeien.

---

## ADR-008: Authenticatie buiten de applicatie, met seam voor later

> In de context van een interne single-tenant admin met één gebruikersrol, facing
> de vraag of authenticatie in de app hoort, besloten we authenticatie buiten de
> applicatie te leggen (reverse proxy) en alleen een seam in te bouwen, om v1 niet
> met auth-complexiteit te belasten, accepterend dat de app zelf voorlopig geen
> identiteit/rollen kent.

**Status:** accepted
**Date:** 2026-06-01

**Context:** De legacy heeft geen ingebouwd autorisatiemodel; afscherming gebeurt
in de deployment-omgeving. Er is één gebruiker (de beheerder).

**Decision:** Authenticatie/afscherming via een reverse proxy vóór de app. In de
backend wordt één duidelijk filter-/interceptor-punt (seam) gereserveerd zodat
in-app auth (bv. OIDC/Keycloak) later toegevoegd kan worden zonder herontwerp.

**Alternatives rejected:**
- In-app auth nu bouwen (Spring Security + IdP) — overbodige complexiteit voor v1 met één gebruiker.
- Helemaal geen seam — zou een latere auth-toevoeging een ingrijpende verbouwing maken.

**Consequences:** De app vertrouwt op de deployment-omgeving voor toegang; geen
rollen/permissies in v1. Endpoints zijn "open" achter de proxy. Het seam-punt moet
bewust leeg-maar-aanwezig blijven.

**Scope:** `backend/src/main/java/.../**` (security-seam/filterketen), `compose/**`
(reverse-proxy), deployment-documentatie.

**Revisit when:** Er meerdere gebruikers of rollen nodig zijn, of de app buiten een
afgeschermde omgeving bereikbaar wordt.
