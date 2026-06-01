package nl.uampyyg.viool.instrument.dto;

import java.time.LocalDate;


/**
 * Transfer object for a single row in the instrument search result list.
 *
 * <p>This is intentionally a separate DTO from {@link InstrumentRow} (the
 * detail contract). The search list exposes only the subset of fields needed
 * by the frontend {@code IInstrumentRow} interface.
 *
 * <p>Field mapping:
 * <ul>
 *   <li>{@link #id}           ↔ INSTRUMENT.ID_INSTRUMENT</li>
 *   <li>{@link #huurnr}       ↔ INSTRUMENT.HUURNR</li>
 *   <li>{@link #aanschafnr}   ↔ INSTRUMENT.AANSCHAFNR</li>
 *   <li>{@link #type}         ↔ INSTR_TYPE.OMSCHRIJVING (LEFT JOIN; empty string when no type)</li>
 *   <li>{@link #maat}         ↔ INSTRUMENT.MAAT</li>
 *   <li>{@link #inkoopAdres}  — ADRES domain is out of v1 scope; always {@code ""}</li>
 *   <li>{@link #verkoopAdres} — ADRES domain is out of v1 scope; always {@code ""}</li>
 *   <li>{@link #aanschafdatum} ↔ INSTRUMENT.DATUM_IN (ISO-8601 or null)</li>
 *   <li>{@link #verkoopdatum}  ↔ INSTRUMENT.DATUM_UIT (ISO-8601 or null)</li>
 * </ul>
 *
 * <p><b>Address-search deviation</b>: FO §4.1 lists inkoop/verkoop address
 * fields (naam, straat, postcode, woonplaats, e-mail, telefoon) among the
 * searchable fields. Because the ADRES domain is not yet implemented in v1,
 * those fields are not joined and cannot be searched. Search covers only the
 * instrument's own fields and the type omschrijving.
 */
public class InstrumentSearchRow
{
   private Long      id;
   private Integer   huurnr;
   private String    aanschafnr;
   private String    type;
   private String    maat;
   private String    inkoopAdres;
   private String    verkoopAdres;
   private LocalDate aanschafdatum;
   private LocalDate verkoopdatum;


   public Long getId()
   {
      return id;
   }


   public void setId(Long id)
   {
      this.id = id;
   }


   public Integer getHuurnr()
   {
      return huurnr;
   }


   public void setHuurnr(Integer huurnr)
   {
      this.huurnr = huurnr;
   }


   public String getAanschafnr()
   {
      return aanschafnr;
   }


   public void setAanschafnr(String aanschafnr)
   {
      this.aanschafnr = aanschafnr;
   }


   public String getType()
   {
      return type;
   }


   public void setType(String type)
   {
      this.type = type;
   }


   public String getMaat()
   {
      return maat;
   }


   public void setMaat(String maat)
   {
      this.maat = maat;
   }


   public String getInkoopAdres()
   {
      return inkoopAdres;
   }


   public void setInkoopAdres(String inkoopAdres)
   {
      this.inkoopAdres = inkoopAdres;
   }


   public String getVerkoopAdres()
   {
      return verkoopAdres;
   }


   public void setVerkoopAdres(String verkoopAdres)
   {
      this.verkoopAdres = verkoopAdres;
   }


   public LocalDate getAanschafdatum()
   {
      return aanschafdatum;
   }


   public void setAanschafdatum(LocalDate aanschafdatum)
   {
      this.aanschafdatum = aanschafdatum;
   }


   public LocalDate getVerkoopdatum()
   {
      return verkoopdatum;
   }


   public void setVerkoopdatum(LocalDate verkoopdatum)
   {
      this.verkoopdatum = verkoopdatum;
   }
}
