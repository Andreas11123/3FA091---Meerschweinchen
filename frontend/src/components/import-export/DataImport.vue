<template>
  <div class="data-import">
    <div class="card">
      <div class="card-header">
        <h5 class="card-title mb-0">Daten importieren</h5>
      </div>

      <div class="card-body">
        <form @submit.prevent="importData">
          <div class="mb-3">
            <label for="dataType" class="form-label">Datentyp</label>
            <select id="dataType" class="form-select" v-model="dataType" required>
              <option value="customers">Kunden</option>
              <option value="readings">Ablesungen</option>
            </select>
          </div>

          <div class="mb-3">
            <label for="fileFormat" class="form-label">Dateiformat</label>
            <select id="fileFormat" class="form-select" v-model="fileFormat" required>
              <option value="json">JSON</option>
              <option value="xml">XML</option>
              <option value="csv">CSV</option>
            </select>
          </div>

          <div class="mb-3">
            <label for="file" class="form-label">Datei auswählen</label>
            <input
              type="file"
              id="file"
              class="form-control"
              @change="handleFileUpload"
              :accept="getAcceptType()"
              required
            />
            <small class="form-text text-muted">
              {{ getFileTypeInfo() }}
            </small>
          </div>

          <div v-if="importStatus === 'success'" class="alert alert-success" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            Import erfolgreich abgeschlossen.
            <div v-if="importResult">
              Es wurden {{ importResult.successful }} Datensätze importiert.
            </div>
          </div>

          <div v-if="importStatus === 'partial'" class="alert alert-warning" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            Import teilweise erfolgreich.
            <div v-if="importResult">
              {{ importResult.successful }} Datensätze erfolgreich, {{ importResult.failed }} Datensätze fehlgeschlagen.
            </div>
            <div v-if="importErrors.length > 0" class="mt-2">
              <strong>Fehler:</strong>
              <ul>
                <li v-for="(error, index) in importErrors" :key="index">
                  {{ error.error }}
                </li>
              </ul>
            </div>
          </div>

          <div v-if="importStatus === 'error'" class="alert alert-danger" role="alert">
            <i class="bi bi-x-circle-fill me-2"></i>
            Fehler beim Import.
            <div v-if="importErrors.length > 0" class="mt-2">
              <strong>Fehler:</strong>
              <ul>
                <li v-for="(error, index) in importErrors" :key="index">
                  {{ error.error }}
                </li>
              </ul>
            </div>
          </div>

          <div class="d-flex justify-content-end">
            <button
              type="submit"
              class="btn btn-primary"
              :disabled="!selectedFile || loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Importieren
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'DataImport',
  setup() {
    const store = useStore()

    const dataType = ref('customers')
    const fileFormat = ref('json')
    const selectedFile = ref(null)

    // Computed Properties
    const loading = computed(() => store.getters.isLoading)
    const importStatus = computed(() => store.getters['importExport/getImportStatus'])
    const importErrors = computed(() => store.getters['importExport/getImportErrors'])
    const importResult = computed(() => store.getters['importExport/getLastImportedData'])

    // Methoden
    const handleFileUpload = (event) => {
      selectedFile.value = event.target.files[0] || null
    }

    const getAcceptType = () => {
      switch (fileFormat.value) {
        case 'json': return '.json,application/json'
        case 'xml': return '.xml,application/xml,text/xml'
        case 'csv': return '.csv,text/csv'
        default: return ''
      }
    }

    const getFileTypeInfo = () => {
      switch (fileFormat.value) {
        case 'json':
          return dataType.value === 'customers'
            ? 'JSON-Datei mit {"customers": [...]} Struktur.'
            : 'JSON-Datei mit {"readings": [...]} Struktur.'
        case 'xml':
          return dataType.value === 'customers'
            ? 'XML-Datei mit <customers><customer>...</customer></customers> Struktur.'
            : 'XML-Datei mit <readings><reading>...</reading></readings> Struktur.'
        case 'csv':
          return 'CSV-Datei mit Header-Zeile und Komma als Trennzeichen.'
        default:
          return ''
      }
    }

    const importData = async () => {
      if (!selectedFile.value) return

      try {
        // Status zurücksetzen
        store.dispatch('importExport/clearImportStatus')

        // Datei importieren
        if (dataType.value === 'customers') {
          await store.dispatch('importExport/importCustomers', {
            file: selectedFile.value,
            format: fileFormat.value
          })
        } else {
          await store.dispatch('importExport/importReadings', {
            file: selectedFile.value,
            format: fileFormat.value
          })
        }
      } catch (error) {
        console.error('Import-Fehler:', error)
      }
    }

    return {
      dataType,
      fileFormat,
      selectedFile,
      loading,
      importStatus,
      importErrors,
      importResult,
      handleFileUpload,
      getAcceptType,
      getFileTypeInfo,
      importData
    }
  }
}
</script>

<style scoped>
.data-import {
  max-width: 800px;
  margin: 0 auto;
}

</style>