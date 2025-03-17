import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// Bootstrap importieren
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// PrimeVue vorerst entfernen
// import PrimeVue from 'primevue/config'
// import 'primevue/resources/themes/lara-light-indigo/theme.css'
// import 'primevue/resources/primevue.css'
// import 'primeicons/primeicons.css'

// App erstellen und mit Plugins erweitern
const app = createApp(App)
app.use(router)
app.use(store)
// app.use(PrimeVue)

// App starten
app.mount('#app')