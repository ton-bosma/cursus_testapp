package nl.uampyyg.viool.inkoopbron;

import nl.uampyyg.viool.inkoopbron.dto.InkoopbronRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for INKOOPBRON reference data.
 *
 * <p>The three-pass save runs in a single transaction: delete → update → insert.
 */
@Service
public class InkoopbronService implements IInkoopbronService
{
   private static final Logger LOG = LoggerFactory.getLogger(InkoopbronService.class);

   private final IInkoopbronRepository repository;

   public InkoopbronService(IInkoopbronRepository repository)
   {
      this.repository = repository;
   }

   @Override
   public List<InkoopbronRow> findAll()
   {
      return repository.findAll();
   }

   @Override
   @Transactional
   public List<InkoopbronRow> saveAll(List<InkoopbronRow> rows)
   {
      LOG.debug("saveAll called with {} rows", rows.size());

      // Pass 1: delete rows that carry an id and are marked deleted
      rows.stream()
            .filter(r -> r.getId() != null && r.isMarkedDeleted())
            .forEach(r ->
            {
               LOG.debug("Pass 1 – deleting id={}", r.getId());
               repository.deleteById(r.getId());
            });

      // Pass 2: update rows that carry an id and are not marked deleted
      rows.stream()
            .filter(r -> r.getId() != null && !r.isMarkedDeleted())
            .forEach(r ->
            {
               LOG.debug("Pass 2 – updating id={}", r.getId());
               repository.update(r);
            });

      // Pass 3: insert rows without an id (new rows)
      rows.stream()
            .filter(r -> r.getId() == null)
            .forEach(r ->
            {
               LOG.debug("Pass 3 – inserting omschrijving={}", r.getOmschrijving());
               repository.insert(r);
            });

      return repository.findAll();
   }
}
