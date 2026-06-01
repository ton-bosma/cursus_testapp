/**
 * Dutch UI labels — single source of truth for all user-facing strings.
 * Import from this file instead of hard-coding strings in components.
 */
export const NL = {
  app: {
    title: 'Cursus Plan',
  },
  nav: {
    instrumenten: 'Instrumenten',
    types: 'Types',
    inkoopbron: 'Inkoopbron',
  },
  errors: {
    httpGeneric: 'Er is een fout opgetreden bij het ophalen van gegevens.',
    httpNotFound: 'De gevraagde resource is niet gevonden.',
    httpUnauthorized: 'U bent niet geautoriseerd voor deze actie.',
    httpServerError: 'Er is een serverfout opgetreden. Probeer het later opnieuw.',
  },
} as const;

export type NlLabels = typeof NL;
