<template>
  <div class="app-container">
    <Navbar />

    <div class="main-content">
      <div class="container-fluid p-0">
        <div class="row g-0">
          <!-- Sidebar - nur auf md und größeren Bildschirmen anzeigen -->
          <div class="col-md-2 d-none d-md-block sidebar-container">
            <div class="sidebar-content">
              <Sidebar />
            </div>
          </div>

          <!-- Hauptinhalt - nimmt den Rest des Platzes ein -->
          <main class="col-12 col-md-10 main-column">
            <div class="main-wrapper py-3">
              <!-- Loading-Overlay -->
              <div v-if="loading" class="loading-overlay">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Wird geladen...</span>
                </div>
              </div>

              <!-- Fehleranzeige -->
              <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
                {{ error }}
                <button type="button" class="btn-close" @click="clearError" aria-label="Close"></button>
              </div>

              <!-- Mobilmenü - nur auf kleinen Bildschirmen anzeigen -->
              <div class="d-md-none mb-3">
                <MobileSidebar />
              </div>

              <!-- Router-View für die Hauptinhalte -->
              <div class="content-container">
                <router-view />
              </div>
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
import MobileSidebar from '@/components/common/MobileSidebar.vue' // Eine neue Komponente für mobile Geräte

export default {
  name: 'App',
  components: {
    Navbar,
    Sidebar,
    Footer,
    MobileSidebar
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
/* Bootstrap Icons Einbindung - an den Anfang verschieben */
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css");

/* Globale Styles */
html, body {
  height: 100%;
  margin: 0;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
  overflow-x: hidden; /* Verhindert horizontales Scrollen auf Seitenebene */
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
  max-width: 100vw; /* Begrenzt die maximale Breite auf die Viewport-Breite */
  overflow-x: hidden;
}

.main-content {
  flex: 1;
  width: 100%;
}

/* Sidebar-Container - VOLLSTÄNDIG AKTUALISIERT */
.sidebar-container {
  background-color: #f8f9fa;
  border-right: 1px solid #dee2e6;
  height: calc(100vh - 56px - 56px); /* Viewport - Navbar - Footer */
  position: fixed; /* Änderung von sticky zu fixed */
  top: 56px; /* Navbar-Höhe */
  left: 0; /* Am linken Rand fixieren */
  width: 16.66%; /* Entspricht col-md-2 */
  overflow: hidden; /* Verhindert jedes Scrollen */
  z-index: 1000; /* Damit die Sidebar über anderen Inhalten liegt */
}

/* Neuer Container für den Sidebar-Inhalt - AKTUALISIERT */
.sidebar-content {
  height: 100%;
  width: 100%;
  overflow: hidden; /* Kein Scrollen erlauben */
  padding-right: 0;
}

/* Hauptspalte - AKTUALISIERT */
.main-column {
  padding-left: 15px;
  padding-right: 15px;
  margin-left: 16.66%; /* Gleiche Breite wie die Sidebar auf Desktop */
}

/* Content-Container mit Scrolling */
.content-container {
  width: 100%;
  overflow-x: auto; /* Ermöglicht horizontales Scrollen innerhalb des Containers */
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

/* Responsive Tabellen und Karten */
.table-responsive {
  overflow-x: auto;
  width: 100%;
}

.card {
  max-width: 100%;
  overflow: hidden;
}

/* Anpassungen für mobile Geräte */
@media (max-width: 767.98px) {
  .sidebar-container {
    display: none; /* Auf Mobilgeräten ausblenden */
  }

  .main-column {
    margin-left: 0; /* Kein Margin auf Mobilgeräten */
    width: 100%;
    padding-left: 10px;
    padding-right: 10px;
  }

  .main-wrapper {
    padding: 0.5rem;
  }
}
</style>