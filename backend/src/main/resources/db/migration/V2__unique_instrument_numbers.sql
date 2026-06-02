-- V2: enforce uniqueness of generated business keys.
-- PostgreSQL UNIQUE allows multiple NULLs, so instruments without a number are unaffected.
ALTER TABLE instrument ADD CONSTRAINT uq_instrument_aanschafnr UNIQUE (aanschafnr);
ALTER TABLE instrument ADD CONSTRAINT uq_instrument_huurnr UNIQUE (huurnr);
