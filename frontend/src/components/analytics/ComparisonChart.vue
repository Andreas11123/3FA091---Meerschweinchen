<template>
  <div class="comparison-chart">
    <div class="card">
      <div class="card-header">
        <h5 class="card-title mb-0">Verbrauchsvergleich</h5>
      </div>

      <div class="card-body">
        <div class="row mb-3">
          <div class="col-md-4">
            <label for="comparisonMeterType" class="form-label">Zählerart</label>
            <select id="comparisonMeterType" class="form-select" v-model="selectedMeterType">
              <option value="HEIZUNG">Heizung</option>
              <option value="STROM">Strom</option>
              <option value="WASSER">Wasser</option>
            </select>
          </div>


          <div class="col-md-4">
            <label for="comparisonYear1" class="form-label">Jahr 1</label>
            <select id="comparisonYear1" class="form-select" v-model="selectedYear1">
              <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="comparisonYear2" class="form-label">Jahr 2</label>
            <select id="comparisonYear2" class="form-select" v-model="selectedYear2">
              <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
            </select>
          </div>
        </div>

        <div class="mb-3">
          <label for="comparisonCustomer" class="form-label">Kunde</label>
          <select id="comparisonCustomer" class="form-select" v-model="selectedCustomerId">
            <option :value="null">Alle Kunden</option>
            <option v-for="customer in customers" :key="customer.id" :value="customer.id">
              {{ customer.firstName }} {{ customer.lastName }}
            </option>
          </select>
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

        <div v-if="!loading && !noDataAvailable" class="mt-4">
          <h6>Vergleichsanalyse:</h6>
          <div v-if="yearlyTotals.year1 && yearlyTotals.year2" class="alert alert-secondary">
            <p><strong>Gesamtverbrauch im {{ selectedYear1 }}:</strong> {{ formatNumber(yearlyTotals.year1) }}</p>
            <p><strong>Gesamtverbrauch im {{ selectedYear2 }}:</strong> {{ formatNumber(yearlyTotals.year2) }}</p>
            <p>
              <strong>Veränderung:</strong>
              {{ formatNumber(yearlyTotals.difference) }}
              ({{ formatPercentage(yearlyTotals.percentageChange) }})
            </p>
          </div>
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
  name: 'ComparisonChart',
  setup() {
    const store = useStore()
    const chartCanvas = ref(null)
    const chartContainer = ref(null)
    const chartInstance = ref(null)

    // Filter & Auswahl
    const selectedMeterType = ref('STROM')
    const selectedCustomerId = ref(null)
    const selectedYear1 = ref(new Date().getFullYear() - 1) // Voriges Jahr
    const selectedYear2 = ref(new Date().getFullYear()) // Aktuelles Jahr

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

    const filteredReadingsYear1 = computed(() => {
      return filterReadingsByYearAndType(selectedYear1.value)
    })

    const filteredReadingsYear2 = computed(() => {
      return filterReadingsByYearAndType(selectedYear2.value)
    })

    const filterReadingsByYearAndType = (year) => {
      return readings.value.filter(reading => {
        const readingYear = new Date(reading.dateOfReading).getFullYear()

        // Jahr und Zählerart filtern
        if (readingYear !== year || reading.kindOfMeter !== selectedMeterType.value) {
          return false
        }

        // Kunde filtern, falls ausgewählt
        if (selectedCustomerId.value && reading.customer) {
          if (reading.customer.id !== selectedCustomerId.value) {
            return false
          }
        }

        return true
      })
    }

    const chartData = computed(() => {
      // Gruppieren nach Monat für beide Jahre
      const data1 = calculateMonthlyAverages(filteredReadingsYear1.value)
      const data2 = calculateMonthlyAverages(filteredReadingsYear2.value)

      return {
        labels: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
        datasets: [
          {
            label: `${selectedYear1.value}`,
            data: data1,
            backgroundColor: 'rgba(54, 162, 235, 0.7)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          },
          {
            label: `${selectedYear2.value}`,
            data: data2,
            backgroundColor: 'rgba(255, 99, 132, 0.7)',
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 1
          }
        ]
      }
    })

    const calculateMonthlyAverages = (readings) => {
      const monthlyData = Array(12).fill(null).map(() => ({ sum: 0, count: 0 }))

      readings.forEach(reading => {
        const month = new Date(reading.dateOfReading).getMonth()
        monthlyData[month].sum += reading.meterCount
        monthlyData[month].count++
      })

      return monthlyData.map(data => data.count > 0 ? data.sum / data.count : null)
    }

    const yearlyTotals = computed(() => {
      const sumYear1 = filteredReadingsYear1.value.reduce((sum, reading) => sum + reading.meterCount, 0)
      const sumYear2 = filteredReadingsYear2.value.reduce((sum, reading) => sum + reading.meterCount, 0)
      const difference = sumYear2 - sumYear1
      const percentageChange = sumYear1 > 0 ? (difference / sumYear1) * 100 : 0

      return {
        year1: sumYear1,
        year2: sumYear2,
        difference,
        percentageChange
      }
    })

    const noDataAvailable = computed(() => {
      return chartData.value.datasets.every(dataset => dataset.data.every(value => value === null))
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

      // Setze Standardjahre, falls verfügbar
      if (availableYears.value.length > 1) {
        if (!selectedYear1.value || !availableYears.value.includes(selectedYear1.value)) {
          selectedYear1.value = availableYears.value[1] // Vorletztes Jahr
        }
        if (!selectedYear2.value || !availableYears.value.includes(selectedYear2.value)) {
          selectedYear2.value = availableYears.value[0] // Letztes Jahr
        }
      }
    }

    const renderChart = () => {
      if (chartInstance.value) {
        chartInstance.value.destroy()
      }

      if (chartCanvas.value && !noDataAvailable.value) {
        const ctx = chartCanvas.value.getContext('2d')

        chartInstance.value = new Chart(ctx, {
          type: 'bar',
          data: chartData.value,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              title: {
                display: true,
                text: `Vergleich ${formatMeterType(selectedMeterType.value)} - ${selectedYear1.value} vs. ${selectedYear2.value}`
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

    const formatNumber = (value) => {
      return value.toFixed(2).replace('.', ',')
    }

    const formatPercentage = (value) => {
      const prefix = value >= 0 ? '+' : ''
      return `${prefix}${value.toFixed(2).replace('.', ',')}%`
    }

    // Watchers
    watch([selectedMeterType, selectedYear1, selectedYear2, selectedCustomerId, readings], () => {
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
      selectedMeterType,
      selectedCustomerId,
      selectedYear1,
      selectedYear2,
      loading,
      customers,
      availableYears,
      noDataAvailable,
      yearlyTotals,
      formatMeterType,
      formatNumber,
      formatPercentage
    }
  }
}
</script>

<style scoped>
.chart-container {
  height: 400px;
  width: 100%;
}
</style>