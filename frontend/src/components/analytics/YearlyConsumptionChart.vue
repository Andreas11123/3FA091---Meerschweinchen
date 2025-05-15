<template>
  <div class="yearly-consumption-chart">
    <div class="card">
      <div class="card-header">
        <h5 class="card-title mb-0">Jahresverbrauch nach Zählerart</h5>
      </div>

      <div class="card-body">
        <div class="row mb-3">
          <div class="col-md-4">
            <label for="yearSelect" class="form-label">Jahr</label>
            <select id="yearSelect" class="form-select" v-model="selectedYear">
              <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="customerSelect" class="form-label">Kunde</label>
            <select id="customerSelect" class="form-select" v-model="selectedCustomerId">
              <option :value="null">Alle Kunden</option>
              <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                {{ customer.firstName }} {{ customer.lastName }}
              </option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="meterTypeSelect" class="form-label">Zählerart</label>
            <select id="meterTypeSelect" class="form-select" v-model="selectedMeterType">
              <option value="all">Alle Zählerarten</option>
              <option value="HEIZUNG">Heizung</option>
              <option value="STROM">Strom</option>
              <option value="WASSER">Wasser</option>
            </select>
          </div>
        </div>

        <div v-if="loading" class="text-center my-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Wird geladen...</span>
          </div>
          <p class="mt-2">Daten werden geladen...</p>
        </div>

        <div v-else-if="noDataAvailable" class="alert alert-info text-center my-4">
          <i class="bi bi-info-circle me-2"></i>
          Keine Daten für die gewählten Filter verfügbar.
        </div>

        <div v-else ref="chartContainer" class="chart-container">
          <canvas ref="chartCanvas"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { useStore } from 'vuex'
import Chart from 'chart.js/auto'

export default {
  name: 'YearlyConsumptionChart',
  setup() {
    const store = useStore()
    const chartCanvas = ref(null)
    const chartContainer = ref(null)
    const chartInstance = ref(null)

    // Filter & Auswahl
    const selectedYear = ref(new Date().getFullYear())
    const selectedCustomerId = ref(null)
    const selectedMeterType = ref('all')

    // Computed Properties
    const loading = computed(() => store.getters.isLoading)
    const customers = computed(() => store.getters['customers/getCustomers'])
    const readings = computed(() => store.getters['readings/getReadings'])

    const availableYears = computed(() => {
      const years = new Set()

      readings.value.forEach(reading => {
        const year = new Date(reading.dateOfReading).getFullYear()
        years.add(year)
      })

      return Array.from(years).sort((a, b) => b - a) // Absteigend sortieren
    })

    const filteredReadings = computed(() => {
      let result = readings.value.filter(reading => {
        const readingYear = new Date(reading.dateOfReading).getFullYear()

        // Jahr filtern
        if (readingYear !== selectedYear.value) {
          return false
        }

        // Kunde filtern, falls ausgewählt
        if (selectedCustomerId.value && reading.customer) {
          if (reading.customer.id !== selectedCustomerId.value) {
            return false
          }
        }

        // Zählerart filtern, falls nicht "alle"
        if (selectedMeterType.value !== 'all' && reading.kindOfMeter !== selectedMeterType.value) {
          return false
        }

        return true
      })

      return result
    })

    const chartData = computed(() => {
      // Gruppieren nach Monat und Zählerart
      const monthlyData = {}
      const meterTypes = new Set()

      // Initialisiere alle Monate
      for (let i = 0; i < 12; i++) {
        monthlyData[i] = {}
      }

      // Filtere und aggregiere die Daten
      filteredReadings.value.forEach(reading => {
        const month = new Date(reading.dateOfReading).getMonth()
        const meterType = reading.kindOfMeter

        meterTypes.add(meterType)

        if (!monthlyData[month][meterType]) {
          monthlyData[month][meterType] = {
            count: 0,
            sum: 0
          }
        }

        monthlyData[month][meterType].count++
        monthlyData[month][meterType].sum += reading.meterCount
      })

      // Berechne Durchschnitt pro Monat und Zählerart
      const datasets = []
      const meterTypeArray = Array.from(meterTypes)

      // Farben für die verschiedenen Zählerarten
      const colors = {
        HEIZUNG: 'rgba(255, 99, 132, 0.7)',
        STROM: 'rgba(54, 162, 235, 0.7)',
        WASSER: 'rgba(75, 192, 192, 0.7)',
        UNBEKANNT: 'rgba(153, 102, 255, 0.7)'
      }

      // Erstelle Dataset für jede Zählerart
      meterTypeArray.forEach(meterType => {
        const data = []

        for (let month = 0; month < 12; month++) {
          if (monthlyData[month][meterType]) {
            data.push(monthlyData[month][meterType].sum / monthlyData[month][meterType].count)
          } else {
            data.push(null) // Keine Daten für diesen Monat
          }
        }

        datasets.push({
          label: formatMeterType(meterType),
          data: data,
          backgroundColor: colors[meterType] || 'rgba(153, 102, 255, 0.7)',
          borderColor: colors[meterType]?.replace('0.7', '1') || 'rgba(153, 102, 255, 1)',
          borderWidth: 1
        })
      })

      return {
        labels: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
        datasets: datasets
      }
    })

    const noDataAvailable = computed(() => {
      return chartData.value.datasets.length === 0 ||
             chartData.value.datasets.every(dataset => dataset.data.every(value => value === null))
    })

    // Methoden
    const loadData = async () => {
      if (readings.value.length === 0) {
        try {
          await store.dispatch('readings/fetchReadings')
        } catch (error) {
          console.error('Fehler beim Laden der Ablesungen:', error)
        }
      }

      if (customers.value.length === 0) {
        try {
          await store.dispatch('customers/fetchCustomers')
        } catch (error) {
          console.error('Fehler beim Laden der Kunden:', error)
        }
      }

      // Setze Standardjahr, falls verfügbar
      if (availableYears.value.length > 0 && !selectedYear.value) {
        selectedYear.value = availableYears.value[0]
      }
    }

    const renderChart = () => {
      if (chartInstance.value) {
        chartInstance.value.destroy()
      }

      if (chartCanvas.value && !noDataAvailable.value) {
        const ctx = chartCanvas.value.getContext('2d')

        chartInstance.value = new Chart(ctx, {
          type: 'line',
          data: chartData.value,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              title: {
                display: true,
                text: `Verbrauch ${selectedYear.value} - ${selectedMeterType.value === 'all' ? 'Alle Zählerarten' : formatMeterType(selectedMeterType.value)}`
              },
              tooltip: {
                mode: 'index',
                intersect: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                title: {
                  display: true,
                  text: 'Durchschnittlicher Zählerstand'
                }
              }
            }
          }
        })
      }
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

    // Watchers
    watch([selectedYear, selectedCustomerId, selectedMeterType, readings], () => {
      renderChart()
    })

    // Lebenszyklus-Hooks
    onMounted(async () => {
      await loadData()
      renderChart()

      // Responsive Chart bei Größenänderung
      window.addEventListener('resize', renderChart)
    })

    return {
      chartCanvas,
      chartContainer,
      selectedYear,
      selectedCustomerId,
      selectedMeterType,
      loading,
      customers,
      availableYears,
      noDataAvailable
    }
  }
}
</script>

<style scoped>
.chart-container {
  height: 400px;
  width: 100%;
  max-width: 100%;
}

.card {
  max-width: 100%;
  overflow: hidden;
}

.row {
  margin-right: 0;
  margin-left: 0;
}
</style>