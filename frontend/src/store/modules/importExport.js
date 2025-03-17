import importExportService from '@/services/importExportService'
import customerService from '@/services/customerService'
import readingService from '@/services/readingService'

const state = {
  importStatus: null,
  exportStatus: null,
  importErrors: [],
  lastImportedData: null,
  lastExportedData: null,
  exportFormat: 'json'
}

const getters = {
  getImportStatus: state => state.importStatus,
  getExportStatus: state => state.exportStatus,
  getImportErrors: state => state.importErrors,
  getLastImportedData: state => state.lastImportedData,
  getLastExportedData: state => state.lastExportedData,
  getExportFormat: state => state.exportFormat
}

const mutations = {
  SET_IMPORT_STATUS(state, status) {
    state.importStatus = status
  },
  SET_EXPORT_STATUS(state, status) {
    state.exportStatus = status
  },
  SET_IMPORT_ERRORS(state, errors) {
    state.importErrors = errors
  },
  SET_LAST_IMPORTED_DATA(state, data) {
    state.lastImportedData = data
  },
  SET_LAST_EXPORTED_DATA(state, data) {
    state.lastExportedData = data
  },
  SET_EXPORT_FORMAT(state, format) {
    state.exportFormat = format
  },
  CLEAR_IMPORT_STATUS(state) {
    state.importStatus = null
    state.importErrors = []
  },
  CLEAR_EXPORT_STATUS(state) {
    state.exportStatus = null
  }
}

const actions = {
  setExportFormat({ commit }, format) {
    commit('SET_EXPORT_FORMAT', format)
  },

  clearImportStatus({ commit }) {
    commit('CLEAR_IMPORT_STATUS')
  },

  clearExportStatus({ commit }) {
    commit('CLEAR_EXPORT_STATUS')
  },

  async importCustomers({ commit, dispatch }, { file, format }) {
    try {
      dispatch('setLoading', true, { root: true })
      commit('SET_IMPORT_STATUS', 'importing')
      commit('SET_IMPORT_ERRORS', [])

      const importedData = await importExportService.importData(file, format, 'customers')
      const successfulImports = []
      const errors = []

      // Verarbeite jeden importierten Kunden
      for (const customer of importedData) {
        try {
          await customerService.createCustomer(customer)
          successfulImports.push(customer)
        } catch (error) {
          errors.push({
            data: customer,
            error: error.message || 'Fehler beim Importieren des Kunden'
          })
        }
      }

      commit('SET_LAST_IMPORTED_DATA', {
        successful: successfulImports,
        failed: errors.length
      })

      if (errors.length > 0) {
        commit('SET_IMPORT_ERRORS', errors)
        commit('SET_IMPORT_STATUS', 'partial')
      } else {
        commit('SET_IMPORT_STATUS', 'success')
      }

      // Lade die Kundenliste neu
      dispatch('customers/fetchCustomers', null, { root: true })

      return {
        successful: successfulImports.length,
        failed: errors.length,
        total: importedData.length
      }
    } catch (error) {
      commit('SET_IMPORT_STATUS', 'error')
      commit('SET_IMPORT_ERRORS', [{ error: error.message || 'Fehler beim Importieren der Daten' }])
      dispatch('setError', error.message || 'Fehler beim Importieren der Daten', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async importReadings({ commit, dispatch }, { file, format }) {
    try {
      dispatch('setLoading', true, { root: true })
      commit('SET_IMPORT_STATUS', 'importing')
      commit('SET_IMPORT_ERRORS', [])

      const importedData = await importExportService.importData(file, format, 'readings')
      const successfulImports = []
      const errors = []

      // Verarbeite jede importierte Ablesung
      for (const reading of importedData) {
        try {
          await readingService.createReading(reading)
          successfulImports.push(reading)
        } catch (error) {
          errors.push({
            data: reading,
            error: error.message || 'Fehler beim Importieren der Ablesung'
          })
        }
      }

      commit('SET_LAST_IMPORTED_DATA', {
        successful: successfulImports,
        failed: errors.length
      })

      if (errors.length > 0) {
        commit('SET_IMPORT_ERRORS', errors)
        commit('SET_IMPORT_STATUS', 'partial')
      } else {
        commit('SET_IMPORT_STATUS', 'success')
      }

      // Lade die Ablesungsliste neu
      dispatch('readings/fetchReadings', null, { root: true })

      return {
        successful: successfulImports.length,
        failed: errors.length,
        total: importedData.length
      }
    } catch (error) {
      commit('SET_IMPORT_STATUS', 'error')
      commit('SET_IMPORT_ERRORS', [{ error: error.message || 'Fehler beim Importieren der Daten' }])
      dispatch('setError', error.message || 'Fehler beim Importieren der Daten', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async exportCustomers({ commit, dispatch, state, rootGetters }) {
    try {
      dispatch('setLoading', true, { root: true })
      commit('SET_EXPORT_STATUS', 'exporting')

      const customers = rootGetters['customers/getCustomers']
      const format = state.exportFormat

      const exportedData = await importExportService.exportData(customers, format, 'customers')
      commit('SET_LAST_EXPORTED_DATA', exportedData)
      commit('SET_EXPORT_STATUS', 'success')

      return exportedData
    } catch (error) {
      commit('SET_EXPORT_STATUS', 'error')
      dispatch('setError', error.message || 'Fehler beim Exportieren der Daten', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  },

  async exportReadings({ commit, dispatch, state, rootGetters }) {
    try {
      dispatch('setLoading', true, { root: true })
      commit('SET_EXPORT_STATUS', 'exporting')

      const readings = rootGetters['readings/getReadings']
      const format = state.exportFormat

      const exportedData = await importExportService.exportData(readings, format, 'readings')
      commit('SET_LAST_EXPORTED_DATA', exportedData)
      commit('SET_EXPORT_STATUS', 'success')

      return exportedData
    } catch (error) {
      commit('SET_EXPORT_STATUS', 'error')
      dispatch('setError', error.message || 'Fehler beim Exportieren der Daten', { root: true })
      throw error
    } finally {
      dispatch('setLoading', false, { root: true })
    }
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}