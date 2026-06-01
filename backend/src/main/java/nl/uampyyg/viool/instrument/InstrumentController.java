package nl.uampyyg.viool.instrument;

import java.util.List;

import nl.uampyyg.viool.instrument.dto.InstrumentRow;
import nl.uampyyg.viool.instrument.dto.InstrumentSearchRow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST endpoints for instrument search and CRUD.
 *
 * <ul>
 *   <li>{@code GET /api/instrument} — multi-term search with archive filter
 *       and max-results ({@code ?q=&archief=&max=}).</li>
 *   <li>{@code GET /api/instrument/{id}} — fetch one instrument (404 if absent).</li>
 *   <li>{@code PUT /api/instrument} — insert (no id) or update (id present),
 *       returning the persisted row.</li>
 *   <li>{@code DELETE /api/instrument/{id}} — delete, 204 on success
 *       (404 if absent).</li>
 * </ul>
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
    * Multi-term search on instruments.
    *
    * <p>The query parameter {@code q} is split on whitespace; all terms must
    * match (AND). By default only active instruments ({@code datum_uit IS NULL})
    * are returned; pass {@code archief=true} to include archived ones.
    * The {@code max} parameter limits the result set (10/20/50/100;
    * omit or pass {@code -1} for unlimited).
    *
    * @param q       search string (optional, blank = no filter)
    * @param archief {@code true} to include archived instruments (default {@code false})
    * @param max     maximum rows (optional; {@code -1} = all)
    * @return {@code 200} with the list of matching rows
    */
   @GetMapping
   public List<InstrumentSearchRow> search(
         @RequestParam(required = false) String q,
         @RequestParam(required = false, defaultValue = "false") boolean archief,
         @RequestParam(required = false) Integer max)
   {
      LOG.debug("GET /api/instrument q={} archief={} max={}", q, archief, max);
      return service.search(q, archief, max);
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
