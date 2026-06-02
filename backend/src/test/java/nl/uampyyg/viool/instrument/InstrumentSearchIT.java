package nl.uampyyg.viool.instrument;

import java.time.LocalDate;
import java.util.List;

import nl.uampyyg.viool.AbstractIntegrationTest;
import nl.uampyyg.viool.instrument.dto.InstrumentRow;
import nl.uampyyg.viool.instrument.dto.InstrumentSearchRow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integration test for {@link InstrumentService#search} against a real
 * PostgreSQL 16 database (Testcontainers).
 *
 * <p>Each test runs in a transaction that is rolled back after the test
 * completes, keeping the database clean between tests.
 *
 * <p>Verifies:
 * <ul>
 *   <li>Multi-term search (AND): two terms → only rows where both terms match.</li>
 *   <li>Archive filter: {@code archief=false} excludes rows with {@code datum_uit} set.</li>
 *   <li>Max-results: the result is capped at the requested limit.</li>
 *   <li>Result fields match the {@code IInstrumentRow} contract.</li>
 * </ul>
 */
@Transactional
class InstrumentSearchIT extends AbstractIntegrationTest
{
   @Autowired
   private InstrumentService service;

   @Autowired
   private InstrumentRepository repository;

   // IDs of inserted test rows — kept so tests can assert on specific rows
   private Long idViool4;
   private Long idViool3;
   private Long idCello;
   private Long idArchived;


   /**
    * Inserts a small, known set of instruments before each test.
    * No type rows are available in the test DB (no FK constraint on id_instr_type),
    * so the type JOIN always returns NULL, which maps to an empty string.
    */
   @BeforeEach
   void setUp()
   {
      // huurnr=1111 pads to "1111"; chosen so "1111" does not appear in any
      // aanschafnr or description field of other setUp rows.
      idViool4   = insertRow("V.010124.01", 1111, "4/4",  "Mooie viool rode streep",   "Geen reparaties",  LocalDate.of(2024, 1,  10), null);
      idViool3   = insertRow("V.010124.02", 102,  "3/4",  "Kleine viool blauwe streep", "Nieuwe snaren",   LocalDate.of(2024, 2,  20), null);
      idCello    = insertRow("C.010124.01", null, "4/4",  "Cello",                      "Stokje bijgesteld", LocalDate.of(2024, 3, 1),  null);
      idArchived = insertRow("V.010123.01", 99,   "4/4",  "Oude viool",                "Veel reparaties",  LocalDate.of(2023, 1,  1),  LocalDate.of(2023, 12, 31));
   }


   // -------------------------------------------------------------------------
   // Multi-term AND search
   // -------------------------------------------------------------------------

   @Test
   void singleTermMatchesSubstring()
   {
      List<InstrumentSearchRow> result = service.search("viool", false, null);

      // archived row is excluded by default; active viool rows should both match
      assertThat(result).extracting(InstrumentSearchRow::getId)
            .contains(idViool4, idViool3)
            .doesNotContain(idCello, idArchived);
   }


   @Test
   void twoTermsBothMustMatch()
   {
      // "3/4" only matches idViool3; "blauwe" only matches idViool3
      // → only idViool3 should appear
      List<InstrumentSearchRow> result = service.search("3/4 blauwe", false, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .containsExactly(idViool3);
   }


   @Test
   void twoTermsNoRowMatchesBothReturnsEmpty()
   {
      // "viool" matches V rows; "cello" matches C row; no row has both
      List<InstrumentSearchRow> result = service.search("viool cello", false, null);

      assertThat(result).isEmpty();
   }


   @Test
   void searchIsCaseInsensitive()
   {
      List<InstrumentSearchRow> result = service.search("VIOOL RODE", false, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .containsExactly(idViool4);
   }


   @Test
   void searchOnAanschafnr()
   {
      List<InstrumentSearchRow> result = service.search("C.010124", false, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .containsExactly(idCello);
   }


   @Test
   void searchOnHuurnrPadded()
   {
      // idViool4 has huurnr=1111; padded = "1111"
      // No other setUp row has "1111" in any searchable field
      List<InstrumentSearchRow> result = service.search("1111", false, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .containsExactly(idViool4);
   }


   @Test
   void searchOnDatumIn()
   {
      // idViool3 has datum_in=2024-02-20 → "20-02-2024"
      List<InstrumentSearchRow> result = service.search("20-02-2024", false, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .containsExactly(idViool3);
   }


   // -------------------------------------------------------------------------
   // Archive filter
   // -------------------------------------------------------------------------

   @Test
   void archiefFalseExcludesRowsWithDatumUit()
   {
      List<InstrumentSearchRow> result = service.search("viool", false, null);

      // idArchived has datum_uit set → must not appear
      assertThat(result).extracting(InstrumentSearchRow::getId)
            .doesNotContain(idArchived);
   }


   @Test
   void archiefTrueIncludesArchivedRows()
   {
      List<InstrumentSearchRow> result = service.search("viool", true, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .contains(idViool4, idViool3, idArchived);
   }


   @Test
   void searchOnDatumUitOnlyVisibleWithArchiefTrue()
   {
      // idArchived has datum_uit=2023-12-31 → "31-12-2023"
      List<InstrumentSearchRow> withoutArchief = service.search("31-12-2023", false, null);
      List<InstrumentSearchRow> withArchief    = service.search("31-12-2023", true,  null);

      assertThat(withoutArchief).isEmpty();
      assertThat(withArchief).extracting(InstrumentSearchRow::getId)
            .contains(idArchived);
   }


   // -------------------------------------------------------------------------
   // Max-results
   // -------------------------------------------------------------------------

   @Test
   void maxLimitsResults()
   {
      // Insert enough extra rows so we can cap to 2
      insertRow("X.000001.01", null, "4/4", "Extra viool een", "geen", LocalDate.of(2024, 4, 1), null);
      insertRow("X.000002.01", null, "4/4", "Extra viool twee", "geen", LocalDate.of(2024, 4, 2), null);
      insertRow("X.000003.01", null, "4/4", "Extra viool drie", "geen", LocalDate.of(2024, 4, 3), null);

      List<InstrumentSearchRow> result = service.search("viool", false, 2);

      assertThat(result).hasSize(2);
   }


   @Test
   void maxMinusOneReturnsAll()
   {
      List<InstrumentSearchRow> result = service.search("viool", false, -1);

      // idViool4 + idViool3 are active viool rows
      assertThat(result).hasSizeGreaterThanOrEqualTo(2);
   }


   @Test
   void maxNullReturnsAll()
   {
      List<InstrumentSearchRow> result = service.search("viool", false, null);

      assertThat(result).hasSizeGreaterThanOrEqualTo(2);
   }


   // -------------------------------------------------------------------------
   // Result contract (IInstrumentRow fields)
   // -------------------------------------------------------------------------

   @Test
   void resultRowMatchesFrontendContract()
   {
      List<InstrumentSearchRow> result = service.search("V.010124.01", false, null);

      assertThat(result).hasSize(1);
      InstrumentSearchRow row = result.get(0);

      assertThat(row.getId()).isEqualTo(idViool4);
      assertThat(row.getHuurnr()).isEqualTo(1111);
      assertThat(row.getAanschafnr()).isEqualTo("V.010124.01");
      // No INSTR_TYPE row in test DB → type resolves to empty string
      assertThat(row.getType()).isEqualTo("");
      assertThat(row.getMaat()).isEqualTo("4/4");
      // ADRES domain not yet in v1 — always empty string
      assertThat(row.getInkoopAdres()).isEqualTo("");
      assertThat(row.getVerkoopAdres()).isEqualTo("");
      assertThat(row.getAanschafdatum()).isEqualTo(LocalDate.of(2024, 1, 10));
      assertThat(row.getVerkoopdatum()).isNull();
   }


   @Test
   void emptyQueryReturnsAllActiveRows()
   {
      List<InstrumentSearchRow> result = service.search("", false, null);

      // At least the three active rows from setUp
      assertThat(result).extracting(InstrumentSearchRow::getId)
            .contains(idViool4, idViool3, idCello)
            .doesNotContain(idArchived);
   }


   @Test
   void nullQueryReturnsAllActiveRows()
   {
      List<InstrumentSearchRow> result = service.search(null, false, null);

      assertThat(result).extracting(InstrumentSearchRow::getId)
            .contains(idViool4, idViool3, idCello)
            .doesNotContain(idArchived);
   }


   // -------------------------------------------------------------------------
   // Helper
   // -------------------------------------------------------------------------

   private Long insertRow(
         String aanschafnr, Integer huurnr, String maat,
         String omschrijvIn, String reparaties,
         LocalDate datumIn, LocalDate datumUit)
   {
      InstrumentRow row = new InstrumentRow();
      row.setAanschafnr(aanschafnr);
      row.setHuurnr(huurnr);
      row.setMaat(maat);
      row.setOmschrijvIn(omschrijvIn);
      row.setReparaties(reparaties);
      row.setDatumIn(datumIn);
      row.setDatumUit(datumUit);
      return repository.insert(row);
   }
}
