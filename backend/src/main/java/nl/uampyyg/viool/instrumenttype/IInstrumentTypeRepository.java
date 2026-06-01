package nl.uampyyg.viool.instrumenttype;

import nl.uampyyg.viool.instrumenttype.dto.InstrumentTypeRow;

import java.util.List;

/**
 * Data-access contract for INSTR_TYPE.
 */
public interface IInstrumentTypeRepository
{
   List<InstrumentTypeRow> findAll();

   void deleteById(long id);

   void update(InstrumentTypeRow row);

   void insert(InstrumentTypeRow row);
}
