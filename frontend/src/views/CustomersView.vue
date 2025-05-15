<template>
  <div class="customers-view">
    <h1 class="mb-4">Kundenverwaltung</h1>

    <customer-list />
  </div>
</template>

<script>
import CustomerList from '@/components/customers/CustomerList.vue'

export default {
  name: 'CustomersView', 
  components: {
    CustomerList
  }
}
</script>