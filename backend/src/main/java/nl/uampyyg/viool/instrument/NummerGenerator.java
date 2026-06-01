package nl.uampyyg.viool.instrument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Pure formatting and sequence calculation for instrument number generators.
 *
 * <p>This class contains only deterministic logic that can be unit-tested without
 * a database connection. The DB-side count/max queries are passed in as plain
 * {@code int} values so that the pure calculation is decoupled from jOOQ.
 *
 * <h2>Aanschafnummer — format {@code L.ddm.myy.NN}</h2>
 * <ol>
 *   <li>{@code L} — first letter (uppercase) of the inkoopbron omschrijving.</li>
 *   <li>Datum-in formatted {@code ddMMyy}, split with dots:
 *       {@code dd+first digit of MM} = first group, {@code last digit of MM + yy} = second group.
 *       Result: {@code L.ddm.myy.}.</li>
 *   <li>{@code NN} — two-digit zero-padded sequence: count of instruments sharing the same
 *       10-character prefix (exclusive of the current instrument) + 1.</li>
 * </ol>
 *
 * <h2>Huurnummer — format {@code yy*100 + volgnummer}</h2>
 * <ul>
 *   <li>{@code yy} = current year, 2 digits.</li>
 *   <li>{@code volgnummer} = max volgnummer among instruments of this year (exclusive of the
 *       current instrument) + 1; if none this year → 1.</li>
 * </ul>
 *
 * <p>Business rules: FO §5.1 (huurnr) and §5.2 (aanschafnr).
 */
public final class NummerGenerator
{
   private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("ddMMyy");


   private NummerGenerator()
   {
      // utility class — not instantiated
   }


   /**
    * Builds the 10-character aanschafnummer prefix {@code L.ddm.myy.}.
    *
    * <p>This prefix is used for the DB count query (match on
    * {@code SUBSTRING(aanschafnr, 1, 10) = prefix}).
    *
    * @param inkoopbronOmschrijving omschrijving of the inkoopbron; must be non-null and non-blank
    * @param datumIn                the datum-in of the instrument; must be non-null
    * @return the 10-character prefix, e.g. {@code "C.030.526."}
    * @throws IllegalArgumentException when {@code inkoopbronOmschrijving} or {@code datumIn}
    *                                  is null or blank (ADR-006: no silent failure)
    */
   public static String buildAanschafnrPrefix(String inkoopbronOmschrijving, LocalDate datumIn)
   {
      if (datumIn == null)
      {
         throw new IllegalArgumentException("datumIn is required to generate aanschafnr");
      }
      if (inkoopbronOmschrijving == null || inkoopbronOmschrijving.isBlank())
      {
         throw new IllegalArgumentException("inkoopbron omschrijving is required to generate aanschafnr");
      }

      String letter = String.valueOf(inkoopbronOmschrijving.charAt(0)).toUpperCase();
      String date   = datumIn.format(DATE_FMT);                // e.g. "030526" for 03-05-2026

      // L.ddm.myy.
      // date[0..2] = dd + first digit of MM,  date[3..5] = last digit of MM + yy
      return letter + "." + date.substring(0, 3) + "." + date.substring(3, 6) + ".";
   }


   /**
    * Builds the full aanschafnummer from the prefix and the count of existing instruments
    * that already share that prefix (excluding the current instrument).
    *
    * <p>The sequence number is {@code existingCount + 1}, zero-padded to 2 digits.
    *
    * @param prefix        the 10-character prefix as returned by {@link #buildAanschafnrPrefix}
    * @param existingCount count of instruments (other than the current one) sharing {@code prefix}
    * @return the full aanschafnummer, e.g. {@code "C.030.526.01"}
    */
   public static String buildAanschafnr(String prefix, int existingCount)
   {
      return prefix + String.format("%02d", existingCount + 1);
   }


   /**
    * Computes the huurnummer from the 2-digit year and the max existing volgnummer this year.
    *
    * @param twoDigitYear    current year as 2 digits (e.g. {@code 26} for 2026)
    * @param maxVolgnummerThisYear highest volgnummer found in the DB for this year, excluding the
    *                        current instrument; pass {@code 0} when no instruments exist this year
    * @return the new huurnummer, e.g. {@code 2601} for first instrument of 2026
    */
   public static int buildHuurnr(int twoDigitYear, int maxVolgnummerThisYear)
   {
      return twoDigitYear * 100 + (maxVolgnummerThisYear + 1);
   }
}
