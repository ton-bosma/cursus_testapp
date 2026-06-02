package nl.uampyyg.viool.instrumenttype.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO for a single instrument-type row exchanged with the frontend.
 *
 * <p>{@code markedDeleted} is request-only; it is never persisted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstrumentTypeRow
{
   private Long id;
   private String omschrijving;
   private BigDecimal forfaitAccessoires;
   private boolean markedDeleted;

   // ── Constructors ──────────────────────────────────────────────────────────

   public InstrumentTypeRow() {}

   public InstrumentTypeRow(Long id, String omschrijving, BigDecimal forfaitAccessoires)
   {
      this.id = id;
      this.omschrijving = omschrijving;
      this.forfaitAccessoires = forfaitAccessoires;
      this.markedDeleted = false;
   }

   // ── Getters & Setters ─────────────────────────────────────────────────────

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getOmschrijving()
   {
      return omschrijving;
   }

   public void setOmschrijving(String omschrijving)
   {
      this.omschrijving = omschrijving;
   }

   public BigDecimal getForfaitAccessoires()
   {
      return forfaitAccessoires;
   }

   public void setForfaitAccessoires(BigDecimal forfaitAccessoires)
   {
      this.forfaitAccessoires = forfaitAccessoires;
   }

   public boolean isMarkedDeleted()
   {
      return markedDeleted;
   }

   public void setMarkedDeleted(boolean markedDeleted)
   {
      this.markedDeleted = markedDeleted;
   }
}
