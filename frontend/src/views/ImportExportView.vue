<template>
  <div class="import-export">
    <h1>Import/Export</h1>
    <p>Hier können Daten importiert und exportiert werden die datwen.</p>
  <div class="import-export-view">
    <h1 class="mb-4">Import/Export</h1>

    <div class="row">
      <div class="col-md-6 mb-4">
        <data-import />
      </div>

      <div class="col-md-6 mb-4">
        <data-export />
      </div>
    </div>
  </div>
</template>

<script>
import DataImport from '@/components/import-export/DataImport.vue'
import DataExport from '@/components/import-export/DataExport.vue'

export default {
  name: 'ImportExportView'
  name: 'ImportExportView',
  components: {
    DataImport,
    DataExport
  }
}

</script>