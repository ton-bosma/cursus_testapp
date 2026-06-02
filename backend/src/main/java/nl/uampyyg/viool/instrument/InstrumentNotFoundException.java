package nl.uampyyg.viool.instrument;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Thrown when an instrument is requested, updated or deleted by an id that does
 * not exist. Annotated with {@link ResponseStatus} so Spring MVC translates it
 * into a clean {@code 404 Not Found} response without a dedicated
 * {@code @ControllerAdvice} (ADR-006: the exception is surfaced, never
 * swallowed).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InstrumentNotFoundException extends RuntimeException
{
   private static final long serialVersionUID = 1L;


   public InstrumentNotFoundException(Long id)
   {
      super("Instrument not found: " + id);
   }
}
