package nl.uampyyg.viool.instrument;

import java.util.Optional;

import nl.uampyyg.viool.instrument.dto.InstrumentRow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Application service for instrument CRUD.
 *
 * <p>Encapsulates the insert-versus-update decision: a {@link InstrumentRow}
 * without an id is inserted, one with an id is updated. After a successful
 * save the freshly persisted row is re-read and returned so callers always
 * observe database-resolved values (notably the generated id).
 */
@Service
public class InstrumentService
{
   private static final Logger LOG = LoggerFactory.getLogger(InstrumentService.class);

   private final InstrumentRepository repository;


   public InstrumentService(InstrumentRepository repository)
   {
      this.repository = repository;
   }


   /**
    * Reads a single instrument by id.
    *
    * @param id the instrument id
    * @return the row, or {@link Optional#empty()} when it does not exist
    */
   @Transactional(readOnly = true)
   public Optional<InstrumentRow> findById(Long id)
   {
      return repository.findById(id);
   }


   /**
    * Persists an instrument. When {@code row.getId()} is {@code null} the row is
    * inserted and the generated id is used to re-read it; otherwise the existing
    * row is updated in full.
    *
    * @param row the instrument to save
    * @return the persisted row as read back from the database
    * @throws InstrumentNotFoundException when updating an id that does not exist
    */
   @Transactional
   public InstrumentRow save(InstrumentRow row)
   {
      Long id;
      if (row.getId() == null)
      {
         id = repository.insert(row);
         LOG.debug("Inserted instrument with generated id {}", id);
      }
      else
      {
         int updated = repository.update(row);
         if (updated == 0)
         {
            throw new InstrumentNotFoundException(row.getId());
         }
         id = row.getId();
         LOG.debug("Updated instrument with id {}", id);
      }

      return repository.findById(id)
            .orElseThrow(() -> new InstrumentNotFoundException(id));
   }


   /**
    * Deletes an instrument by id.
    *
    * @param id the instrument id
    * @throws InstrumentNotFoundException when the id does not exist
    */
   @Transactional
   public void delete(Long id)
   {
      int deleted = repository.deleteById(id);
      if (deleted == 0)
      {
         throw new InstrumentNotFoundException(id);
      }
      LOG.debug("Deleted instrument with id {}", id);
   }
}
