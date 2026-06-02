package nl.uampyyg.viool.inkoopbron;

import nl.uampyyg.viool.inkoopbron.dto.InkoopbronRow;

import java.util.List;

/**
 * Data-access contract for INKOOPBRON.
 */
public interface IInkoopbronRepository
{
   List<InkoopbronRow> findAll();

   void deleteById(long id);

   void update(InkoopbronRow row);

   void insert(InkoopbronRow row);
}
