import customerService from '@/services/customerService'

const state = {
  customers: [],
  selectedCustomer: null
}

const getters = {
  getCustomers: state => state.customers,
  getSelectedCustomer: state => state.selectedCustomer,
  getCustomerById: state => id => {
    return state.customers.find(customer => customer.id === id)
  }
}

const mutations = {
  SET_CUSTOMERS(state, customers) {
    state.customers = customers
  },
  SET_SELECTED_CUSTOMER(state, customer) {
    state.selectedCustomer = customer
  },
  ADD_CUSTOMER(state, customer) {
    state.customers.push(customer)
  },
  UPDATE_CUSTOMER(state, updatedCustomer) {
    const index = state.customers.findIndex(c => c.id === updatedCustomer.id)
    if (index !== -1) {
      state.customers.splice(index, 1, updatedCustomer)
      if (state.selectedCustomer && state.selectedCustomer.id === updatedCustomer.id) {
        state.selectedCustomer = updatedCustomer
      }
    }
  },
  REMOVE_CUSTOMER(state, customerId) {
    state.customers = state.customers.filter(c => c.id !== customerId)
    if (state.selectedCustomer && state.selectedCustomer.id === customerId) {
      state.selectedCustomer = null
    }
  }
}

const actions = {
  async fetchCustomers({ commit, dispatch }) {
    try {
      dispatch('setLoading', true, { root: true })
      const response = await customerService.getCustomers()
      commit('SET_CUSTOMERS', response.data.customers)
      return response.data.customers
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Laden der Kunden', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async fetchCustomerById({ commit, dispatch }, customerId) {
    try {
      dispatch('setLoading', true, { root: true })
      const response = await customerService.getCustomerById(customerId)
      commit('SET_SELECTED_CUSTOMER', response.data.customer)
      return response.data.customer
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Laden des Kunden', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async createCustomer({ commit, dispatch }, customer) {
    try {
      dispatch('setLoading', true, { root: true })
      const response = await customerService.createCustomer(customer)
      commit('ADD_CUSTOMER', response.data.customer)
      return response.data.customer
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Erstellen des Kunden', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async updateCustomer({ commit, dispatch }, customer) {
    try {
      dispatch('setLoading', true, { root: true })
      await customerService.updateCustomer(customer)
      commit('UPDATE_CUSTOMER', customer)
      return customer
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Aktualisieren des Kunden', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async deleteCustomer({ commit, dispatch }, customerId) {
    try {
      dispatch('setLoading', true, { root: true })
      await customerService.deleteCustomer(customerId)
      commit('REMOVE_CUSTOMER', customerId)
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Löschen des Kunden', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  selectCustomer({ commit }, customer) {
    commit('SET_SELECTED_CUSTOMER', customer)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}