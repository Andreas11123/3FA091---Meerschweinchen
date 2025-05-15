<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
      <router-link class="navbar-brand" to="/">
        <i class="bi bi-house-door-fill me-2"></i>
        Hausverwaltung
      </router-link>

      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
        aria-controls="navbarNav"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/" active-class="active" exact>
              <i class="bi bi-house me-1"></i> Home
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/customers" active-class="active">
              <i class="bi bi-people me-1"></i> Kunden
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/readings" active-class="active">
              <i class="bi bi-speedometer2 me-1"></i> Ablesungen
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/analytics" active-class="active">
              <i class="bi bi-graph-up me-1"></i> Auswertungen
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/import-export" active-class="active">
              <i class="bi bi-arrow-left-right me-1"></i> Import/Export
            </router-link>
          </li>
        </ul>

        <div class="d-flex align-items-center">
          <span class="text-light me-3">
            <i class="bi bi-info-circle me-1"></i>
            Version 1.0.0
          </span>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Navbar'
}
</script>

<style scoped>
.navbar {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

}
</style>