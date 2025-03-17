import axios from 'axios'

// Konfiguriere die Basis-URL für Axios mit Umgebungsvariable
const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/test/resources'

// Erstelle eine Axios-Instanz mit der Basis-URL
const apiClient = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000 // 10 Sekunden Timeout
})

// Anfrage-Interceptor
apiClient.interceptors.request.use(
  config => {
    // Hier könnte später Authentifizierung hinzugefügt werden
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Antwort-Interceptor
apiClient.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // Fehlerbehandlung
    const errorMessage = error.response?.data?.error || 'Ein Fehler ist aufgetreten'
    console.error('API-Fehler:', errorMessage)
    return Promise.reject(error)
  }
)

export default apiClient