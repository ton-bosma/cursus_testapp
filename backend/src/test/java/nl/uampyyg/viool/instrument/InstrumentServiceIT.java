package nl.uampyyg.viool.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import nl.uampyyg.viool.AbstractIntegrationTest;
import nl.uampyyg.viool.instrument.dto.InstrumentRow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Integration test for {@link InstrumentService} against a real PostgreSQL 16
 * database (Testcontainers). Exercises the full CRUD lifecycle: insert returns
 * a generated id, update rewrites all columns, findById round-trips and delete
 * removes the row.
 */
class InstrumentServiceIT extends AbstractIntegrationTest
{
   @Autowired
   private InstrumentService service;


   @Test
   void insertWithoutIdGeneratesIdAndReturnsRow()
   {
      InstrumentRow row = sampleRow();

      InstrumentRow saved = service.save(row);

      assertThat(saved.getId()).isNotNull();
      assertThat(saved.getAanschafnr()).isEqualTo("A-001");
      assertThat(saved.getHuurnr()).isEqualTo(42);
      assertThat(saved.getDatumIn()).isEqualTo(LocalDate.of(2024, 1, 15));
      assertThat(saved.getInkoopInstr()).isEqualByComparingTo("1234.56");
      assertThat(saved.getInkoopAcc()).isEqualByComparingTo("78.90");
      assertThat(saved.getVerkoopBtw()).isEqualByComparingTo("21.00");
      assertThat(saved.isAntique()).isTrue();
      assertThat(saved.getOmschrijvIn()).isEqualTo("Mooie viool");
      assertThat(saved.getMaat()).isEqualTo("4/4");
   }


   @Test
   void findByIdReturnsPersistedRow()
   {
      InstrumentRow saved = service.save(sampleRow());

      Optional<InstrumentRow> found = service.findById(saved.getId());

      assertThat(found).isPresent();
      assertThat(found.get().getId()).isEqualTo(saved.getId());
      assertThat(found.get().getAanschafnr()).isEqualTo("A-001");
   }


   @Test
   void findByIdUnknownReturnsEmpty()
   {
      assertThat(service.findById(-9999L)).isEmpty();
   }


   @Test
   void updateRewritesAllFields()
   {
      InstrumentRow saved = service.save(sampleRow());
      Long id = saved.getId();

      InstrumentRow update = new InstrumentRow();
      update.setId(id);
      update.setAanschafnr("A-002");
      update.setHuurnr(7);
      update.setDatumIn(LocalDate.of(2025, 6, 1));
      update.setIdAdresIn(11L);
      update.setInkoopInstr(new BigDecimal("999.99"));
      update.setInkoopAcc(new BigDecimal("12.34"));
      update.setInkoopFactuur("F-99");
      update.setVerkoopBtw(new BigDecimal("9.00"));
      update.setOmschrijvIn("Aangepaste omschrijving");
      update.setDatumUit(LocalDate.of(2025, 7, 1));
      update.setVerkoopInstr(new BigDecimal("2000.00"));
      update.setIdAdresUit(22L);
      update.setReparaties("Nieuwe snaren");
      update.setMaat("3/4");
      update.setAntique(false);
      update.setAnno("1850");
      update.setFoto("foto2.jpg");
      update.setDatumTaxatie(LocalDate.of(2025, 5, 1));

      InstrumentRow result = service.save(update);

      assertThat(result.getId()).isEqualTo(id);
      assertThat(result.getAanschafnr()).isEqualTo("A-002");
      assertThat(result.getHuurnr()).isEqualTo(7);
      assertThat(result.getDatumIn()).isEqualTo(LocalDate.of(2025, 6, 1));
      assertThat(result.getIdAdresIn()).isEqualTo(11L);
      assertThat(result.getInkoopInstr()).isEqualByComparingTo("999.99");
      assertThat(result.isAntique()).isFalse();
      assertThat(result.getReparaties()).isEqualTo("Nieuwe snaren");
      assertThat(result.getMaat()).isEqualTo("3/4");

      // Update deliberately writes every column: a field omitted from the update
      // payload is cleared rather than preserved from the original insert.
      assertThat(result.getIdAdresTaxateur()).isNull();
   }


   @Test
   void updateUnknownIdThrows()
   {
      InstrumentRow row = sampleRow();
      row.setId(-12345L);

      assertThatThrownBy(() -> service.save(row))
            .isInstanceOf(InstrumentNotFoundException.class);
   }


   @Test
   void deleteRemovesRow()
   {
      InstrumentRow saved = service.save(sampleRow());
      Long id = saved.getId();

      service.delete(id);

      assertThat(service.findById(id)).isEmpty();
   }


   @Test
   void deleteUnknownIdThrows()
   {
      assertThatThrownBy(() -> service.delete(-555L))
            .isInstanceOf(InstrumentNotFoundException.class);
   }


   /**
    * Builds a fully populated instrument (no id) for insertion.
    */
   private static InstrumentRow sampleRow()
   {
      InstrumentRow row = new InstrumentRow();
      row.setAanschafnr("A-001");
      row.setHuurnr(42);
      row.setDatumIn(LocalDate.of(2024, 1, 15));
      row.setIdAdresIn(1L);
      row.setInkoopInstr(new BigDecimal("1234.56"));
      row.setInkoopAcc(new BigDecimal("78.90"));
      row.setInkoopFactuur("F-001");
      row.setIdAdresTaxateur(3L);
      row.setVerkoopBtw(new BigDecimal("21.00"));
      row.setOmschrijvIn("Mooie viool");
      row.setDatumUit(LocalDate.of(2024, 12, 31));
      row.setVerkoopInstr(new BigDecimal("1500.00"));
      row.setIdAdresUit(2L);
      row.setReparaties("Geen");
      row.setMaat("4/4");
      row.setAntique(true);
      row.setAnno("1900");
      row.setFoto("foto.jpg");
      row.setDatumTaxatie(LocalDate.of(2024, 2, 1));
      return row;
   }
}
