package nl.uampyyg.viool.inkoopbron;

import nl.uampyyg.viool.inkoopbron.dto.InkoopbronRow;

import java.util.List;

/**
 * Business-logic contract for inkoopbronnen.
 */
public interface IInkoopbronService
{
   List<InkoopbronRow> findAll();

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
   List<InkoopbronRow> saveAll(List<InkoopbronRow> rows);
}
