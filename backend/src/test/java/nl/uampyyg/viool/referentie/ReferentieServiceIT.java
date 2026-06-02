package nl.uampyyg.viool.referentie;

import nl.uampyyg.viool.AbstractIntegrationTest;
import nl.uampyyg.viool.inkoopbron.IInkoopbronService;
import nl.uampyyg.viool.inkoopbron.dto.InkoopbronRow;
import nl.uampyyg.viool.instrumenttype.IInstrumentTypeService;
import nl.uampyyg.viool.instrumenttype.dto.InstrumentTypeRow;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static nl.uampyyg.viool.jooq.Tables.INKOOPBRON;
import static nl.uampyyg.viool.jooq.Tables.INSTR_TYPE;
import static nl.uampyyg.viool.jooq.Tables.INSTRUMENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the referentiedata services (InstrumentType and Inkoopbron).
 *
 * <p>Verifies GET (findAll) and PUT (saveAll three-pass: delete/update/insert)
 * against a real PostgreSQL 16 database via Testcontainers.
 */
class ReferentieServiceIT extends AbstractIntegrationTest
{
   @Autowired
   private IInstrumentTypeService instrumentTypeService;

   @Autowired
   private IInkoopbronService inkoopbronService;

   @Autowired
   private DSLContext dsl;

   // ── Test data cleanup between tests ───────────────────────────────────────

   @BeforeEach
   void clearData()
   {
      // Delete in FK-safe order: instrument references both tables
      dsl.deleteFrom(INSTRUMENT).execute();
      dsl.deleteFrom(INSTR_TYPE).execute();
      dsl.deleteFrom(INKOOPBRON).execute();
   }

   // ── InstrumentType ────────────────────────────────────────────────────────

   @Test
   void instrumentType_getAll_emptyAfterClear()
   {
      List<InstrumentTypeRow> result = instrumentTypeService.findAll();
      assertThat(result).isEmpty();
   }

   @Test
   void instrumentType_insert_addsRows()
   {
      InstrumentTypeRow newRow = new InstrumentTypeRow(null, "Viool", new BigDecimal("12.50"));

      List<InstrumentTypeRow> result = instrumentTypeService.saveAll(List.of(newRow));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isNotNull();
      assertThat(result.get(0).getOmschrijving()).isEqualTo("Viool");
      assertThat(result.get(0).getForfaitAccessoires()).isEqualByComparingTo("12.50");
      assertThat(result.get(0).isMarkedDeleted()).isFalse();
   }

   @Test
   void instrumentType_update_changesExistingRow()
   {
      // Arrange – insert one row
      InstrumentTypeRow inserted = instrumentTypeService
            .saveAll(List.of(new InstrumentTypeRow(null, "Cello", new BigDecimal("8.00"))))
            .get(0);

      // Act – update via saveAll (id present, not marked deleted)
      inserted.setOmschrijving("Contrabas");
      inserted.setForfaitAccessoires(new BigDecimal("15.00"));
      List<InstrumentTypeRow> result = instrumentTypeService.saveAll(List.of(inserted));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(inserted.getId());
      assertThat(result.get(0).getOmschrijving()).isEqualTo("Contrabas");
      assertThat(result.get(0).getForfaitAccessoires()).isEqualByComparingTo("15.00");
   }

   @Test
   void instrumentType_delete_removesMarkedRow()
   {
      // Arrange – insert two rows
      List<InstrumentTypeRow> inserted = instrumentTypeService.saveAll(List.of(
            new InstrumentTypeRow(null, "Altviool", new BigDecimal("5.00")),
            new InstrumentTypeRow(null, "Harp", new BigDecimal("20.00"))));
      assertThat(inserted).hasSize(2);

      // Act – mark the first one deleted
      inserted.get(0).setMarkedDeleted(true);
      List<InstrumentTypeRow> result = instrumentTypeService.saveAll(inserted);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getOmschrijving()).isEqualTo("Harp");
   }

   @Test
   void instrumentType_threePassInOneCall_deleteUpdateInsert()
   {
      // Arrange – seed two rows
      List<InstrumentTypeRow> seed = instrumentTypeService.saveAll(List.of(
            new InstrumentTypeRow(null, "Piano", new BigDecimal("30.00")),
            new InstrumentTypeRow(null, "Trompet", new BigDecimal("10.00"))));

      // Act – delete first, update second, insert one new
      seed.get(0).setMarkedDeleted(true);
      seed.get(1).setOmschrijving("Trombone");

      InstrumentTypeRow brandNew = new InstrumentTypeRow(null, "Fluit", new BigDecimal("7.00"));

      List<InstrumentTypeRow> result = instrumentTypeService.saveAll(
            List.of(seed.get(0), seed.get(1), brandNew));

      assertThat(result).hasSize(2);
      assertThat(result).extracting(InstrumentTypeRow::getOmschrijving)
            .containsExactlyInAnyOrder("Trombone", "Fluit");
   }

   // ── Inkoopbron ────────────────────────────────────────────────────────────

   @Test
   void inkoopbron_getAll_emptyAfterClear()
   {
      List<InkoopbronRow> result = inkoopbronService.findAll();
      assertThat(result).isEmpty();
   }

   @Test
   void inkoopbron_insert_addsRows()
   {
      InkoopbronRow newRow = new InkoopbronRow(null, "Leverancier A", true);

      List<InkoopbronRow> result = inkoopbronService.saveAll(List.of(newRow));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isNotNull();
      assertThat(result.get(0).getOmschrijving()).isEqualTo("Leverancier A");
      assertThat(result.get(0).isRapporteren()).isTrue();
      assertThat(result.get(0).isMarkedDeleted()).isFalse();
   }

   @Test
   void inkoopbron_update_changesExistingRow()
   {
      // Arrange
      InkoopbronRow inserted = inkoopbronService
            .saveAll(List.of(new InkoopbronRow(null, "Oud", false)))
            .get(0);

      // Act
      inserted.setOmschrijving("Nieuw");
      inserted.setRapporteren(true);
      List<InkoopbronRow> result = inkoopbronService.saveAll(List.of(inserted));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(inserted.getId());
      assertThat(result.get(0).getOmschrijving()).isEqualTo("Nieuw");
      assertThat(result.get(0).isRapporteren()).isTrue();
   }

   @Test
   void inkoopbron_delete_removesMarkedRow()
   {
      // Arrange
      List<InkoopbronRow> inserted = inkoopbronService.saveAll(List.of(
            new InkoopbronRow(null, "Bron X", true),
            new InkoopbronRow(null, "Bron Y", false)));
      assertThat(inserted).hasSize(2);

      // Act – mark first deleted
      inserted.get(0).setMarkedDeleted(true);
      List<InkoopbronRow> result = inkoopbronService.saveAll(inserted);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getOmschrijving()).isEqualTo("Bron Y");
   }

   @Test
   void inkoopbron_threePassInOneCall_deleteUpdateInsert()
   {
      // Arrange
      List<InkoopbronRow> seed = inkoopbronService.saveAll(List.of(
            new InkoopbronRow(null, "Te verwijderen", true),
            new InkoopbronRow(null, "Te updaten", false)));

      // Act
      seed.get(0).setMarkedDeleted(true);
      seed.get(1).setOmschrijving("Geupdate");
      seed.get(1).setRapporteren(true);

      InkoopbronRow brandNew = new InkoopbronRow(null, "Nieuw ingevoerd", false);

      List<InkoopbronRow> result = inkoopbronService.saveAll(
            List.of(seed.get(0), seed.get(1), brandNew));

      assertThat(result).hasSize(2);
      assertThat(result).extracting(InkoopbronRow::getOmschrijving)
            .containsExactlyInAnyOrder("Geupdate", "Nieuw ingevoerd");
   }
}
