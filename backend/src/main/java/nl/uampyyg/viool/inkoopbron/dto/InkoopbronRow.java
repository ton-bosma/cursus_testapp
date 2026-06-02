package nl.uampyyg.viool.inkoopbron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for a single inkoopbron row exchanged with the frontend.
 *
 * <p>{@code markedDeleted} is request-only; it is never persisted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InkoopbronRow
{
   private Long id;
   private String omschrijving;
   private boolean rapporteren;
   private boolean markedDeleted;

   // ── Constructors ──────────────────────────────────────────────────────────

   public InkoopbronRow() {}

   public InkoopbronRow(Long id, String omschrijving, boolean rapporteren)
   {
      this.id = id;
      this.omschrijving = omschrijving;
      this.rapporteren = rapporteren;
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

   public boolean isRapporteren()
   {
      return rapporteren;
   }

   public void setRapporteren(boolean rapporteren)
   {
      this.rapporteren = rapporteren;
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
