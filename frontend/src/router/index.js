import { createRouter, createWebHistory } from 'vue-router'

// Views importieren (werden später erstellt)
const HomeView = () => import('../views/HomeView.vue')
const CustomersView = () => import('../views/CustomersView.vue')
const ReadingsView = () => import('../views/ReadingsView.vue')
const AnalyticsView = () => import('../views/AnalyticsView.vue')
const ImportExportView = () => import('../views/ImportExportView.vue')

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },

  {
    path: '/customers',
    name: 'customers',
    component: CustomersView
  },
  {
    path: '/readings',
    name: 'readings',
    component: ReadingsView
  },
  {
    path: '/analytics',
    name: 'analytics',
    component: AnalyticsView
  },
  {
    path: '/import-export',
    name: 'import-export',
    component: ImportExportView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router