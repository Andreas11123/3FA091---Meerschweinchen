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
                <th @click="sortBy('firstName')">Vorname
                  <i v-if="sortKey === 'firstName'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('lastName')">Nachname
                  <i v-if="sortKey === 'lastName'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
                </th>
                <th @click="sortBy('birthDate')">Geburtsdatum
                  <i v-if="sortKey === 'birthDate'" :class="sortOrder === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down'"></i>
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
                <td>{{ customer.firstName }}</td>
                <td>{{ customer.lastName }}</td>
                <td>{{ formatDate(customer.birthDate) }}</td>
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
            <customer-form
              :customer="currentCustomer"
              :is-editing="isEditing"
              @save="saveCustomer"
              @cancel="hideCustomerModal"
            />
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
            <p>Möchten Sie den Kunden {{ currentCustomer.firstName }} {{ currentCustomer.lastName }} wirklich löschen?</p>
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
import CustomerForm from './CustomerForm.vue'

export default {
  name: 'CustomerList',
  components: {
    CustomerForm
  },
  setup() {
    // Store & Reactive Variablen
    const store = useStore()
    const searchTerm = ref('')
    const currentCustomer = ref({})
    const isEditing = ref(false)
    const customerFormModal = ref(null)
    const deleteModal = ref(null)
    const sortKey = ref('lastName')
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
          customer.firstName.toLowerCase().includes(term) ||
          customer.lastName.toLowerCase().includes(term)
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
        if (sortKey.value === 'birthDate' && valueA && valueB) {
          valueA = new Date(valueA)
          valueB = new Date(valueB)
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
        firstName: '',
        lastName: '',
        birthDate: null,
        gender: ''
      }
      isEditing.value = false

      const modal = new Modal(customerFormModal.value)
      modal.show()
    }

    const showEditCustomerModal = (customer) => {
      currentCustomer.value = { ...customer }
      isEditing.value = true

      const modal = new Modal(customerFormModal.value)
      modal.show()
    }

    const showDeleteCustomerModal = (customer) => {
      currentCustomer.value = { ...customer }

      const modal = new Modal(deleteModal.value)
      modal.show()
    }

    const hideCustomerModal = () => {
      const modal = Modal.getInstance(customerFormModal.value)
      if (modal) modal.hide()
    }

    const saveCustomer = async (customerData) => {
      try {
        if (isEditing.value) {
          await store.dispatch('customers/updateCustomer', customerData)
          alert('Kunde wurde erfolgreich aktualisiert!')
        } else {
          await store.dispatch('customers/createCustomer', customerData)
          alert('Kunde wurde erfolgreich erstellt!')
        }
        hideCustomerModal()
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

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
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
      hideCustomerModal,
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