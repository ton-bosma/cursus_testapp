-- V1: Instrument baseline — INSTR_TYPE, INKOOPBRON, INSTRUMENT
-- PKs via GENERATED ALWAYS AS IDENTITY (PostgreSQL).
-- Amounts: NUMERIC(20,2). Booleans: BOOLEAN.
-- ADRES FKs (ID_ADRES_IN, ID_ADRES_UIT, ID_ADRES_TAXATEUR) are nullable BIGINT
-- without FK constraints; the ADRES table is deferred to a later migration.

CREATE TABLE instr_type
(
    id_instr_type BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    omschrijving  VARCHAR(255),
    forfait_acc   NUMERIC(20, 2)
);

CREATE TABLE inkoopbron
(
    id_inkoopbron BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    omschrijving  VARCHAR(255),
    jn_rapporteren BOOLEAN    NOT NULL DEFAULT FALSE
);

CREATE TABLE instrument
(
    id_instrument      BIGINT         NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    aanschafnr         VARCHAR(255),
    huurnr             INTEGER,
    datum_in           DATE,
    id_adres_in        BIGINT,                          -- FK → ADRES (deferred)
    inkoop_instr       NUMERIC(20, 2),
    inkoop_acc         NUMERIC(20, 2),
    inkoop_factuur     VARCHAR(255),
    id_inkoopbron      BIGINT         REFERENCES inkoopbron (id_inkoopbron),
    id_adres_taxateur  BIGINT,                          -- FK → ADRES (deferred)
    verkoop_btw        NUMERIC(20, 2),
    omschrijv_in       TEXT,
    datum_uit          DATE,
    verkoop_instr      NUMERIC(20, 2),
    id_adres_uit       BIGINT,                          -- FK → ADRES (deferred)
    reparaties         TEXT,
    maat               VARCHAR(255),
    antique            BOOLEAN        NOT NULL DEFAULT FALSE,
    anno               VARCHAR(255),
    id_instr_type      BIGINT         REFERENCES instr_type (id_instr_type),
    foto               VARCHAR(255),
    datum_taxatie      DATE
);
