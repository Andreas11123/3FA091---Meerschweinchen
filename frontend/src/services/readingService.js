import apiClient from './api'

export default {
  /**
   * Holt Ablesungen mit optionalen Filtern
   * @param {string} customerId - Optional: Kunden-ID (UUID)
   * @param {string} startDate - Optional: Startdatum (YYYY-MM-DD)
   * @param {string} endDate - Optional: Enddatum (YYYY-MM-DD)
   * @param {string} kindOfMeter - Optional: Zählerart (HEIZUNG, STROM, WASSER, UNBEKANNT)
   */
  getReadings(customerId, startDate, endDate, kindOfMeter) {
    const params = {}

    if (customerId) params.customer = customerId
    if (startDate) params.start = startDate
    if (endDate) params.end = endDate
    if (kindOfMeter) params.kindOfMeter = kindOfMeter

    return apiClient.get('/readings', { params })
  },

  /**
   * Holt eine Ablesung nach ID
   * @param {string} id - Ablesungs-ID (UUID)
   */
  getReadingById(id) {
    return apiClient.get(`/readings/${id}`)
  },

  /**
   * Erstellt eine neue Ablesung
   * @param {Object} reading - Ablesungsdaten
   */
  createReading(reading) {
    // Gemäß der JSON-Schema-Vorgabe aus deinem Projekt
    const requestData = {
      reading: {
        dateOfReading: reading.dateOfReading,
        meterId: reading.meterId,
        substitute: reading.substitute || false,
        meterCount: reading.meterCount,
        kindOfMeter: reading.kindOfMeter,
        comment: reading.comment || null
      }
    }

    // Kunden-Daten hinzufügen, falls vorhanden
    if (reading.customer) {
      requestData.reading.customer = {
        id: reading.customer.id,
        firstName: reading.customer.firstName,
        lastName: reading.customer.lastName,
        birthDate: reading.customer.birthDate,
        gender: reading.customer.gender
      }
    }

    // ID hinzufügen, falls vorhanden
    if (reading.id) {
      requestData.reading.id = reading.id
    }

    return apiClient.post('/readings', requestData)
  },

  /**
   * Aktualisiert eine Ablesung
   * @param {Object} reading - Aktualisierte Ablesungsdaten mit ID
   */
  updateReading(reading) {
    // Gemäß der JSON-Schema-Vorgabe
    const requestData = {
      reading: {
        id: reading.id,
        dateOfReading: reading.dateOfReading,
        meterId: reading.meterId,
        substitute: reading.substitute || false,
        meterCount: reading.meterCount,
        kindOfMeter: reading.kindOfMeter,
        comment: reading.comment || null
      }
    }

    // Kunden-Daten hinzufügen, falls vorhanden
    if (reading.customer) {
      requestData.reading.customer = {
        id: reading.customer.id,
        firstName: reading.customer.firstName,
        lastName: reading.customer.lastName,
        birthDate: reading.customer.birthDate,
        gender: reading.customer.gender
      }
    }

    return apiClient.put('/readings', requestData)
  },

  /**
   * Löscht eine Ablesung
   * @param {string} id - Ablesungs-ID (UUID)
   */
  deleteReading(id) {
    return apiClient.delete(`/readings/${id}`)
  }
}