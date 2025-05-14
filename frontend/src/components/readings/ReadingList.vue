<template>
  <div class="reading-list">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="card-title mb-0">Ablesungsverwaltung</h5>
        <button class="btn btn-primary" @click="showAddReadingModal">
          <i class="bi bi-plus-circle-fill me-1"></i> Neue Ablesung
        </button>
      </div>

      <div class="card-body">
        <!-- Filter -->
        <div class="row mb-3">
          <div class="col-md-3 mb-2">
            <label for="customerFilter" class="form-label">Kunde</label>
            <select class="form-select" id="customerFilter" v-model="filters.customerId" @change="applyFilters">
              <option :value="null">Alle Kunden</option>
              <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                {{ customer.firstname }} {{ customer.lastname }}
              </option>
            </select>
          </div>

          <div class="col-md-3 mb-2">
            <label for="meterTypeFilter" class="form-label">Zählerart</label>
            <select class="form-select" id="meterTypeFilter" v-model="filters.kindOfMeter" @change="applyFilters">
              <option :value="null">Alle Zählerarten</option>
              <option value="HEIZUNG">Heizung</option>
              <option value="STROM">Strom</option>
              <option value="WASSER">Wasser</option>
              <option value="UNBEKANNT">Unbekannt</option>
            </select>
          </div>

          <div class="col-md-3 mb-2">
            <label for="startDateFilter" class="form-label">Von</label>
            <input type="date" class="form-control" id="startDateFilter" v-model="filters.startDate" @change="applyFilters">
          </div>

          <div class="col-md-3 mb-2">
            <label for="endDateFilter" class="form-label">Bis</label>
            <input type="date" class="form-control" id="endDateFilter" v-model="filters.endDate" @change="applyFilters">
          </div>
        </div>

        <div class="mb-3 text-end">
          <button class="btn btn-outline-secondary" @click="resetFilters">
            <i class="bi bi-funnel me-1"></i> Filter zurücksetzen
          </button>
        </div>

        <!-- Tabelle -->
        <div class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th @click="sortBy('customer')">Kunde
                  <i v-if="sortKey === 'customer'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('dateOfReading')">Datum
                  <i v-if="sortKey === 'dateOfReading'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('kindOfMeter')">Zählerart
                  <i v-if="sortKey === 'kindOfMeter'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('meterId')">Zähler-ID
                  <i v-if="sortKey === 'meterId'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('meterCount')">Zählerstand
                  <i v-if="sortKey === 'meterCount'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th>Ersatzwert</th>
                <th>Aktionen</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="7" class="text-center">
                  <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Wird geladen...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="sortedReadings.length === 0">
                <td colspan="7" class="text-center">Keine Ablesungen gefunden</td>
              </tr>
              <tr v-for="reading in sortedReadings" :key="reading.id">
                <td>{{ formatCustomer(reading.customer) }}</td>
                <td>{{ formatDate(reading.dateOfReading) }}</td>
                <td>{{ formatMeterType(reading.kindOfMeter) }}</td>
                <td>{{ reading.meterId }}</td>
                <td>{{ formatMeterCount(reading.meterCount) }}</td>
                <td>
                  <span :class="reading.substitute ? 'badge bg-warning' : 'badge bg-success'">
                    {{ reading.substitute ? 'Ja' : 'Nein' }}
                  </span>
                </td>
                <td>
                  <div class="btn-group">
                    <button class="btn btn-sm btn-warning" @click="showEditReadingModal(reading)">
                      <i class="bi bi-pencil-fill"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" @click="showDeleteReadingModal(reading)">
                      <i class="bi bi-trash-fill"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal für Hinzufügen/Bearbeiten -->
    <div class="modal fade" ref="readingFormModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? 'Ablesung bearbeiten' : 'Neue Ablesung' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveReading">
              <div class="mb-3">
                <label for="customer" class="form-label">Kunde</label>
                <select
                  class="form-select"
                  id="customer"
                  v-model="currentReading.customer"
                  required
                >
                  <option value="" disabled>Bitte wählen</option>
                  <option v-for="customer in customers" :key="customer.id" :value="customer">
                    {{ customer.firstname }} {{ customer.lastname }}
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label for="dateOfReading" class="form-label">Ablesedatum</label>
                <input
                  type="date"
                  class="form-control"
                  id="dateOfReading"
                  v-model="currentReading.dateOfReading"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="kindOfMeter" class="form-label">Zählerart</label>
                <select
                  class="form-select"
                  id="kindOfMeter"
                  v-model="currentReading.kindOfMeter"
                  required
                >
                  <option value="" disabled>Bitte wählen</option>
                  <option value="HEIZUNG">Heizung</option>
                  <option value="STROM">Strom</option>
                  <option value="WASSER">Wasser</option>
                  <option value="UNBEKANNT">Unbekannt</option>
                </select>
              </div>

              <div class="mb-3">
                <label for="meterId" class="form-label">Zähler-ID</label>
                <input
                  type="text"
                  class="form-control"
                  id="meterId"
                  v-model.trim="currentReading.meterId"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="meterCount" class="form-label">Zählerstand</label>
                <input
                  type="number"
                  step="0.01"
                  class="form-control"
                  id="meterCount"
                  v-model.number="currentReading.meterCount"
                  required
                />
              </div>

              <div class="mb-3 form-check">
                <input
                  type="checkbox"
                  class="form-check-input"
                  id="substitute"
                  v-model="currentReading.substitute"
                />
                <label class="form-check-label" for="substitute">Ersatzwert</label>
              </div>

              <div class="mb-3">
                <label for="comment" class="form-label">Kommentar</label>
                <textarea
                  class="form-control"
                  id="comment"
                  v-model.trim="currentReading.comment"
                  rows="3"
                ></textarea>
              </div>

              <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Abbrechen</button>
                <button type="submit" class="btn btn-primary">Speichern</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal für Löschen -->
    <div class="modal fade" ref="deleteModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Ablesung löschen</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>Möchten Sie die Ablesung wirklich löschen?</p>
            <div>
              <strong>Kunde:</strong> {{ formatCustomer(currentReading.customer) }}<br>
              <strong>Datum:</strong> {{ formatDate(currentReading.dateOfReading) }}<br>
              <strong>Zählerart:</strong> {{ formatMeterType(currentReading.kindOfMeter) }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Abbrechen</button>
            <button type="button" class="btn btn-danger" @click="deleteReading">Löschen</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive } from 'vue'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap'
import { formatDateArray, formatDateForDisplay } from '@/utils/dateFormatter'

export default {
  name: 'ReadingList',
  setup() {
    // Store & Reactive Variablen
    const store = useStore()
    const currentReading = ref({})
    const isEditing = ref(false)
    const readingFormModal = ref(null)
    const deleteModal = ref(null)
    const sortKey = ref('dateOfReading')
    const sortOrder = ref('desc')

    // Filter
    const filters = reactive({
      customerId: null,
      startDate: null,
      endDate: null,
      kindOfMeter: null
    })

    // Computed Properties
    const readings = computed(() => store.getters['readings/getReadings'])
    const customers = computed(() => store.getters['customers/getCustomers'])
    const loading = computed(() => store.getters.isLoading)

    const sortedReadings = computed(() => {
      let result = [...readings.value]

      // Sortierung
      result.sort((a, b) => {
        let valueA, valueB;

        // Spezielle Behandlung für customer
        if (sortKey.value === 'customer') {
          valueA = a.customer ? `${a.customer.lastname} ${a.customer.firstname}` : ''
          valueB = b.customer ? `${b.customer.lastname} ${b.customer.firstname}` : ''
        } else {
          valueA = a[sortKey.value]
          valueB = b[sortKey.value]
        }

        // Behandle null-Werte
        if (valueA === null) valueA = ''
        if (valueB === null) valueB = ''

        // Konvertiere Daten für Vergleich
        if (sortKey.value === 'dateOfReading' && valueA && valueB) {
          if (Array.isArray(valueA) && Array.isArray(valueB)) {
            // Geburtsdatum als Array [Jahr, Monat, Tag]
            valueA = new Date(valueA[0], valueA[1]-1, valueA[2])
            valueB = new Date(valueB[0], valueB[1]-1, valueB[2])
          } else {
            valueA = new Date(valueA)
            valueB = new Date(valueB)
          }
        } else if (typeof valueA === 'string') {
          valueA = valueA.toLowerCase()
          valueB = valueB.toLowerCase()
        }

        if (valueA < valueB) return sortOrder.value === 'asc' ? -1 : 1
        if (valueA > valueB) return sortOrder.value === 'asc' ? 1 : -1
        return 0
      })

      return result
    })

    // Methoden
    const loadData = async () => {
      try {
        // Lade Kunden, falls noch nicht geladen
        if (customers.value.length === 0) {
          await store.dispatch('customers/fetchCustomers')
        }
        // Lade Ablesungen mit aktuellen Filtern
        await store.dispatch('readings/fetchReadings')
      } catch (error) {
        console.error('Fehler beim Laden der Daten:', error)
        alert('Daten konnten nicht geladen werden.')
      }
    }

    const showAddReadingModal = () => {
      currentReading.value = {
        customer: null,
        dateOfReading: new Date().toISOString().split('T')[0],
        kindOfMeter: '',
        meterId: '',
        meterCount: null,
        substitute: false,
        comment: ''
      }
      isEditing.value = false

      const modal = new Modal(readingFormModal.value)
      modal.show()
    }

    const showEditReadingModal = (reading) => {
      currentReading.value = { ...reading }

      // Konvertiere Datum-Array in ISO-String für Datums-Input
      if (Array.isArray(currentReading.value.dateOfReading)) {
        const [year, month, day] = currentReading.value.dateOfReading
        currentReading.value.dateOfReading = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      }

      isEditing.value = true

      const modal = new Modal(readingFormModal.value)
      modal.show()
    }

    const showDeleteReadingModal = (reading) => {
      currentReading.value = { ...reading }

      const modal = new Modal(deleteModal.value)
      modal.show()
    }

    const saveReading = async () => {
      try {
        // Hier würde man normalerweise das Datum in ein Array umwandeln,
        // aber dafür sorgt der readingService

        if (isEditing.value) {
          await store.dispatch('readings/updateReading', currentReading.value)
          alert('Ablesung wurde erfolgreich aktualisiert!')
        } else {
          await store.dispatch('readings/createReading', currentReading.value)
          alert('Ablesung wurde erfolgreich erstellt!')
        }

        const modal = Modal.getInstance(readingFormModal.value)
        if (modal) modal.hide()
      } catch (error) {
        console.error('Fehler beim Speichern der Ablesung:', error)
        alert('Ablesung konnte nicht gespeichert werden.')
      }
    }

    const deleteReading = async () => {
      try {
        await store.dispatch('readings/deleteReading', currentReading.value.id)

        const modal = Modal.getInstance(deleteModal.value)
        if (modal) modal.hide()

        alert('Ablesung wurde erfolgreich gelöscht!')
      } catch (error) {
        console.error('Fehler beim Löschen der Ablesung:', error)
        alert('Ablesung konnte nicht gelöscht werden.')
      }
    }

    const sortBy = (key) => {
      if (sortKey.value === key) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
      } else {
        sortKey.value = key
        sortOrder.value = 'asc'
      }
    }

    const formatDate = (date) => {
      return formatDateForDisplay(date);
    }

    const formatCustomer = (customer) => {
      if (!customer) return '—'
      return `${customer.firstname} ${customer.lastname}`
    }

    const formatMeterType = (meterType) => {
      switch (meterType) {
        case 'HEIZUNG': return 'Heizung'
        case 'STROM': return 'Strom'
        case 'WASSER': return 'Wasser'
        case 'UNBEKANNT': return 'Unbekannt'
        default: return meterType
      }
    }

    const formatMeterCount = (count) => {
      if (count === null || count === undefined) return '—'
      return count.toFixed(2).replace('.', ',')
    }

    const applyFilters = async () => {
      try {
        // Formatiere Datumswerte für API, falls vorhanden
        const formattedFilters = {
          customerId: filters.customerId,
          kindOfMeter: filters.kindOfMeter,
          startDate: filters.startDate,
          endDate: filters.endDate
        }

        await store.dispatch('readings/setFilters', formattedFilters)
      } catch (error) {
        console.error('Fehler beim Anwenden der Filter:', error)
        alert('Filter konnten nicht angewendet werden.')
      }
    }

    const resetFilters = async () => {
      filters.customerId = null
      filters.startDate = null
      filters.endDate = null
      filters.kindOfMeter = null

      try {
        await store.dispatch('readings/resetFilters')
      } catch (error) {
        console.error('Fehler beim Zurücksetzen der Filter:', error)
        alert('Filter konnten nicht zurückgesetzt werden.')
      }
    }

    // Beim Laden der Komponente
    onMounted(() => {
      loadData()
    })

    return {
      readings,
      customers,
      loading,
      currentReading,
      isEditing,
      readingFormModal,
      deleteModal,
      sortKey,
      sortOrder,
      filters,
      sortedReadings,
      loadData,
      showAddReadingModal,
      showEditReadingModal,
      showDeleteReadingModal,
      saveReading,
      deleteReading,
      sortBy,
      formatDate,
      formatCustomer,
      formatMeterType,
      formatMeterCount,
      applyFilters,
      resetFilters
    }
  }
}
</script>

<style scoped>
.table th {
  cursor: pointer;
}
</style>