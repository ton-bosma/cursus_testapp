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
  instrument: {
    title: 'Instrumenten',
    zoekLabel: 'Zoeken',
    zoekPlaceholder: 'Zoek op huurnr, aanschafnr, type...',
    toonArchief: 'Toon archief',
    maxResultaten: 'Max. resultaten',
    nieuw: 'Nieuw instrument',
    geenResultaten: 'Geen instrumenten gevonden.',
    kolommen: {
      huurnr: 'Huurnr',
      aanschafnr: 'Aanschafnr',
      type: 'Type',
      maat: 'Maat',
      inkoopAdres: 'Inkoopadres',
      verkoopAdres: 'Verkoopadres',
      aanschafdatum: 'Aanschafdatum',
      verkoopdatum: 'Verkoopdatum',
    },
  },
  instrumenttype: {
    title: 'Instrument types',
    colOmschrijving: 'Omschrijving',
    colForfaitAccessoires: 'Forfait accessoires',
    btnRijToevoegen: 'Rij toevoegen',
    btnOpslaan: 'Opslaan',
    btnVerwijderen: 'Verwijderen',
    opgeslagen: 'Types opgeslagen.',
    foutOpslaan: 'Fout bij opslaan van types.',
  },
  inkoopbron: {
    title: 'Inkoopbronnen',
    colOmschrijving: 'Omschrijving',
    colRapporteren: 'Rapporteren',
    btnRijToevoegen: 'Rij toevoegen',
    btnOpslaan: 'Opslaan',
    btnVerwijderen: 'Verwijderen',
    opgeslagen: 'Inkoopbronnen opgeslagen.',
    foutOpslaan: 'Fout bij opslaan van inkoopbronnen.',
  },
} as const;

export type NlLabels = typeof NL;
