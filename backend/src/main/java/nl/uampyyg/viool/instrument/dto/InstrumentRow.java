package nl.uampyyg.viool.instrument.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * Transfer object for a single INSTRUMENT row.
 *
 * <p>Field names match the camelCase JSON contract consumed by the frontend
 * instrument detail screen. Each field maps one-to-one to a column on the
 * jOOQ-generated INSTRUMENT table:
 *
 * <ul>
 *   <li>{@link #id} ↔ ID_INSTRUMENT — {@code null} signals an insert,
 *       a non-null value signals an update.</li>
 *   <li>{@link #aanschafnr} ↔ AANSCHAFNR</li>
 *   <li>{@link #huurnr} ↔ HUURNR</li>
 *   <li>{@link #datumIn} ↔ DATUM_IN</li>
 *   <li>{@link #idAdresIn} ↔ ID_ADRES_IN</li>
 *   <li>{@link #inkoopInstr} ↔ INKOOP_INSTR</li>
 *   <li>{@link #inkoopAcc} ↔ INKOOP_ACC</li>
 *   <li>{@link #inkoopFactuur} ↔ INKOOP_FACTUUR</li>
 *   <li>{@link #idInkoopbron} ↔ ID_INKOOPBRON</li>
 *   <li>{@link #idAdresTaxateur} ↔ ID_ADRES_TAXATEUR</li>
 *   <li>{@link #verkoopBtw} ↔ VERKOOP_BTW</li>
 *   <li>{@link #omschrijvIn} ↔ OMSCHRIJV_IN</li>
 *   <li>{@link #datumUit} ↔ DATUM_UIT</li>
 *   <li>{@link #verkoopInstr} ↔ VERKOOP_INSTR</li>
 *   <li>{@link #idAdresUit} ↔ ID_ADRES_UIT</li>
 *   <li>{@link #reparaties} ↔ REPARATIES</li>
 *   <li>{@link #maat} ↔ MAAT</li>
 *   <li>{@link #antique} ↔ ANTIQUE</li>
 *   <li>{@link #anno} ↔ ANNO</li>
 *   <li>{@link #idInstrType} ↔ ID_INSTR_TYPE</li>
 *   <li>{@link #foto} ↔ FOTO</li>
 *   <li>{@link #datumTaxatie} ↔ DATUM_TAXATIE</li>
 * </ul>
 *
 * <p>All fields are nullable except for the insert/update semantics carried by
 * {@link #id}. {@code antique} is a primitive {@code boolean} (the column is
 * {@code NOT NULL DEFAULT FALSE}); a missing JSON value therefore defaults to
 * {@code false}.
 */
public class InstrumentRow
{
   private Long       id;
   private String     aanschafnr;
   private Integer    huurnr;
   private LocalDate  datumIn;
   private Long       idAdresIn;
   private BigDecimal inkoopInstr;
   private BigDecimal inkoopAcc;
   private String     inkoopFactuur;
   private Long       idInkoopbron;
   private Long       idAdresTaxateur;
   private BigDecimal verkoopBtw;
   private String     omschrijvIn;
   private LocalDate  datumUit;
   private BigDecimal verkoopInstr;
   private Long       idAdresUit;
   private String     reparaties;
   private String     maat;
   private boolean    antique;
   private String     anno;
   private Long       idInstrType;
   private String     foto;
   private LocalDate  datumTaxatie;


   public Long getId()
   {
      return id;
   }


   public void setId(Long id)
   {
      this.id = id;
   }


   public String getAanschafnr()
   {
      return aanschafnr;
   }


   public void setAanschafnr(String aanschafnr)
   {
      this.aanschafnr = aanschafnr;
   }


   public Integer getHuurnr()
   {
      return huurnr;
   }


   public void setHuurnr(Integer huurnr)
   {
      this.huurnr = huurnr;
   }


   public LocalDate getDatumIn()
   {
      return datumIn;
   }


   public void setDatumIn(LocalDate datumIn)
   {
      this.datumIn = datumIn;
   }


   public Long getIdAdresIn()
   {
      return idAdresIn;
   }


   public void setIdAdresIn(Long idAdresIn)
   {
      this.idAdresIn = idAdresIn;
   }


   public BigDecimal getInkoopInstr()
   {
      return inkoopInstr;
   }


   public void setInkoopInstr(BigDecimal inkoopInstr)
   {
      this.inkoopInstr = inkoopInstr;
   }


   public BigDecimal getInkoopAcc()
   {
      return inkoopAcc;
   }


   public void setInkoopAcc(BigDecimal inkoopAcc)
   {
      this.inkoopAcc = inkoopAcc;
   }


   public String getInkoopFactuur()
   {
      return inkoopFactuur;
   }


   public void setInkoopFactuur(String inkoopFactuur)
   {
      this.inkoopFactuur = inkoopFactuur;
   }


   public Long getIdInkoopbron()
   {
      return idInkoopbron;
   }


   public void setIdInkoopbron(Long idInkoopbron)
   {
      this.idInkoopbron = idInkoopbron;
   }


   public Long getIdAdresTaxateur()
   {
      return idAdresTaxateur;
   }


   public void setIdAdresTaxateur(Long idAdresTaxateur)
   {
      this.idAdresTaxateur = idAdresTaxateur;
   }


   public BigDecimal getVerkoopBtw()
   {
      return verkoopBtw;
   }


   public void setVerkoopBtw(BigDecimal verkoopBtw)
   {
      this.verkoopBtw = verkoopBtw;
   }


   public String getOmschrijvIn()
   {
      return omschrijvIn;
   }


   public void setOmschrijvIn(String omschrijvIn)
   {
      this.omschrijvIn = omschrijvIn;
   }


   public LocalDate getDatumUit()
   {
      return datumUit;
   }


   public void setDatumUit(LocalDate datumUit)
   {
      this.datumUit = datumUit;
   }


   public BigDecimal getVerkoopInstr()
   {
      return verkoopInstr;
   }


   public void setVerkoopInstr(BigDecimal verkoopInstr)
   {
      this.verkoopInstr = verkoopInstr;
   }


   public Long getIdAdresUit()
   {
      return idAdresUit;
   }


   public void setIdAdresUit(Long idAdresUit)
   {
      this.idAdresUit = idAdresUit;
   }


   public String getReparaties()
   {
      return reparaties;
   }


   public void setReparaties(String reparaties)
   {
      this.reparaties = reparaties;
   }


   public String getMaat()
   {
      return maat;
   }


   public void setMaat(String maat)
   {
      this.maat = maat;
   }


   public boolean isAntique()
   {
      return antique;
   }


   public void setAntique(boolean antique)
   {
      this.antique = antique;
   }


   public String getAnno()
   {
      return anno;
   }


   public void setAnno(String anno)
   {
      this.anno = anno;
   }


   public Long getIdInstrType()
   {
      return idInstrType;
   }


   public void setIdInstrType(Long idInstrType)
   {
      this.idInstrType = idInstrType;
   }


   public String getFoto()
   {
      return foto;
   }


   public void setFoto(String foto)
   {
      this.foto = foto;
   }


   public LocalDate getDatumTaxatie()
   {
      return datumTaxatie;
   }


   public void setDatumTaxatie(LocalDate datumTaxatie)
   {
      this.datumTaxatie = datumTaxatie;
   }
}
