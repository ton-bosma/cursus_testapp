package nl.uampyyg.viool.instrumenttype;

import nl.uampyyg.viool.instrumenttype.dto.InstrumentTypeRow;

import java.util.List;

/**
 * Business-logic contract for instrument types.
 */
public interface IInstrumentTypeService
{
   List<InstrumentTypeRow> findAll();

   /**
    * Persists the full list in one transaction using a three-pass strategy:
    * <ol>
    *   <li>Delete rows that have an id and are marked deleted.</li>
    *   <li>Update rows that have an id and are not marked deleted.</li>
    *   <li>Insert rows without an id.</li>
    * </ol>
    *
    * @param rows the complete list as supplied by the client
    * @return the reloaded full list after all mutations
    */
   List<InstrumentTypeRow> saveAll(List<InstrumentTypeRow> rows);
}
