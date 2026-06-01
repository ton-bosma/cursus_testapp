package nl.uampyyg.viool.instrumenttype;

import nl.uampyyg.viool.instrumenttype.dto.InstrumentTypeRow;
import nl.uampyyg.viool.jooq.tables.InstrType;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static nl.uampyyg.viool.jooq.tables.InstrType.INSTR_TYPE;

/**
 * jOOQ-based repository for the INSTR_TYPE table.
 */
@Repository
public class InstrumentTypeRepository implements IInstrumentTypeRepository
{
   private static final Logger LOG = LoggerFactory.getLogger(InstrumentTypeRepository.class);

   private final DSLContext dsl;

   public InstrumentTypeRepository(DSLContext dsl)
   {
      this.dsl = dsl;
   }

   @Override
   public List<InstrumentTypeRow> findAll()
   {
      LOG.debug("Fetching all instrument types");
      return dsl
            .select(INSTR_TYPE.ID_INSTR_TYPE, INSTR_TYPE.OMSCHRIJVING, INSTR_TYPE.FORFAIT_ACC)
            .from(INSTR_TYPE)
            .orderBy(INSTR_TYPE.OMSCHRIJVING)
            .fetch(r -> new InstrumentTypeRow(
                  r.get(INSTR_TYPE.ID_INSTR_TYPE),
                  r.get(INSTR_TYPE.OMSCHRIJVING),
                  r.get(INSTR_TYPE.FORFAIT_ACC)));
   }

   @Override
   public void deleteById(long id)
   {
      LOG.debug("Deleting instrument type id={}", id);
      dsl.deleteFrom(INSTR_TYPE)
            .where(INSTR_TYPE.ID_INSTR_TYPE.eq(id))
            .execute();
   }

   @Override
   public void update(InstrumentTypeRow row)
   {
      LOG.debug("Updating instrument type id={}", row.getId());
      dsl.update(INSTR_TYPE)
            .set(INSTR_TYPE.OMSCHRIJVING, row.getOmschrijving())
            .set(INSTR_TYPE.FORFAIT_ACC, row.getForfaitAccessoires())
            .where(INSTR_TYPE.ID_INSTR_TYPE.eq(row.getId()))
            .execute();
   }

   @Override
   public void insert(InstrumentTypeRow row)
   {
      LOG.debug("Inserting new instrument type omschrijving={}", row.getOmschrijving());
      dsl.insertInto(INSTR_TYPE)
            .set(INSTR_TYPE.OMSCHRIJVING, row.getOmschrijving())
            .set(INSTR_TYPE.FORFAIT_ACC, row.getForfaitAccessoires())
            .execute();
   }
}
