package nl.uampyyg.viool.instrumenttype;

import nl.uampyyg.viool.instrumenttype.dto.InstrumentTypeRow;
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
 * REST endpoints for INSTR_TYPE reference data.
 *
 * <ul>
 *   <li>GET  /api/instrumenttype – returns the full list</li>
 *   <li>PUT  /api/instrumenttype – replaces the full list (three-pass save)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/instrumenttype")
public class InstrumentTypeController
{
   private static final Logger LOG = LoggerFactory.getLogger(InstrumentTypeController.class);

   private final IInstrumentTypeService service;

   public InstrumentTypeController(IInstrumentTypeService service)
   {
      this.service = service;
   }

   @GetMapping
   public ResponseEntity<List<InstrumentTypeRow>> getAll()
   {
      LOG.debug("GET /api/instrumenttype");
      return ResponseEntity.ok(service.findAll());
   }

   @PutMapping
   public ResponseEntity<List<InstrumentTypeRow>> saveAll(
         @RequestBody List<InstrumentTypeRow> rows)
   {
      LOG.debug("PUT /api/instrumenttype – {} rows received", rows.size());
      return ResponseEntity.ok(service.saveAll(rows));
   }
}
