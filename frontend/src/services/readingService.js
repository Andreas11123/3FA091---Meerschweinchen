// src/services/readingService.js
import apiClient from './api'

export default {
  // Hole alle Ablesungen mit optionalen Filtern
  getReadings(customerId, startDate, endDate, kindOfMeter) {
    const params = {}

    if (customerId) params.customer = customerId
    if (startDate) params.start = startDate
    if (endDate) params.end = endDate
    if (kindOfMeter) params.kindOfMeter = kindOfMeter

    return apiClient.get('/readings', { params })
  },

  // Hole eine Ablesung anhand der ID
  getReadingById(id) {
    return apiClient.get(`/readings/${id}`)
  },

  // Erstelle eine neue Ablesung
  createReading(reading) {
    // Direktes Reading-Objekt erstellen (ohne "reading" wrapper)
    const requestData = {
      dateOfReading: reading.dateOfReading,
      meterId: reading.meterId,
      substitute: reading.substitute || false,
      meterCount: reading.meterCount,
      kindOfMeter: reading.kindOfMeter,
      comment: reading.comment || null
    }

    // Kunden-Daten hinzufügen, falls vorhanden
    if (reading.customer) {
      requestData.customer = {
        id: reading.customer.id,
        firstname: reading.customer.firstName || reading.customer.firstname,
        lastname: reading.customer.lastName || reading.customer.lastname,
        birthdate: reading.customer.birthDate || reading.customer.birthdate,
        gender: reading.customer.gender
      }
    }

    // ID hinzufügen, falls vorhanden
    if (reading.id) {
      requestData.id = reading.id
    }

    console.log('Sending reading data:', JSON.stringify(requestData, null, 2));
    return apiClient.post('/readings', requestData)
  },

  // Aktualisiere eine Ablesung
  updateReading(reading) {
    // Direktes Reading-Objekt erstellen (ohne "reading" wrapper)
    const requestData = {
      id: reading.id,
      dateOfReading: reading.dateOfReading,
      meterId: reading.meterId,
      substitute: reading.substitute || false,
      meterCount: reading.meterCount,
      kindOfMeter: reading.kindOfMeter,
      comment: reading.comment || null
    }

    // Kunden-Daten hinzufügen, falls vorhanden
    if (reading.customer) {
      requestData.customer = {
        id: reading.customer.id,
        firstname: reading.customer.firstName || reading.customer.firstname,
        lastname: reading.customer.lastName || reading.customer.lastname,
        birthdate: reading.customer.birthDate || reading.customer.birthdate,
        gender: reading.customer.gender
      }
    }

    console.log('Updating reading:', JSON.stringify(requestData, null, 2));
    return apiClient.put('/readings', requestData)
  },

  // Lösche eine Ablesung
  deleteReading(id) {
    return apiClient.delete(`/readings/${id}`)
  }
}