<template>
  <div class="sidebar d-none d-md-block">
    <div class="sidebar-sticky">
      <ul class="nav flex-column">
        <li class="nav-item">
          <router-link class="nav-link" to="/" active-class="active" exact>
            <i class="bi bi-house me-2"></i>
            Home
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" to="/customers" active-class="active">
            <i class="bi bi-people me-2"></i>
            Kunden
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" to="/readings" active-class="active">
            <i class="bi bi-speedometer2 me-2"></i>
            Ablesungen
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" to="/analytics" active-class="active">
            <i class="bi bi-graph-up me-2"></i>
            Auswertungen
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" to="/import-export" active-class="active">
            <i class="bi bi-arrow-left-right me-2"></i>
            Import/Export
          </router-link>
        </li>
      </ul>

      <hr>

      <div class="sidebar-heading">System</div>
      <ul class="nav flex-column mb-2">
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="resetDatabase">
            <i class="bi bi-arrow-clockwise me-2"></i>
            Datenbank zurücksetzen
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex'
import apiClient from '@/services/api'

export default {
  name: 'Sidebar',
  setup() {
    const store = useStore()

    const resetDatabase = async () => {
      if (confirm('Möchten Sie die Datenbank wirklich zurücksetzen? Alle Daten werden gelöscht.')) {
        try {
          store.dispatch('setLoading', true)
          await apiClient.delete('/setupDB')
          alert('Datenbank wurde zurückgesetzt')

          // Daten neu laden
          await store.dispatch('customers/fetchCustomers')
          await store.dispatch('readings/fetchReadings')
        } catch (error) {
          console.error('Fehler beim Zurücksetzen der Datenbank:', error)
          alert('Fehler beim Zurücksetzen der Datenbank')
        } finally {
          store.dispatch('setLoading', false)
        }
      }
    }

    return {
      resetDatabase
    }
  }
}
</script>

<style scoped>
.sidebar {
  position: sticky;
  top: 0;
  height: calc(100vh - 56px);
  padding: 20px 0;
  overflow-x: hidden;
  overflow-y: auto;
  background-color: #f8f9fa;
  border-right: 1px solid #dee2e6;
  width: 240px;
}

.sidebar-sticky {
  position: sticky;
  top: 0;
  height: calc(100vh - 56px);
  padding-top: 20px;
  overflow-x: hidden;
  overflow-y: auto;
}

.sidebar-heading {
  font-size: 0.75rem;
  text-transform: uppercase;
  padding: 0 1rem;
  margin-bottom: 0.5rem;
  color: #6c757d;
  font-weight: bold;
}

.nav-link {
  padding: 0.5rem 1rem;
  color: #495057;
  transition: color 0.2s ease-in-out;
}

.nav-link:hover {
  color: #007bff;
}

.nav-link.active {
  color: #007bff;
  font-weight: bold;
  background-color: rgba(0, 123, 255, 0.1);
}

.nav-link i {
  width: 24px;
  text-align: center;
}
</style>