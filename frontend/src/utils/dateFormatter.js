/**
 * Formatiert ein Datums-Array [Jahr, Monat, Tag] zu einem lesbaren String
 */
export function formatDateArray(dateArray) {
  if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 3) {
    return '';
  }

  const [year, month, day] = dateArray;
  return `${day}.${month}.${year}`;
}


/**
 * Formatiert ein Datum für die Anzeige (ISO-String oder Array)
 */
export function formatDateForDisplay(date) {
  if (!date) return '';

  if (Array.isArray(date)) {
    return formatDateArray(date);
  }

  if (typeof date === 'string') {
    return new Date(date).toLocaleDateString('de-DE');
  }

  return date.toLocaleDateString('de-DE');
}