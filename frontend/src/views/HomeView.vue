<template>
  <div class="home-view">
    <div class="jumbotron bg-light p-5 rounded">
      <h1 class="display-4">Willkommen bei der Hausverwaltung</h1>
      <p class="lead">
        Mit diesem System können Sie Kunden verwalten, Ablesungen erfassen und Daten importieren/exportieren.
      </p>
      <hr class="my-4">
      <p>Wählen Sie eine Option aus dem Menü oder nutzen Sie die Schnellzugriffe:</p>

      <div class="row mt-4">
        <div class="col-md-3 mb-3">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">
                <i class="bi bi-people-fill me-2 text-primary"></i>
                Kunden
              </h5>
              <p class="card-text">Kundendaten verwalten und bearbeiten</p>
              <router-link to="/customers" class="btn btn-primary">Zu Kunden</router-link>
            </div>
          </div>
        </div>

        <div class="col-md-3 mb-3">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">
                <i class="bi bi-speedometer2 me-2 text-success"></i>
                Ablesungen
              </h5>
              <p class="card-text">Zählerstände erfassen und bearbeiten</p>
              <router-link to="/readings" class="btn btn-success">Zu Ablesungen</router-link>
            </div>
          </div>
        </div>

        <div class="col-md-3 mb-3">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">
                <i class="bi bi-graph-up me-2 text-info"></i>
                Auswertungen
              </h5>
              <p class="card-text">Daten grafisch visualisieren</p>
              <router-link to="/analytics" class="btn btn-info text-white">Zu Auswertungen</router-link>
            </div>
          </div>
        </div>

        <div class="col-md-3 mb-3">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">
                <i class="bi bi-arrow-left-right me-2 text-warning"></i>
                Import/Export
              </h5>
              <p class="card-text">Daten im- und exportieren</p>
              <router-link to="/import-export" class="btn btn-warning">Zu Import/Export</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row mt-5">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">Systemstatus</h5>
          </div>
          <div class="card-body">
            <div class="d-flex justify-content-between mb-3">
              <span>Verbindung zum Server:</span>
              <span class="text-success">
                <i class="bi bi-check-circle-fill me-1"></i> Aktiv
              </span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span>Datenbank:</span>
              <span class="text-success">
                <i class="bi bi-check-circle-fill me-1"></i> Verbunden
              </span>
            </div>
            <div class="d-flex justify-content-between">
              <span>System-Version:</span>
              <span>1.0.0</span>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="card-title mb-0">Letzte Aktivitäten</h5>
            <span class="badge bg-secondary">Demo</span>
          </div>
          <div class="card-body">
            <ul class="list-group list-group-flush">
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <span>
                  <i class="bi bi-person-plus me-2 text-primary"></i>
                  Neuer Kunde angelegt
                </span>
                <small class="text-muted">Heute</small>
              </li>
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <span>
                  <i class="bi bi-speedometer2 me-2 text-success"></i>
                  Ablesung hinzugefügt
                </span>
                <small class="text-muted">Gestern</small>
              </li>
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <span>
                  <i class="bi bi-arrow-down me-2 text-info"></i>
                  Daten importiert
                </span>
                <small class="text-muted">Vor 2 Tagen</small>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomeView'
}
</script>

<style scoped>
.card-title i {
  font-size: 1.25rem;
}
</style>