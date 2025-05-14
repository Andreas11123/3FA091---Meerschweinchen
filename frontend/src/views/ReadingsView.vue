<template>
  <div class="readings-view">
    <h1 class="mb-4">Ablesungsverwaltung</h1>

    <reading-list />
  </div>
</template>

<script>
import ReadingList from '@/components/readings/ReadingList.vue'

export default {
  name: 'ReadingsView',
  components: {
    ReadingList
  }
}
</script>