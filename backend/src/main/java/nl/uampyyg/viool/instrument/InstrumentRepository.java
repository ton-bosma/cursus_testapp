package nl.uampyyg.viool.instrument;

import java.util.Optional;

import nl.uampyyg.viool.instrument.dto.InstrumentRow;
import nl.uampyyg.viool.jooq.tables.records.InstrumentRecord;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static nl.uampyyg.viool.jooq.Tables.INSTRUMENT;


/**
 * jOOQ-based data access for the INSTRUMENT table.
 *
 * <p>All statements use jOOQ's type-safe DSL with bind parameters; no SQL is
 * assembled through string concatenation (ADR-003). Mapping between the
 * {@link InstrumentRow} transfer object and the generated
 * {@link InstrumentRecord} is performed explicitly so that the set of columns
 * touched by each statement is unambiguous.
 */
@Repository
public class InstrumentRepository
{
   private final DSLContext dsl;


   public InstrumentRepository(DSLContext dsl)
   {
      this.dsl = dsl;
   }


   /**
    * Inserts a new instrument. The {@code ID_INSTRUMENT} column is database
    * generated (identity) and is therefore never written; the generated key is
    * returned so callers can re-read the persisted row.
    *
    * @param row the values to insert (its {@code id} is ignored)
    * @return the generated {@code ID_INSTRUMENT}
    */
   public Long insert(InstrumentRow row)
   {
      InstrumentRecord record = dsl.newRecord(INSTRUMENT);
      applyMutableColumns(record, row);

      record.store();
      return record.getIdInstrument();
   }


   /**
    * Updates the instrument with the given id, writing <em>all</em> mutable
    * columns deliberately. This makes the update a full replace of the row's
    * editable state: any field left {@code null} on the incoming
    * {@link InstrumentRow} clears the corresponding column. This is intentional
    * — the frontend always submits the complete row — and avoids partial-update
    * ambiguity.
    *
    * @param row the values to write; {@code row.getId()} selects the target row
    * @return the number of rows affected (0 when the id does not exist)
    */
   public int update(InstrumentRow row)
   {
      return dsl.update(INSTRUMENT)
            .set(INSTRUMENT.AANSCHAFNR,        row.getAanschafnr())
            .set(INSTRUMENT.HUURNR,            row.getHuurnr())
            .set(INSTRUMENT.DATUM_IN,          row.getDatumIn())
            .set(INSTRUMENT.ID_ADRES_IN,       row.getIdAdresIn())
            .set(INSTRUMENT.INKOOP_INSTR,      row.getInkoopInstr())
            .set(INSTRUMENT.INKOOP_ACC,        row.getInkoopAcc())
            .set(INSTRUMENT.INKOOP_FACTUUR,    row.getInkoopFactuur())
            .set(INSTRUMENT.ID_INKOOPBRON,     row.getIdInkoopbron())
            .set(INSTRUMENT.ID_ADRES_TAXATEUR, row.getIdAdresTaxateur())
            .set(INSTRUMENT.VERKOOP_BTW,       row.getVerkoopBtw())
            .set(INSTRUMENT.OMSCHRIJV_IN,      row.getOmschrijvIn())
            .set(INSTRUMENT.DATUM_UIT,         row.getDatumUit())
            .set(INSTRUMENT.VERKOOP_INSTR,     row.getVerkoopInstr())
            .set(INSTRUMENT.ID_ADRES_UIT,      row.getIdAdresUit())
            .set(INSTRUMENT.REPARATIES,        row.getReparaties())
            .set(INSTRUMENT.MAAT,              row.getMaat())
            .set(INSTRUMENT.ANTIQUE,           row.isAntique())
            .set(INSTRUMENT.ANNO,              row.getAnno())
            .set(INSTRUMENT.ID_INSTR_TYPE,     row.getIdInstrType())
            .set(INSTRUMENT.FOTO,              row.getFoto())
            .set(INSTRUMENT.DATUM_TAXATIE,     row.getDatumTaxatie())
            .where(INSTRUMENT.ID_INSTRUMENT.eq(row.getId()))
            .execute();
   }


   /**
    * Reads a single instrument by its primary key.
    *
    * @param id the {@code ID_INSTRUMENT} to look up
    * @return the row, or {@link Optional#empty()} when no such row exists
    */
   public Optional<InstrumentRow> findById(Long id)
   {
      return dsl.selectFrom(INSTRUMENT)
            .where(INSTRUMENT.ID_INSTRUMENT.eq(id))
            .fetchOptional()
            .map(InstrumentRepository::toRow);
   }


   /**
    * Deletes the instrument with the given id.
    *
    * @param id the {@code ID_INSTRUMENT} to delete
    * @return the number of rows affected (0 when the id does not exist)
    */
   public int deleteById(Long id)
   {
      return dsl.deleteFrom(INSTRUMENT)
            .where(INSTRUMENT.ID_INSTRUMENT.eq(id))
            .execute();
   }


   /**
    * Copies every mutable (non-identity) column from the DTO onto a fresh
    * record. Used for inserts; the identity {@code ID_INSTRUMENT} is left to
    * the database.
    */
   private static void applyMutableColumns(InstrumentRecord record, InstrumentRow row)
   {
      record.setAanschafnr(row.getAanschafnr());
      record.setHuurnr(row.getHuurnr());
      record.setDatumIn(row.getDatumIn());
      record.setIdAdresIn(row.getIdAdresIn());
      record.setInkoopInstr(row.getInkoopInstr());
      record.setInkoopAcc(row.getInkoopAcc());
      record.setInkoopFactuur(row.getInkoopFactuur());
      record.setIdInkoopbron(row.getIdInkoopbron());
      record.setIdAdresTaxateur(row.getIdAdresTaxateur());
      record.setVerkoopBtw(row.getVerkoopBtw());
      record.setOmschrijvIn(row.getOmschrijvIn());
      record.setDatumUit(row.getDatumUit());
      record.setVerkoopInstr(row.getVerkoopInstr());
      record.setIdAdresUit(row.getIdAdresUit());
      record.setReparaties(row.getReparaties());
      record.setMaat(row.getMaat());
      record.setAntique(row.isAntique());
      record.setAnno(row.getAnno());
      record.setIdInstrType(row.getIdInstrType());
      record.setFoto(row.getFoto());
      record.setDatumTaxatie(row.getDatumTaxatie());
   }


   /**
    * Maps a generated record onto the transfer object.
    */
   private static InstrumentRow toRow(InstrumentRecord record)
   {
      InstrumentRow row = new InstrumentRow();
      row.setId(record.getIdInstrument());
      row.setAanschafnr(record.getAanschafnr());
      row.setHuurnr(record.getHuurnr());
      row.setDatumIn(record.getDatumIn());
      row.setIdAdresIn(record.getIdAdresIn());
      row.setInkoopInstr(record.getInkoopInstr());
      row.setInkoopAcc(record.getInkoopAcc());
      row.setInkoopFactuur(record.getInkoopFactuur());
      row.setIdInkoopbron(record.getIdInkoopbron());
      row.setIdAdresTaxateur(record.getIdAdresTaxateur());
      row.setVerkoopBtw(record.getVerkoopBtw());
      row.setOmschrijvIn(record.getOmschrijvIn());
      row.setDatumUit(record.getDatumUit());
      row.setVerkoopInstr(record.getVerkoopInstr());
      row.setIdAdresUit(record.getIdAdresUit());
      row.setReparaties(record.getReparaties());
      row.setMaat(record.getMaat());
      row.setAntique(Boolean.TRUE.equals(record.getAntique()));
      row.setAnno(record.getAnno());
      row.setIdInstrType(record.getIdInstrType());
      row.setFoto(record.getFoto());
      row.setDatumTaxatie(record.getDatumTaxatie());
      return row;
   }
}
