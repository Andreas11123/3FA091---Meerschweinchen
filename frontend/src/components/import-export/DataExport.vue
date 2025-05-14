<template>
  <div class="data-export">
    <div class="card">
      <div class="card-header">
        <h5 class="card-title mb-0">Daten exportieren</h5>
      </div>

      <div class="card-body">
        <form @submit.prevent="exportData">
          <div class="mb-3">
            <label for="exportDataType" class="form-label">Datentyp</label>
            <select id="exportDataType" class="form-select" v-model="dataType" required>
              <option value="customers">Kunden</option>
              <option value="readings">Ablesungen</option>
            </select>
          </div>

          <div class="mb-3">
            <label for="exportFileFormat" class="form-label">Dateiformat</label>
            <select id="exportFileFormat" class="form-select" v-model="fileFormat" required>
              <option value="json">JSON</option>
              <option value="xml">XML</option>
              <option value="csv">CSV</option>
            </select>
          </div>

          <div v-if="dataType === 'readings'" class="mb-3">
            <div class="card bg-light">
              <div class="card-body">
                <h6 class="card-subtitle mb-2 text-muted">Filter für Ablesungen</h6>

                <div class="mb-3">
                  <label for="exportCustomer" class="form-label">Kunde</label>
                  <select id="exportCustomer" class="form-select" v-model="filters.customerId">
                    <option :value="null">Alle Kunden</option>
                    <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                      {{ customer.firstName }} {{ customer.lastName }}
                    </option>
                  </select>
                </div>

                <div class="mb-3">
                  <label for="exportMeterType" class="form-label">Zählerart</label>
                  <select id="exportMeterType" class="form-select" v-model="filters.kindOfMeter">
                    <option :value="null">Alle Zählerarten</option>
                    <option value="HEIZUNG">Heizung</option>
                    <option value="STROM">Strom</option>
                    <option value="WASSER">Wasser</option>
                    <option value="UNBEKANNT">Unbekannt</option>
                  </select>
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label for="exportStartDate" class="form-label">Von</label>
                    <input
                      type="date"
                      id="exportStartDate"
                      class="form-control"
                      v-model="filters.startDate"
                    />
                  </div>

                  <div class="col-md-6 mb-3">
                    <label for="exportEndDate" class="form-label">Bis</label>
                    <input
                      type="date"
                      id="exportEndDate"
                      class="form-control"
                      v-model="filters.endDate"
                    />
                  </div>
                </div>

                <div class="d-flex justify-content-end">
                  <button type="button" class="btn btn-outline-secondary" @click="resetFilters">
                    Filter zurücksetzen
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="exportStatus === 'success'" class="alert alert-success" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            Export erfolgreich abgeschlossen. Die Datei wurde heruntergeladen.
          </div>

          <div v-if="exportStatus === 'error'" class="alert alert-danger" role="alert">
            <i class="bi bi-x-circle-fill me-2"></i>
            Fehler beim Export. Bitte versuchen Sie es erneut.
          </div>

          <div class="d-flex justify-content-end">
            <button
              type="submit"
              class="btn btn-primary"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              <i class="bi bi-download me-1"></i> Exportieren
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'DataExport',
  setup() {
    const store = useStore()

    const dataType = ref('customers')
    const fileFormat = ref('json')

    // Filter für Ablesungen
    const filters = reactive({
      customerId: null,
      startDate: null,
      endDate: null,
      kindOfMeter: null
    })

    // Computed Properties
    const loading = computed(() => store.getters.isLoading)
    const customers = computed(() => store.getters['customers/getCustomers'])
    const exportStatus = computed(() => store.getters['importExport/getExportStatus'])

    // Methoden
    const exportData = async () => {
      try {
        // Export-Format setzen
        await store.dispatch('importExport/setExportFormat', fileFormat.value)

        // Status zurücksetzen
        store.dispatch('importExport/clearExportStatus')

        // Filter anwenden, falls Ablesungen exportiert werden
        if (dataType.value === 'readings') {
          // Formatiere Datumswerte
          const formattedFilters = {
            customerId: filters.customerId,
            kindOfMeter: filters.kindOfMeter,
            startDate: filters.startDate,
            endDate: filters.endDate
          }

          await store.dispatch('readings/setFilters', formattedFilters)
        }

        // Daten exportieren
        if (dataType.value === 'customers') {
          await store.dispatch('importExport/exportCustomers')
        } else {
          await store.dispatch('importExport/exportReadings')
        }
      } catch (error) {
        console.error('Export-Fehler:', error)
        alert('Fehler beim Exportieren der Daten.')
      }
    }

    const resetFilters = () => {
      filters.customerId = null
      filters.startDate = null
      filters.endDate = null
      filters.kindOfMeter = null
    }

    // Beim Laden der Komponente
    onMounted(async () => {
      // Lade Kunden, falls noch nicht geladen
      if (customers.value.length === 0) {
        try {
          await store.dispatch('customers/fetchCustomers')
        } catch (error) {
          console.error('Fehler beim Laden der Kunden:', error)
        }
      }
    })

    return {
      dataType,
      fileFormat,
      filters,
      loading,
      customers,
      exportStatus,
      exportData,
      resetFilters
    }
  }
}
</script>

<style scoped>
.data-export {
  max-width: 800px;
  margin: 0 auto;
}
</style>