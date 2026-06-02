package nl.uampyyg.viool.instrument;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nl.uampyyg.viool.instrument.dto.InstrumentRow;
import nl.uampyyg.viool.instrument.dto.InstrumentSearchRow;
import nl.uampyyg.viool.jooq.tables.records.InstrumentRecord;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static nl.uampyyg.viool.jooq.Tables.INSTR_TYPE;
import static nl.uampyyg.viool.jooq.Tables.INSTRUMENT;
import static org.jooq.impl.DSL.val;


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
    * Multi-term search on instruments.
    *
    * <p>The query terms in {@code terms} are AND-ed: a row is included only
    * when every term matches at least one of the following instrument-own
    * fields (case-insensitive substring):
    * <ul>
    *   <li>INSTR_TYPE.OMSCHRIJVING (type description)</li>
    *   <li>INSTRUMENT.MAAT</li>
    *   <li>INSTRUMENT.HUURNR left-padded to 4 digits (e.g. {@code "0042"})</li>
    *   <li>INSTRUMENT.AANSCHAFNR</li>
    *   <li>INSTRUMENT.OMSCHRIJV_IN</li>
    *   <li>INSTRUMENT.REPARATIES</li>
    *   <li>INSTRUMENT.DATUM_IN formatted {@code dd-MM-yyyy}</li>
    *   <li>INSTRUMENT.DATUM_UIT formatted {@code dd-MM-yyyy}</li>
    * </ul>
    *
    * <p><b>Address-search deviation</b>: FO §4.1 also lists inkoop/verkoop
    * address fields as searchable. The ADRES domain is out of v1 scope; those
    * fields are not searched here. See {@link InstrumentSearchRow} for details.
    *
    * <p>All search terms use jOOQ {@code likeIgnoreCase} / {@code DSL.lower}
    * with bound parameters — no string concatenation into SQL (ADR-003).
    *
    * @param terms    search terms (already split by the caller); empty → no
    *                 term condition (all rows match)
    * @param archief  when {@code false} only non-archived rows ({@code datum_uit IS NULL})
    *                 are returned; when {@code true} archived rows are included too
    * @param max      maximum number of rows to return; {@code -1} or {@code null}
    *                 means unlimited
    * @return the matching rows ordered by aanschafnr
    */
   public List<InstrumentSearchRow> search(List<String> terms, boolean archief, Integer max)
   {
      // Date formatter for the formatted-date search fields
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

      // Archive filter: default = active only (datum_uit IS NULL)
      Condition baseCondition = archief
            ? DSL.trueCondition()
            : INSTRUMENT.DATUM_UIT.isNull();

      // AND over terms: each term must match at least one of the searchable fields
      for (String term : terms)
      {
         String pattern = "%" + term.toLowerCase() + "%";

         // huurnr padded to 4 digits: lpad(cast(huurnr as varchar), 4, '0')
         Field<String> huurnrPadded = DSL.field(
               "lpad(cast({0} as varchar), 4, '0')",
               String.class,
               INSTRUMENT.HUURNR);

         // datum_in formatted dd-MM-yyyy
         Field<String> datumInFormatted = DSL.field(
               "to_char({0}, 'DD-MM-YYYY')",
               String.class,
               INSTRUMENT.DATUM_IN);

         // datum_uit formatted dd-MM-yyyy
         Field<String> datumUitFormatted = DSL.field(
               "to_char({0}, 'DD-MM-YYYY')",
               String.class,
               INSTRUMENT.DATUM_UIT);

         Condition termCondition = DSL.or(
               DSL.lower(INSTR_TYPE.OMSCHRIJVING).like(DSL.val(pattern)),
               DSL.lower(INSTRUMENT.MAAT).like(DSL.val(pattern)),
               DSL.lower(huurnrPadded).like(DSL.val(pattern)),
               DSL.lower(INSTRUMENT.AANSCHAFNR).like(DSL.val(pattern)),
               DSL.lower(INSTRUMENT.OMSCHRIJV_IN).like(DSL.val(pattern)),
               DSL.lower(INSTRUMENT.REPARATIES).like(DSL.val(pattern)),
               datumInFormatted.like(DSL.val(pattern)),
               datumUitFormatted.like(DSL.val(pattern))
         );

         baseCondition = baseCondition.and(termCondition);
      }

      var query = dsl
            .select(
                  INSTRUMENT.ID_INSTRUMENT,
                  INSTRUMENT.HUURNR,
                  INSTRUMENT.AANSCHAFNR,
                  INSTR_TYPE.OMSCHRIJVING,
                  INSTRUMENT.MAAT,
                  INSTRUMENT.DATUM_IN,
                  INSTRUMENT.DATUM_UIT)
            .from(INSTRUMENT)
            .leftJoin(INSTR_TYPE)
            .on(INSTR_TYPE.ID_INSTR_TYPE.eq(INSTRUMENT.ID_INSTR_TYPE))
            .where(baseCondition)
            .orderBy(INSTRUMENT.AANSCHAFNR.asc());

      List<InstrumentSearchRow> result = new ArrayList<>();
      if (max != null && max > 0)
      {
         result = query.limit(max).fetch(InstrumentRepository::toSearchRow);
      }
      else
      {
         result = query.fetch(InstrumentRepository::toSearchRow);
      }
      return result;
   }


   /**
    * Returns the highest 2-digit sequence number (positions 11-12 of the
    * {@code aanschafnr}) among instruments whose first 10 characters equal
    * {@code prefix}, excluding the instrument identified by {@code excludeId}.
    *
    * <p>Used to compute the next sequence number {@code NN} in the
    * aanschafnummer format {@code L.ddm.myy.NN} (FO §5.2). Using MAX instead
    * of COUNT avoids re-collision after a delete (gaps in the sequence no
    * longer cause duplicate numbers). The exclusion of the current instrument
    * ensures re-generating a number for an existing instrument yields a stable
    * result.
    *
    * @param prefix    the 10-character prefix to match (e.g. {@code "C.030.526."})
    * @param excludeId the id of the instrument to exclude; pass {@code -1} for new instruments
    * @return the maximum sequence number found (0 when no instruments share this prefix)
    */
   public int maxAanschafnrSeqByPrefix(String prefix, long excludeId)
   {
      // SUBSTRING(aanschafnr, 1, 10) = first 10 chars (PostgreSQL 1-based)
      Field<String> aanschafnrPrefix = DSL.field(
            "substring({0}, 1, 10)",
            String.class,
            INSTRUMENT.AANSCHAFNR);

      // positions 11-12 of aanschafnr = the 2-digit NN sequence
      Field<Integer> seqPart = DSL.field(
            "cast(substring({0}, 11, 2) as integer)",
            Integer.class,
            INSTRUMENT.AANSCHAFNR);

      Integer max = dsl.select(DSL.max(seqPart))
            .from(INSTRUMENT)
            .where(INSTRUMENT.AANSCHAFNR.isNotNull())
            .and(aanschafnrPrefix.eq(val(prefix)))
            .and(INSTRUMENT.ID_INSTRUMENT.ne(excludeId))
            .fetchOne(DSL.max(seqPart));

      return max != null ? max : 0;
   }


   /**
    * Returns the highest volgnummer (positions 3-4 of the left-padded huurnr)
    * among instruments of the given 2-digit year, excluding the instrument
    * identified by {@code excludeId}.
    *
    * <p>Used to compute the next huurnummer (FO §5.1).
    * The huurnr is left-padded to 4 digits before extracting positions:
    * chars 1-2 = year, chars 3-4 = volgnummer.
    *
    * @param twoDigitYear the current year as a zero-padded 2-character string (e.g. {@code "26"})
    * @param excludeId    the id of the instrument to exclude; pass {@code -1} for new instruments
    * @return the maximum volgnummer this year, or {@code 0} when no instruments exist this year
    */
   public int maxVolgnummerThisYear(String twoDigitYear, long excludeId)
   {
      // lpad(cast(huurnr as varchar), 4, '0')
      Field<String> padded = DSL.field(
            "lpad(cast({0} as varchar), 4, '0')",
            String.class,
            INSTRUMENT.HUURNR);

      // year part = first 2 chars, volgnummer part = chars 3-4
      Field<String> yearPart      = DSL.field("substring({0}, 1, 2)", String.class, padded);
      Field<Integer> volgnrPart   = DSL.field(
            "cast(substring({0}, 3, 2) as integer)",
            Integer.class,
            padded);

      Integer max = dsl.select(DSL.max(volgnrPart))
            .from(INSTRUMENT)
            .where(INSTRUMENT.HUURNR.isNotNull())
            .and(yearPart.eq(val(twoDigitYear)))
            .and(INSTRUMENT.ID_INSTRUMENT.ne(excludeId))
            .fetchOne(DSL.max(volgnrPart));

      return max != null ? max : 0;
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
    * Maps a jOOQ record from the search query onto an {@link InstrumentSearchRow}.
    * {@code inkoopAdres} and {@code verkoopAdres} are set to {@code ""} because
    * the ADRES domain is not yet implemented in v1.
    */
   private static InstrumentSearchRow toSearchRow(org.jooq.Record record)
   {
      InstrumentSearchRow row = new InstrumentSearchRow();
      row.setId(record.get(INSTRUMENT.ID_INSTRUMENT));
      row.setHuurnr(record.get(INSTRUMENT.HUURNR));
      row.setAanschafnr(record.get(INSTRUMENT.AANSCHAFNR));
      String omschrijving = record.get(INSTR_TYPE.OMSCHRIJVING);
      row.setType(omschrijving != null ? omschrijving : "");
      row.setMaat(record.get(INSTRUMENT.MAAT));
      // ADRES domain not yet implemented in v1; address fields always empty
      row.setInkoopAdres("");
      row.setVerkoopAdres("");
      row.setAanschafdatum(record.get(INSTRUMENT.DATUM_IN));
      row.setVerkoopdatum(record.get(INSTRUMENT.DATUM_UIT));
      return row;
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
