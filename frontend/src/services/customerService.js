import apiClient from './api'

export default {
  /**
   * Holt alle Kunden
   */
  getCustomers() {
    return apiClient.get('/customers')
  },

  /**
   * Holt einen Kunden nach ID
   * @param {string} id - Kunden-ID (UUID)
   */
  getCustomerById(id) {
    return apiClient.get(`/customers/${id}`)
  },

  /**
   * Erstellt einen neuen Kunden
   * @param {Object} customer - Kundendaten
   */
  createCustomer(customer) {
    // Gemäß der JSON-Schema-Vorgabe aus deinem Projekt
    const requestData = {
      customer: {
        firstName: customer.firstName,
        lastName: customer.lastName,
        birthDate: customer.birthDate,
        gender: customer.gender
      }
    }

    // ID hinzufügen, falls vorhanden
    if (customer.id) {
      requestData.customer.id = customer.id
    }

    return apiClient.post('/customers', requestData)
  },

  /**
   * Aktualisiert einen Kunden
   * @param {Object} customer - Aktualisierte Kundendaten mit ID
   */
  updateCustomer(customer) {
    // Gemäß der JSON-Schema-Vorgabe für PUT /customers
    const requestData = {
      customers: [
        {
          id: customer.id,
          firstName: customer.firstName,
          lastName: customer.lastName,
          birthDate: customer.birthDate,
          gender: customer.gender
        }
      ]
    }

    return apiClient.put('/customers', requestData)
  },

  /**
   * Löscht einen Kunden
   * @param {string} id - Kunden-ID (UUID)
   */
  deleteCustomer(id) {
    return apiClient.delete(`/customers/${id}`)
  }
}