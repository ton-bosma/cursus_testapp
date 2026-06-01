package nl.uampyyg.viool.inkoopbron;

import nl.uampyyg.viool.inkoopbron.dto.InkoopbronRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for INKOOPBRON reference data.
 *
 * <ul>
 *   <li>GET  /api/inkoopbron – returns the full list</li>
 *   <li>PUT  /api/inkoopbron – replaces the full list (three-pass save)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/inkoopbron")
public class InkoopbronController
{
   private static final Logger LOG = LoggerFactory.getLogger(InkoopbronController.class);

   private final IInkoopbronService service;

   public InkoopbronController(IInkoopbronService service)
   {
      this.service = service;
   }

   @GetMapping
   public ResponseEntity<List<InkoopbronRow>> getAll()
   {
      LOG.debug("GET /api/inkoopbron");
      return ResponseEntity.ok(service.findAll());
   }

   @PutMapping
   public ResponseEntity<List<InkoopbronRow>> saveAll(
         @RequestBody List<InkoopbronRow> rows)
   {
      LOG.debug("PUT /api/inkoopbron – {} rows received", rows.size());
      return ResponseEntity.ok(service.saveAll(rows));
   }
}
