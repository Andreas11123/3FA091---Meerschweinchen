<template>
  <form @submit.prevent="submitForm">
    <div class="mb-3">
      <label for="firstName" class="form-label">Vorname</label>
      <input
        type="text"
        class="form-control"
        id="firstName"
        v-model.trim="formData.firstName"
        :class="{ 'is-invalid': errors.firstName }"
        required
      />
      <div v-if="errors.firstName" class="invalid-feedback">
        {{ errors.firstName }}
      </div>
    </div>

    <div class="mb-3">
      <label for="lastName" class="form-label">Nachname</label>
      <input
        type="text"
        class="form-control"
        id="lastName"
        v-model.trim="formData.lastName"
        :class="{ 'is-invalid': errors.lastName }"
        required
      />
      <div v-if="errors.lastName" class="invalid-feedback">
        {{ errors.lastName }}
      </div>
    </div>

    <div class="mb-3">
      <label for="birthDate" class="form-label">Geburtsdatum</label>
      <input
        type="date"
        class="form-control"
        id="birthDate"
        v-model="formData.birthDate"
      />
    </div>

    <div class="mb-3">
      <label for="gender" class="form-label">Geschlecht</label>
      <select
        class="form-select"
        id="gender"
        v-model="formData.gender"
        :class="{ 'is-invalid': errors.gender }"
        required
      >
        <option value="" disabled>Bitte wählen</option>
        <option value="M">Männlich</option>
        <option value="W">Weiblich</option>
        <option value="D">Divers</option>
        <option value="U">Unbekannt</option>
      </select>
      <div v-if="errors.gender" class="invalid-feedback">
        {{ errors.gender }}
      </div>
    </div>

    <div class="d-flex justify-content-end gap-2">
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">Abbrechen</button>
      <button type="submit" class="btn btn-primary">Speichern</button>
    </div>
  </form>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'

export default {
  name: 'CustomerForm',
  props: {
    customer: {
      type: Object,
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
      firstName: '',
      lastName: '',
      birthDate: null,
      gender: ''
    })

    const errors = reactive({
      firstName: '',
      lastName: '',
      gender: ''
    })

    // Daten aus props laden
    const initForm = () => {
      formData.id = props.customer.id || null
      formData.firstName = props.customer.firstName || ''
      formData.lastName = props.customer.lastName || ''
      formData.birthDate = props.customer.birthDate || null
      formData.gender = props.customer.gender || ''

      // Datum formatieren, falls vorhanden
      if (formData.birthDate && typeof formData.birthDate === 'string') {
        // Für HTML date input wird YYYY-MM-DD benötigt
        const date = new Date(formData.birthDate)
        formData.birthDate = date.toISOString().split('T')[0]
      }

      // Fehler zurücksetzen
      Object.keys(errors).forEach(key => {
        errors[key] = ''
      })
    }

    const validateForm = () => {
      let isValid = true

      // Vorname prüfen
      if (!formData.firstName.trim()) {
        errors.firstName = 'Vorname ist erforderlich'
        isValid = false
      } else {
        errors.firstName = ''
      }

      // Nachname prüfen
      if (!formData.lastName.trim()) {
        errors.lastName = 'Nachname ist erforderlich'
        isValid = false
      } else {
        errors.lastName = ''
      }

      // Geschlecht prüfen
      if (!formData.gender) {
        errors.gender = 'Geschlecht ist erforderlich'
        isValid = false
      } else {
        errors.gender = ''
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