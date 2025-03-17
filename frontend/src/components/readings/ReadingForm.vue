<template>
  <form @submit.prevent="submitForm">
    <div class="mb-3">
      <label for="customer" class="form-label">Kunde</label>
      <select
        class="form-select"
        id="customer"
        v-model="formData.customer"
        :class="{ 'is-invalid': errors.customer }"
        required
      >
        <option value="" disabled>Bitte wählen</option>
        <option v-for="customer in customers" :key="customer.id" :value="customer">
          {{ customer.firstName }} {{ customer.lastName }}
        </option>
      </select>
      <div v-if="errors.customer" class="invalid-feedback">
        {{ errors.customer }}
      </div>
    </div>

    <div class="mb-3">
      <label for="dateOfReading" class="form-label">Ablesedatum</label>
      <input
        type="date"
        class="form-control"
        id="dateOfReading"
        v-model="formData.dateOfReading"
        :class="{ 'is-invalid': errors.dateOfReading }"
        required
      />
      <div v-if="errors.dateOfReading" class="invalid-feedback">
        {{ errors.dateOfReading }}
      </div>
    </div>

    <div class="mb-3">
      <label for="kindOfMeter" class="form-label">Zählerart</label>
      <select
        class="form-select"
        id="kindOfMeter"
        v-model="formData.kindOfMeter"
        :class="{ 'is-invalid': errors.kindOfMeter }"
        required
      >
        <option value="" disabled>Bitte wählen</option>
        <option value="HEIZUNG">Heizung</option>
        <option value="STROM">Strom</option>
        <option value="WASSER">Wasser</option>
        <option value="UNBEKANNT">Unbekannt</option>
      </select>
      <div v-if="errors.kindOfMeter" class="invalid-feedback">
        {{ errors.kindOfMeter }}
      </div>
    </div>

    <div class="mb-3">
      <label for="meterId" class="form-label">Zähler-ID</label>
      <input
        type="text"
        class="form-control"
        id="meterId"
        v-model.trim="formData.meterId"
        :class="{ 'is-invalid': errors.meterId }"
        required
      />
      <div v-if="errors.meterId" class="invalid-feedback">
        {{ errors.meterId }}
      </div>
    </div>

    <div class="mb-3">
      <label for="meterCount" class="form-label">Zählerstand</label>
      <input
        type="number"
        step="0.01"
        class="form-control"
        id="meterCount"
        v-model.number="formData.meterCount"
        :class="{ 'is-invalid': errors.meterCount }"
        required
      />
      <div v-if="errors.meterCount" class="invalid-feedback">
        {{ errors.meterCount }}
      </div>
    </div>

    <div class="mb-3 form-check">
      <input
        type="checkbox"
        class="form-check-input"
        id="substitute"
        v-model="formData.substitute"
      />
      <label class="form-check-label" for="substitute">Ersatzwert</label>
    </div>

    <div class="mb-3">
      <label for="comment" class="form-label">Kommentar</label>
      <textarea
        class="form-control"
        id="comment"
        v-model.trim="formData.comment"
        rows="3"
      ></textarea>
    </div>

    <div class="d-flex justify-content-end gap-2">
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">Abbrechen</button>
      <button type="submit" class="btn btn-primary">Speichern</button>
    </div>
  </form>
</template>

<script>
import { reactive, onMounted } from 'vue'

export default {
  name: 'ReadingForm',
  props: {
    reading: {
      type: Object,
      required: true
    },
    customers: {
      type: Array,
      required: true
    },
    isEditing: {
      type: Boolean,
      default: false
    }
  },
  emits: ['save', 'cancel'],
  setup(props, { emit }) {
    const formData = reactive({
      id: null,
      customer: null,
      dateOfReading: null,
      kindOfMeter: '',
      meterId: '',
      meterCount: null,
      substitute: false,
      comment: ''
    })

    const errors = reactive({
      customer: '',
      dateOfReading: '',
      kindOfMeter: '',
      meterId: '',
      meterCount: ''
    })

    // Initialisierung der Formulardaten aus dem übergebenen Reading-Objekt
    const initForm = () => {
      formData.id = props.reading.id || null
      formData.dateOfReading = props.reading.dateOfReading || null
      formData.kindOfMeter = props.reading.kindOfMeter || ''
      formData.meterId = props.reading.meterId || ''
      formData.meterCount = props.reading.meterCount || null
      formData.substitute = props.reading.substitute || false
      formData.comment = props.reading.comment || ''

      // Referenziert den passenden Kunden aus der customers-Liste
      if (props.reading.customer && props.reading.customer.id) {
        formData.customer = props.customers.find(c => c.id === props.reading.customer.id) || null
      } else {
        formData.customer = null
      }

      // Fehler zurücksetzen
      Object.keys(errors).forEach(key => {
        errors[key] = ''
      })
    }

    const validateForm = () => {
      let isValid = true

      // Kunde prüfen
      if (!formData.customer) {
        errors.customer = 'Kunde ist erforderlich'
        isValid = false
      } else {
        errors.customer = ''
      }

      // Ablesedatum prüfen
      if (!formData.dateOfReading) {
        errors.dateOfReading = 'Ablesedatum ist erforderlich'
        isValid = false
      } else {
        errors.dateOfReading = ''
      }

      // Zählerart prüfen
      if (!formData.kindOfMeter) {
        errors.kindOfMeter = 'Zählerart ist erforderlich'
        isValid = false
      } else {
        errors.kindOfMeter = ''
      }

      // Zähler-ID prüfen
      if (!formData.meterId.trim()) {
        errors.meterId = 'Zähler-ID ist erforderlich'
        isValid = false
      } else {
        errors.meterId = ''
      }

      // Zählerstand prüfen
      if (formData.meterCount === null || formData.meterCount === undefined || isNaN(formData.meterCount)) {
        errors.meterCount = 'Zählerstand ist erforderlich und muss eine gültige Zahl sein'
        isValid = false
      } else {
        errors.meterCount = ''
      }

      return isValid
    }

    const submitForm = () => {
      if (validateForm()) {
        emit('save', { ...formData })
      }
    }

    onMounted(() => {
      initForm()
    })

    return {
      formData,
      errors,
      submitForm
    }
  }
}
</script>