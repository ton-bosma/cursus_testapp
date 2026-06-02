package nl.uampyyg.viool;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.jooq.impl.DSL.count;
import static nl.uampyyg.viool.jooq.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that verifies the three baseline tables exist in the
 * Testcontainers PostgreSQL database after Flyway migrations have run.
 */
class BaselineSchemaIT extends AbstractIntegrationTest
{
   @Autowired
   private DSLContext dsl;

   @Test
   void instrTypeTableExists()
   {
      int rows = dsl.select(count()).from(INSTR_TYPE).fetchOne(count());
      assertThat(rows).isGreaterThanOrEqualTo(0);
   }

   @Test
   void inkoopbronTableExists()
   {
      int rows = dsl.select(count()).from(INKOOPBRON).fetchOne(count());
      assertThat(rows).isGreaterThanOrEqualTo(0);
   }

   @Test
   void instrumentTableExists()
   {
      int rows = dsl.select(count()).from(INSTRUMENT).fetchOne(count());
      assertThat(rows).isGreaterThanOrEqualTo(0);
   }
}
