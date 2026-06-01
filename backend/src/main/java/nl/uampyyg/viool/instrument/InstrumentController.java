package nl.uampyyg.viool.instrument;

import nl.uampyyg.viool.instrument.dto.InstrumentRow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST endpoints for single-instrument CRUD.
 *
 * <ul>
 *   <li>{@code GET /api/instrument/{id}} — fetch one instrument (404 if absent).</li>
 *   <li>{@code PUT /api/instrument} — insert (no id) or update (id present),
 *       returning the persisted row.</li>
 *   <li>{@code DELETE /api/instrument/{id}} — delete, 204 on success
 *       (404 if absent).</li>
 * </ul>
 *
 * <p>Multi-term search lives in a separate endpoint/ticket and is intentionally
 * not implemented here.
 */
@RestController
@RequestMapping("/api/instrument")
public class InstrumentController
{
   private static final Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

   private final InstrumentService service;


   public InstrumentController(InstrumentService service)
   {
      this.service = service;
   }


   /**
    * Fetches a single instrument by id.
    *
    * @param id the instrument id
    * @return {@code 200} with the row
    * @throws InstrumentNotFoundException ({@code 404}) when the id does not exist
    */
   @GetMapping("/{id}")
   public InstrumentRow getById(@PathVariable Long id)
   {
      return service.findById(id)
            .orElseThrow(() -> new InstrumentNotFoundException(id));
   }


   /**
    * Inserts or updates an instrument. A {@code null} id in the body triggers an
    * insert; a present id triggers a full update.
    *
    * @param row the instrument to persist
    * @return {@code 200} with the persisted row (including the generated id)
    */
   @PutMapping
   public InstrumentRow save(@RequestBody InstrumentRow row)
   {
      LOG.debug("Saving instrument (id={})", row.getId());
      return service.save(row);
   }


   /**
    * Deletes an instrument by id.
    *
    * @param id the instrument id
    * @return {@code 204 No Content}
    * @throws InstrumentNotFoundException ({@code 404}) when the id does not exist
    */
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(@PathVariable Long id)
   {
      service.delete(id);
      return ResponseEntity.noContent().build();
   }
}
