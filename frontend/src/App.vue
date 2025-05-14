<template>
  <div class="app-container">
    <Navbar />

    <div class="main-content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-md-2 d-none d-md-block">
            <Sidebar />
          </div>

          <main class="col-md-10">
            <div class="main-wrapper py-3">
              <div v-if="loading" class="loading-overlay">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Wird geladen...</span>
                </div>
              </div>

              <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
                {{ error }}
                <button type="button" class="btn-close" @click="clearError" aria-label="Close"></button>
              </div>

              <router-view />
            </div>
          </main>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import Navbar from '@/components/common/Navbar.vue'
import Sidebar from '@/components/common/Sidebar.vue'
import Footer from '@/components/common/Footer.vue'

export default {
  name: 'App',
  components: {
    Navbar,
    Sidebar,
    Footer
  },
  setup() {
    const store = useStore()

    const loading = computed(() => store.getters.isLoading)
    const error = computed(() => store.getters.errorMessage)

    const clearError = () => {
      store.dispatch('clearError')
    }

    return {
      loading,
      error,
      clearError
    }
  }
}
</script>

<style>
/* Globale Styles */
html, body {
  height: 100%;
  margin: 0;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}

.main-wrapper {
  position: relative;
  min-height: calc(100vh - 56px - 56px); /* Viewport - Navbar - Footer */
  padding: 1rem;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

/* Bootstrap Icons Einbindung */
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css");
</style>