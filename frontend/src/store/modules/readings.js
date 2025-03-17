import readingService from '@/services/readingService'

const state = {
  readings: [],
  selectedReading: null,
  filters: {
    customerId: null,
    startDate: null,
    endDate: null,
    kindOfMeter: null
  }
}

const getters = {
  getReadings: state => state.readings,
  getSelectedReading: state => state.selectedReading,
  getFilters: state => state.filters,
  getReadingById: state => id => {
    return state.readings.find(reading => reading.id === id)
  },
  getReadingsByCustomerId: state => customerId => {
    return state.readings.filter(reading =>
      reading.customer && reading.customer.id === customerId
    )
  },
  getReadingsByMeterType: state => meterType => {
    return state.readings.filter(reading =>
      reading.kindOfMeter === meterType
    )
  },
  getReadingsGroupedByYear: state => {
    const grouped = {}
    state.readings.forEach(reading => {
      const year = new Date(reading.dateOfReading).getFullYear()
      if (!grouped[year]) {
        grouped[year] = []
      }
      grouped[year].push(reading)
    })
    return grouped
  },
  getReadingsForAnalytics: state => {
    // Gruppieren nach Monat/Jahr und Zählertyp für Analysen
    const grouped = {}

    state.readings.forEach(reading => {
      const date = new Date(reading.dateOfReading)
      const year = date.getFullYear()
      const month = date.getMonth()
      const meterType = reading.kindOfMeter

      if (!grouped[year]) {
        grouped[year] = {}
      }

      if (!grouped[year][month]) {
        grouped[year][month] = {}
      }

      if (!grouped[year][month][meterType]) {
        grouped[year][month][meterType] = []
      }

      grouped[year][month][meterType].push(reading)
    })

    return grouped
  }
}

const mutations = {
  SET_READINGS(state, readings) {
    state.readings = readings
  },
  SET_SELECTED_READING(state, reading) {
    state.selectedReading = reading
  },
  SET_FILTERS(state, filters) {
    state.filters = { ...state.filters, ...filters }
  },
  RESET_FILTERS(state) {
    state.filters = {
      customerId: null,
      startDate: null,
      endDate: null,
      kindOfMeter: null
    }
  },
  ADD_READING(state, reading) {
    state.readings.push(reading)
  },
  UPDATE_READING(state, updatedReading) {
    const index = state.readings.findIndex(r => r.id === updatedReading.id)
    if (index !== -1) {
      state.readings.splice(index, 1, updatedReading)
      if (state.selectedReading && state.selectedReading.id === updatedReading.id) {
        state.selectedReading = updatedReading
      }
    }
  },
  REMOVE_READING(state, readingId) {
    state.readings = state.readings.filter(r => r.id !== readingId)
    if (state.selectedReading && state.selectedReading.id === readingId) {
      state.selectedReading = null
    }
  }
}

const actions = {
  async fetchReadings({ commit, dispatch, state }) {
    try {
      dispatch('setLoading', true, { root: true })
      const { customerId, startDate, endDate, kindOfMeter } = state.filters
      const response = await readingService.getReadings(customerId, startDate, endDate, kindOfMeter)
      commit('SET_READINGS', response.data.readings || [])
      return response.data.readings || []
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Laden der Ablesungen', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async fetchReadingById({ commit, dispatch }, readingId) {
    try {
      dispatch('setLoading', true, { root: true })
      const response = await readingService.getReadingById(readingId)
      commit('SET_SELECTED_READING', response.data.reading)
      return response.data.reading
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Laden der Ablesung', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async createReading({ commit, dispatch }, reading) {
    try {
      dispatch('setLoading', true, { root: true })
      const response = await readingService.createReading(reading)
      commit('ADD_READING', response.data.reading)
      return response.data.reading
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Erstellen der Ablesung', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async updateReading({ commit, dispatch }, reading) {
    try {
      dispatch('setLoading', true, { root: true })
      await readingService.updateReading(reading)
      commit('UPDATE_READING', reading)
      return reading
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Aktualisieren der Ablesung', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async deleteReading({ commit, dispatch }, readingId) {
    try {
      dispatch('setLoading', true, { root: true })
      await readingService.deleteReading(readingId)
      commit('REMOVE_READING', readingId)
    } catch (error) {
      dispatch('setError', error.message || 'Fehler beim Löschen der Ablesung', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  setFilters({ commit, dispatch }, filters) {
    commit('SET_FILTERS', filters)
    return dispatch('fetchReadings')
  },

  resetFilters({ commit, dispatch }) {
    commit('RESET_FILTERS')
    return dispatch('fetchReadings')
  },

  selectReading({ commit }, reading) {
    commit('SET_SELECTED_READING', reading)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}