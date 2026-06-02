package nl.uampyyg.viool;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that require a live PostgreSQL database.
 *
 * <p>Starts a PostgreSQL 16 container via Testcontainers (shared across all
 * subclasses in one test run) and applies all Flyway migrations before the
 * first test runs. Spring's datasource properties are overridden via
 * {@link DynamicPropertySource} so that the application context connects to
 * the container instead of a static datasource URL.
 *
 * <p>Usage:
 * <pre>{@code
 * class MyIT extends AbstractIntegrationTest {
 *     @Autowired DSLContext dsl;
 *
 *     @Test
 *     void tablesExist() {
 *         dsl.select(count()).from("instrument").fetchOne();
 *     }
 * }
 * }</pre>
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest
{
   @Container
   static final PostgreSQLContainer<?> POSTGRES =
         new PostgreSQLContainer<>("postgres:16")
               .withDatabaseName("viool_test")
               .withUsername("viool")
               .withPassword("viool");

   /**
    * Registers the container's JDBC URL and credentials as Spring datasource
    * properties so the application context uses the container.
    */
   @DynamicPropertySource
   static void registerDatasourceProperties(DynamicPropertyRegistry registry)
   {
      registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
      registry.add("spring.datasource.username", POSTGRES::getUsername);
      registry.add("spring.datasource.password", POSTGRES::getPassword);
      registry.add("spring.datasource.driver-class-name",
            () -> "org.postgresql.Driver");

      // Re-enable Flyway and point it at the container
      registry.add("spring.flyway.enabled",  () -> "true");
      registry.add("spring.flyway.url",      POSTGRES::getJdbcUrl);
      registry.add("spring.flyway.user",     POSTGRES::getUsername);
      registry.add("spring.flyway.password", POSTGRES::getPassword);
   }

   /**
    * Runs Flyway migrations once before any test in the suite, ensuring all
    * three baseline tables are created before the Spring context starts.
    *
    * <p>This is a safety net; when {@code spring.flyway.enabled=true} Spring
    * Boot also runs Flyway on context startup, but having it here guards
    * against edge-cases where the context has not yet started.
    */
   @BeforeAll
   static void applyMigrations()
   {
      Flyway.configure()
            .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
            .locations("classpath:db/migration")
            .load()
            .migrate();
   }
}
