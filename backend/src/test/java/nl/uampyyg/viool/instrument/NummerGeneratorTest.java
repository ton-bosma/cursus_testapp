package nl.uampyyg.viool.instrument;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Pure-logic unit tests for {@link NummerGenerator}.
 *
 * <p>No database, no Spring context — the calculations are fully deterministic.
 */
class NummerGeneratorTest
{
   // ── buildAanschafnrPrefix ─────────────────────────────────────────────────

   @Test
   void aanschafnrPrefix_christiesAndMay2026()
   {
      // "Christie's" + 03-05-2026 → C.030.526.
      String prefix = NummerGenerator.buildAanschafnrPrefix(
            "Christie's", LocalDate.of(2026, 5, 3));

      assertThat(prefix).isEqualTo("C.030.526.");
   }

   @Test
   void aanschafnrPrefix_lowerCaseFirstLetterIsUppercased()
   {
      String prefix = NummerGenerator.buildAanschafnrPrefix(
            "amsterdam", LocalDate.of(2025, 1, 15));

      assertThat(prefix).startsWith("A.");
   }

   @Test
   void aanschafnrPrefix_singleDigitDayAndMonth()
   {
      // 01-01-24 → "V.010.124."
      String prefix = NummerGenerator.buildAanschafnrPrefix(
            "Vanderveen", LocalDate.of(2024, 1, 1));

      assertThat(prefix).isEqualTo("V.010.124.");
   }

   @Test
   void aanschafnrPrefix_prefixIsAlways10Chars()
   {
      String prefix = NummerGenerator.buildAanschafnrPrefix(
            "Christie's", LocalDate.of(2026, 5, 3));

      assertThat(prefix).hasSize(10);
   }

   @Test
   void aanschafnrPrefix_throwsWhenDatumInNull()
   {
      assertThatThrownBy(() ->
            NummerGenerator.buildAanschafnrPrefix("Christie's", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("datumIn");
   }

   @Test
   void aanschafnrPrefix_throwsWhenOmschrijvingNull()
   {
      assertThatThrownBy(() ->
            NummerGenerator.buildAanschafnrPrefix(null, LocalDate.of(2026, 5, 3)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("omschrijving");
   }

   @Test
   void aanschafnrPrefix_throwsWhenOmschrijvingBlank()
   {
      assertThatThrownBy(() ->
            NummerGenerator.buildAanschafnrPrefix("  ", LocalDate.of(2026, 5, 3)))
            .isInstanceOf(IllegalArgumentException.class);
   }

   // ── buildAanschafnr ───────────────────────────────────────────────────────

   @Test
   void aanschafnr_firstInSeries()
   {
      // no existing instruments with this prefix → maxSeq = 0 → NN = 01
      String nr = NummerGenerator.buildAanschafnr("C.030.526.", 0);

      assertThat(nr).isEqualTo("C.030.526.01");
   }

   @Test
   void aanschafnr_secondInSeries()
   {
      String nr = NummerGenerator.buildAanschafnr("C.030.526.", 1);

      assertThat(nr).isEqualTo("C.030.526.02");
   }

   @Test
   void aanschafnr_largeMaxSeq_zeropaddedTwoDigits()
   {
      String nr = NummerGenerator.buildAanschafnr("C.030.526.", 9);

      assertThat(nr).isEqualTo("C.030.526.10");
   }

   @Test
   void aanschafnr_throwsWhenSequenceExhausted()
   {
      // maxSeq = 99 → next would be 100 > 99, must throw
      assertThatThrownBy(() -> NummerGenerator.buildAanschafnr("C.030.526.", 99))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("aanschafnr sequence exhausted");
   }

   // ── buildHuurnr ───────────────────────────────────────────────────────────

   @Test
   void huurnr_firstInstrumentOf2026()
   {
      // no instruments this year → maxVolgnummer = 0 → 26*100+1 = 2601
      int nr = NummerGenerator.buildHuurnr(26, 0);

      assertThat(nr).isEqualTo(2601);
   }

   @Test
   void huurnr_secondInstrumentOf2026()
   {
      int nr = NummerGenerator.buildHuurnr(26, 1);

      assertThat(nr).isEqualTo(2602);
   }

   @Test
   void huurnr_yearRollover()
   {
      // year 0 (2000) with no prior instruments → 1
      int nr = NummerGenerator.buildHuurnr(0, 0);

      assertThat(nr).isEqualTo(1);
   }

   @Test
   void huurnr_yearBoundary2000()
   {
      // year 00 * 100 + 1 = 1
      assertThat(NummerGenerator.buildHuurnr(0, 0)).isEqualTo(1);
   }

   @Test
   void huurnr_year99_firstInstrument()
   {
      assertThat(NummerGenerator.buildHuurnr(99, 0)).isEqualTo(9901);
   }

   @Test
   void huurnr_throwsWhenSequenceExhausted()
   {
      // maxVolgnummer = 99 → next would be 100 > 99, must throw
      assertThatThrownBy(() -> NummerGenerator.buildHuurnr(26, 99))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("huurnr volgnummer exhausted");
   }

   // ── integration of prefix + sequence ─────────────────────────────────────

   @Test
   void fullAanschafnr_christies_03May2026_firstInSeries()
   {
      String prefix = NummerGenerator.buildAanschafnrPrefix(
            "Christie's", LocalDate.of(2026, 5, 3));
      String nr = NummerGenerator.buildAanschafnr(prefix, 0);

      assertThat(nr).isEqualTo("C.030.526.01");
   }
}
