<template>
  <div class="app-container">
    <header class="app-header">
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">Hausverwaltung</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <router-link class="nav-link" to="/">Home</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/customers">Kunden</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/readings">Ablesungen</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/analytics">Auswertungen</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/import-export">Import/Export</router-link>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>

    <main class="app-content container mt-4">
      <router-view />
    </main>

    <footer class="app-footer text-center py-3 mt-5 bg-light">
      <div class="container">
        <span class="text-muted">© 2025 Hausverwaltung</span>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-content {
  flex: 1;
}
</style>