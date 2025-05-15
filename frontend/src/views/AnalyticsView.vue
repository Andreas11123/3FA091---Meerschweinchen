<template>
  <div class="analytics">
    <h1>Datenauswertung</h1>
    <p>Hier werden Daten ausgewertet.</p>
    <div class="analytics-view">
      <h1 class="mb-4">Datenauswertung</h1>

      <div class="mb-5">
        <yearly-consumption-chart />
      </div>

      <div class="mb-5">
        <comparison-chart />
      </div>
    </div>
  </div>
</template>

<script>
import YearlyConsumptionChart from '@/components/analytics/YearlyConsumptionChart.vue'
import ComparisonChart from '@/components/analytics/ComparisonChart.vue'

export default {
  name: 'AnalyticsView',
  components: {
    YearlyConsumptionChart,
    ComparisonChart
  }
}
</script>