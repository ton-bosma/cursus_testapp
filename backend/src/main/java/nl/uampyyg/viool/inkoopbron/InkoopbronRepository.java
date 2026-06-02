package nl.uampyyg.viool.inkoopbron;

import nl.uampyyg.viool.inkoopbron.dto.InkoopbronRow;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static nl.uampyyg.viool.jooq.tables.Inkoopbron.INKOOPBRON;

/**
 * jOOQ-based repository for the INKOOPBRON table.
 */
@Repository
public class InkoopbronRepository implements IInkoopbronRepository
{
   private static final Logger LOG = LoggerFactory.getLogger(InkoopbronRepository.class);

   private final DSLContext dsl;

   public InkoopbronRepository(DSLContext dsl)
   {
      this.dsl = dsl;
   }

   @Override
   public List<InkoopbronRow> findAll()
   {
      LOG.debug("Fetching all inkoopbronnen");
      return dsl
            .select(INKOOPBRON.ID_INKOOPBRON, INKOOPBRON.OMSCHRIJVING, INKOOPBRON.JN_RAPPORTEREN)
            .from(INKOOPBRON)
            .orderBy(INKOOPBRON.OMSCHRIJVING)
            .fetch(r -> new InkoopbronRow(
                  r.get(INKOOPBRON.ID_INKOOPBRON),
                  r.get(INKOOPBRON.OMSCHRIJVING),
                  Boolean.TRUE.equals(r.get(INKOOPBRON.JN_RAPPORTEREN))));
   }

   @Override
   public void deleteById(long id)
   {
      LOG.debug("Deleting inkoopbron id={}", id);
      dsl.deleteFrom(INKOOPBRON)
            .where(INKOOPBRON.ID_INKOOPBRON.eq(id))
            .execute();
   }

   @Override
   public void update(InkoopbronRow row)
   {
      LOG.debug("Updating inkoopbron id={}", row.getId());
      dsl.update(INKOOPBRON)
            .set(INKOOPBRON.OMSCHRIJVING, row.getOmschrijving())
            .set(INKOOPBRON.JN_RAPPORTEREN, row.isRapporteren())
            .where(INKOOPBRON.ID_INKOOPBRON.eq(row.getId()))
            .execute();
   }

   @Override
   public void insert(InkoopbronRow row)
   {
      LOG.debug("Inserting new inkoopbron omschrijving={}", row.getOmschrijving());
      dsl.insertInto(INKOOPBRON)
            .set(INKOOPBRON.OMSCHRIJVING, row.getOmschrijving())
            .set(INKOOPBRON.JN_RAPPORTEREN, row.isRapporteren())
            .execute();
   }
}
