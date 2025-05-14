<template>
  <div class="customer-list">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="card-title mb-0">Kundenverwaltung</h5>
        <button class="btn btn-primary" @click="showAddCustomerModal">
          <i class="bi bi-person-plus-fill me-1"></i> Neuer Kunde
        </button>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <input
            v-model="searchTerm"
            type="text"
            class="form-control"
            placeholder="Kunde suchen..."
            @input="filterCustomers"
          />
        </div>

        <div class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th @click="sortBy('firstname')">Vorname
                  <i v-if="sortKey === 'firstname'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('lastname')">Nachname
                  <i v-if="sortKey === 'lastname'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('birthdate')">Geburtsdatum
                  <i v-if="sortKey === 'birthdate'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('gender')">Geschlecht
                  <i v-if="sortKey === 'gender'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th>Aktionen</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="5" class="text-center">
                  <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Wird geladen...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="filteredCustomers.length === 0">
                <td colspan="5" class="text-center">Keine Kunden gefunden</td>
              </tr>
              <tr v-for="customer in filteredCustomers" :key="customer.id" @click="selectCustomer(customer)">
                <td>{{ customer.firstname }}</td>
                <td>{{ customer.lastname }}</td>
                <td>{{ formatDate(customer.birthdate) }}</td>
                <td>{{ formatGender(customer.gender) }}</td>
                <td>
                  <div class="btn-group">
                    <button class="btn btn-sm btn-warning" @click.stop="showEditCustomerModal(customer)">
                      <i class="bi bi-pencil-fill"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" @click.stop="showDeleteCustomerModal(customer)">
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
    <div class="modal fade" ref="customerFormModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? 'Kunde bearbeiten' : 'Neuer Kunde' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveCustomer">
              <div class="mb-3">
                <label for="firstname" class="form-label">Vorname</label>
                <input
                  type="text"
                  class="form-control"
                  id="firstname"
                  v-model.trim="currentCustomer.firstname"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="lastname" class="form-label">Nachname</label>
                <input
                  type="text"
                  class="form-control"
                  id="lastname"
                  v-model.trim="currentCustomer.lastname"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="birthdate" class="form-label">Geburtsdatum</label>
                <input
                  type="date"
                  class="form-control"
                  id="birthdate"
                  v-model="currentCustomer.birthdate"
                />
              </div>

              <div class="mb-3">
                <label for="gender" class="form-label">Geschlecht</label>
                <select
                  class="form-select"
                  id="gender"
                  v-model="currentCustomer.gender"
                  required
                >
                  <option value="" disabled>Bitte wählen</option>
                  <option value="M">Männlich</option>
                  <option value="W">Weiblich</option>
                  <option value="D">Divers</option>
                  <option value="U">Unbekannt</option>
                </select>
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
            <h5 class="modal-title">Kunde löschen</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>Möchten Sie den Kunden {{ currentCustomer.firstname }} {{ currentCustomer.lastname }} wirklich löschen?</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Abbrechen</button>
            <button type="button" class="btn btn-danger" @click="deleteCustomer">Löschen</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap'
import { formatDateArray } from '@/utils/dateFormatter'

export default {
  name: 'CustomerList',
  setup() {
    // Store & Reactive Variablen
    const store = useStore()
    const searchTerm = ref('')
    const currentCustomer = ref({})
    const isEditing = ref(false)
    const customerFormModal = ref(null)
    const deleteModal = ref(null)
    const sortKey = ref('lastname')
    const sortOrder = ref('asc')

    // Computed Properties
    const customers = computed(() => store.getters['customers/getCustomers'])
    const loading = computed(() => store.getters.isLoading)

    const filteredCustomers = computed(() => {
      let result = [...customers.value]

      // Suche
      if (searchTerm.value) {
        const term = searchTerm.value.toLowerCase()
        result = result.filter(customer =>
          customer.firstname.toLowerCase().includes(term) ||
          customer.lastname.toLowerCase().includes(term)
        )
      }

      // Sortierung
      result.sort((a, b) => {
        let valueA = a[sortKey.value]
        let valueB = b[sortKey.value]

        // Behandle null-Werte
        if (valueA === null) valueA = ''
        if (valueB === null) valueB = ''

        // Konvertiere Daten für Vergleich
        if (sortKey.value === 'birthdate' && valueA && valueB) {
          // Falls Geburtsdatum als Array
          if (Array.isArray(valueA) && Array.isArray(valueB)) {
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
    const loadCustomers = async () => {
      try {
        await store.dispatch('customers/fetchCustomers')
      } catch (error) {
        console.error('Fehler beim Laden der Kunden:', error)
        alert('Kunden konnten nicht geladen werden.')
      }
    }

    const showAddCustomerModal = () => {
      currentCustomer.value = {
        firstname: '',
        lastname: '',
        birthdate: null,
        gender: ''
      }
      isEditing.value = false

      const modal = new Modal(customerFormModal.value)
      modal.show()
    }

    const showEditCustomerModal = (customer) => {
      currentCustomer.value = { ...customer }

      // Konvertiere Geburtsdatum-Array in ISO-String für Date-Input
      if (Array.isArray(currentCustomer.value.birthdate)) {
        const [year, month, day] = currentCustomer.value.birthdate
        currentCustomer.value.birthdate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      }

      isEditing.value = true

      const modal = new Modal(customerFormModal.value)
      modal.show()
    }

    const showDeleteCustomerModal = (customer) => {
      currentCustomer.value = { ...customer }

      const modal = new Modal(deleteModal.value)
      modal.show()
    }

    const saveCustomer = async () => {
      try {
        // Wenn Geburtsdatum als ISO-String vorliegt, behalten wir es so
        // Backend konvertiert es später in ein Array

        if (isEditing.value) {
          await store.dispatch('customers/updateCustomer', currentCustomer.value)
          alert('Kunde wurde erfolgreich aktualisiert!')
        } else {
          await store.dispatch('customers/createCustomer', currentCustomer.value)
          alert('Kunde wurde erfolgreich erstellt!')
        }

        const modal = Modal.getInstance(customerFormModal.value)
        if (modal) modal.hide()
      } catch (error) {
        console.error('Fehler beim Speichern des Kunden:', error)
        alert('Kunde konnte nicht gespeichert werden.')
      }
    }

    const deleteCustomer = async () => {
      try {
        await store.dispatch('customers/deleteCustomer', currentCustomer.value.id)

        const modal = Modal.getInstance(deleteModal.value)
        if (modal) modal.hide()

        alert('Kunde wurde erfolgreich gelöscht!')
      } catch (error) {
        console.error('Fehler beim Löschen des Kunden:', error)
        alert('Kunde konnte nicht gelöscht werden.')
      }
    }

    const selectCustomer = (customer) => {
      store.dispatch('customers/selectCustomer', customer)
    }

    const filterCustomers = () => {
      // Wird bereits durch das computed property 'filteredCustomers' erledigt
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
      if (!date) return ''

      // Falls das Datum ein Array ist [Jahr, Monat, Tag]
      if (Array.isArray(date)) {
        return formatDateArray(date)
      }

      // Falls das Datum ein String ist
      if (typeof date === 'string') {
        return new Date(date).toLocaleDateString('de-DE')
      }

      // Falls es bereits ein Date-Objekt ist
      return date.toLocaleDateString('de-DE')
    }

    const formatGender = (gender) => {
      switch (gender) {
        case 'M': return 'Männlich'
        case 'W': return 'Weiblich'
        case 'D': return 'Divers'
        case 'U': return 'Unbekannt'
        default: return gender
      }
    }

    // Beim Laden der Komponente
    onMounted(() => {
      loadCustomers()
    })

    return {
      customers,
      loading,
      searchTerm,
      currentCustomer,
      isEditing,
      customerFormModal,
      deleteModal,
      filteredCustomers,
      sortKey,
      sortOrder,
      loadCustomers,
      showAddCustomerModal,
      showEditCustomerModal,
      showDeleteCustomerModal,
      saveCustomer,
      deleteCustomer,
      selectCustomer,
      filterCustomers,
      sortBy,
      formatDate,
      formatGender
    }
  }
}
</script>

<style scoped>
.table th {
  cursor: pointer;
}
.table tbody tr {
  cursor: pointer;
}
</style>