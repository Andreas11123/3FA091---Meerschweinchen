// src/services/customerService.js
import apiClient from './api'

export default {
  // Hole alle Kunden
  getCustomers() {
    return apiClient.get('/customers')
  },

  // Hole einen Kunden anhand der ID
  getCustomerById(id) {
    return apiClient.get(`/customers/${id}`)
  },

  // Erstelle einen neuen Kunden
  createCustomer(customer) {
    // Direktes Customer-Objekt senden, NICHT im customer-Schlüssel verpacken
    const requestData = {
      firstname: customer.firstName || customer.firstname,
      lastname: customer.lastName || customer.lastname,
      birthdate: customer.birthDate || customer.birthdate,
      gender: customer.gender,
      id: customer.id
    }

    console.log('Sending customer data:', requestData);
    return apiClient.post('/customers', requestData)
  },

  // Aktualisiere einen Kunden
  updateCustomer(customer) {
    // Direktes Customer-Objekt senden
    const requestData = {
      id: customer.id,
      firstname: customer.firstName || customer.firstname,
      lastname: customer.lastName || customer.lastname,
      birthdate: customer.birthDate || customer.birthdate,
      gender: customer.gender
    }

    console.log('Updating customer:', requestData);
    return apiClient.put('/customers', requestData)
  },


  // Lösche einen Kunden
  deleteCustomer(id) {
    return apiClient.delete(`/customers/${id}`)
  }
}