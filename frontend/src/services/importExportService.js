import Papa from 'papaparse'
import { saveAs } from 'file-saver'
import convert from 'xml-js'

export default {
  /**
   * Importiert Daten aus einer Datei
   * @param {File} file - Die zu importierende Datei
   * @param {string} format - Das Format (json, xml, csv)
   * @param {string} type - Der Datentyp (customers, readings)
   * @returns {Promise<Array>} - Promise mit importierten Daten
   */
  async importData(file, format, type) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()

      reader.onload = async (e) => {
        try {
          const content = e.target.result
          let data = []

          // Konvertiere den Dateiinhalt je nach Format
          switch (format) {
            case 'json':
              data = this._parseJson(content, type)
              break
            case 'xml':
              data = this._parseXml(content, type)
              break
            case 'csv':
              data = await this._parseCsv(content, type)
              break
            default:
              throw new Error(`Nicht unterstütztes Format: ${format}`)
          }

          // Validiere die importierten Daten
          const validatedData = this._validateImportedData(data, type)
          resolve(validatedData)
        } catch (error) {
          reject(error)
        }
      }

      reader.onerror = () => {
        reject(new Error('Fehler beim Lesen der Datei'))
      }

      // Lese die Datei als Text
      reader.readAsText(file)
    })
  },

  /**
   * Exportiert Daten in eine Datei
   * @param {Array} data - Die zu exportierenden Daten
   * @param {string} format - Das Format (json, xml, csv)
   * @param {string} type - Der Datentyp (customers, readings)
   * @returns {Promise<Blob>} - Promise mit Blob der exportierten Daten
   */
  async exportData(data, format, type) {
    try {
      let content
      let mimeType
      let extension

      // Formatiere die Daten je nach Format
      switch (format) {
        case 'json':
          content = this._formatJson(data, type)
          mimeType = 'application/json'
          extension = 'json'
          break
        case 'xml':
          content = this._formatXml(data, type)
          mimeType = 'application/xml'
          extension = 'xml'
          break
        case 'csv':
          content = this._formatCsv(data, type)
          mimeType = 'text/csv'
          extension = 'csv'
          break
        default:
          throw new Error(`Nicht unterstütztes Format: ${format}`)
      }

      // Erstelle Blob und speichere die Datei
      const blob = new Blob([content], { type: mimeType })
      saveAs(blob, `${type}_export.${extension}`)
      return blob
    } catch (error) {
      throw error
    }
  },

  /**
   * Parser für JSON-Daten
   * @private
   */
  _parseJson(content, type) {
    try {
      const parsed = JSON.parse(content)

      if (type === 'customers') {
        return parsed.customers || []
      } else if (type === 'readings') {
        return parsed.readings || []
      } else {
        throw new Error(`Unbekannter Datentyp: ${type}`)
      }
    } catch (error) {
      throw new Error(`Ungültiges JSON-Format: ${error.message}`)
    }
  },

  /**
   * Parser für XML-Daten
   * @private
   */
  _parseXml(content, type) {
    try {
      const options = { compact: true, ignoreComment: true, alwaysChildren: true }
      const parsed = convert.xml2js(content, options)

      if (type === 'customers' && parsed.customers && parsed.customers.customer) {
        // Konvertiere das XML-Format in das erwartete JSON-Format
        const customers = Array.isArray(parsed.customers.customer)
          ? parsed.customers.customer
          : [parsed.customers.customer]

        return customers.map(customer => ({
          id: customer.id ? customer.id._text : null,
          firstName: customer.firstName._text,
          lastName: customer.lastName._text,
          birthDate: customer.birthDate ? customer.birthDate._text : null,
          gender: customer.gender._text
        }))
      } else if (type === 'readings' && parsed.readings && parsed.readings.reading) {
        // Konvertiere das XML-Format in das erwartete JSON-Format
        const readings = Array.isArray(parsed.readings.reading)
          ? parsed.readings.reading
          : [parsed.readings.reading]

        return readings.map(reading => {
          const result = {
            id: reading.id ? reading.id._text : null,
            dateOfReading: reading.dateOfReading._text,
            meterId: reading.meterId._text,
            substitute: reading.substitute._text === 'true',
            meterCount: parseFloat(reading.meterCount._text),
            kindOfMeter: reading.kindOfMeter._text,
            comment: reading.comment ? reading.comment._text : null
          }

          if (reading.customer && reading.customer._text !== 'null') {
            result.customer = {
              id: reading.customer.id._text,
              firstName: reading.customer.firstName._text,
              lastName: reading.customer.lastName._text,
              birthDate: reading.customer.birthDate ? reading.customer.birthDate._text : null,
              gender: reading.customer.gender._text
            }
          }

          return result
        })
      } else {
        throw new Error(`Ungültiges XML-Format für Typ ${type}`)
      }
    } catch (error) {
      throw new Error(`Ungültiges XML-Format: ${error.message}`)
    }
  },

  /**
   * Parser für CSV-Daten
   * @private
   */
  async _parseCsv(content, type) {
    return new Promise((resolve, reject) => {
      Papa.parse(content, {
        header: true,
        skipEmptyLines: true,
        dynamicTyping: true,
        complete: (results) => {
          try {
            if (results.errors && results.errors.length > 0) {
              throw new Error(`CSV-Parsing-Fehler: ${results.errors[0].message}`)
            }

            if (type === 'customers') {
              // Konvertiere das CSV-Format in das erwartete JSON-Format
              const customers = results.data.map(row => ({
                id: row.id || null,
                firstName: row.firstName || row.firstname,
                lastName: row.lastName || row.lastname,
                birthDate: row.birthDate || row.birthdate,
                gender: row.gender
              }))
              resolve(customers)
            } else if (type === 'readings') {
              // Konvertiere das CSV-Format in das erwartete JSON-Format
              const readings = results.data.map(row => {
                const result = {
                  id: row.id || null,
                  dateOfReading: row.dateOfReading,
                  meterId: row.meterId,
                  substitute: row.substitute === 'true' || row.substitute === true,
                  meterCount: parseFloat(row.meterCount),
                  kindOfMeter: row.kindOfMeter,
                  comment: row.comment || null
                }

                // Füge Kundeninformationen hinzu, falls vorhanden
                if (row.customerId) {
                  result.customer = {
                    id: row.customerId,
                    firstName: row.customerFirstName,
                    lastName: row.customerLastName,
                    birthDate: row.customerBirthDate,
                    gender: row.customerGender
                  }
                }

                return result
              })
              resolve(readings)
            } else {
              throw new Error(`Unbekannter Datentyp: ${type}`)
            }
          } catch (error) {
            reject(error)
          }
        },
        error: (error) => {
          reject(new Error(`CSV-Parsing-Fehler: ${error.message}`))
        }
      })
    })
  },

  /**
   * Formatter für JSON-Daten
   * @private
   */
  _formatJson(data, type) {
    if (type === 'customers') {
      return JSON.stringify({ customers: data }, null, 2)
    } else if (type === 'readings') {
      return JSON.stringify({ readings: data }, null, 2)
    } else {
      throw new Error(`Unbekannter Datentyp: ${type}`)
    }
  },

  /**
   * Formatter für XML-Daten
   * @private
   */
  _formatXml(data, type) {
    try {
      let jsObject = {}

      if (type === 'customers') {
        // Erstelle ein XML-freundliches JS-Objekt für Kunden
        jsObject = {
          customers: {
            customer: data.map(customer => ({
              id: { _text: customer.id || '' },
              firstName: { _text: customer.firstName },
              lastName: { _text: customer.lastName },
              birthDate: { _text: customer.birthDate || '' },
              gender: { _text: customer.gender }
            }))
          }
        }
      } else if (type === 'readings') {
        // Erstelle ein XML-freundliches JS-Objekt für Ablesungen
        jsObject = {
          readings: {
            reading: data.map(reading => {
              const result = {
                id: { _text: reading.id || '' },
                dateOfReading: { _text: reading.dateOfReading },
                meterId: { _text: reading.meterId },
                substitute: { _text: reading.substitute.toString() },
                meterCount: { _text: reading.meterCount.toString() },
                kindOfMeter: { _text: reading.kindOfMeter },
                comment: { _text: reading.comment || '' }
              }

              if (reading.customer) {
                result.customer = {
                  id: { _text: reading.customer.id },
                  firstName: { _text: reading.customer.firstName },
                  lastName: { _text: reading.customer.lastName },
                  birthDate: { _text: reading.customer.birthDate || '' },
                  gender: { _text: reading.customer.gender }
                }
              } else {
                result.customer = { _text: 'null' }
              }

              return result
            })
          }
        }
      } else {
        throw new Error(`Unbekannter Datentyp: ${type}`)
      }

      // Konvertiere das JS-Objekt in XML
      const options = { compact: true, ignoreComment: true, spaces: 2 }
      return convert.js2xml(jsObject, options)
    } catch (error) {
      throw new Error(`Fehler beim Formatieren als XML: ${error.message}`)
    }
  },

  /**
   * Formatter für CSV-Daten
   * @private
   */
  _formatCsv(data, type) {
    try {
      let csvData = []

      if (type === 'customers') {
        // Konvertiere Kundendaten in CSV-Format
        csvData = data.map(customer => ({
          id: customer.id || '',
          firstName: customer.firstName,
          lastName: customer.lastName,
          birthDate: customer.birthDate || '',
          gender: customer.gender
        }))
      } else if (type === 'readings') {
        // Konvertiere Ablesungsdaten in CSV-Format
        csvData = data.map(reading => {
          const result = {
            id: reading.id || '',
            dateOfReading: reading.dateOfReading,
            meterId: reading.meterId,
            substitute: reading.substitute.toString(),
            meterCount: reading.meterCount.toString(),
            kindOfMeter: reading.kindOfMeter,
            comment: reading.comment || ''
          }

          // Füge Kundeninformationen hinzu, falls vorhanden
          if (reading.customer) {
            result.customerId = reading.customer.id
            result.customerFirstName = reading.customer.firstName
            result.customerLastName = reading.customer.lastName
            result.customerBirthDate = reading.customer.birthDate || ''
            result.customerGender = reading.customer.gender
          } else {
            result.customerId = ''
            result.customerFirstName = ''
            result.customerLastName = ''
            result.customerBirthDate = ''
            result.customerGender = ''
          }

          return result
        })
      } else {
        throw new Error(`Unbekannter Datentyp: ${type}`)
      }

      // Konvertiere die Daten in CSV
      return Papa.unparse(csvData)
    } catch (error) {
      throw new Error(`Fehler beim Formatieren als CSV: ${error.message}`)
    }
  },

  /**
   * Validiert importierte Daten
   * @private
   */
  _validateImportedData(data, type) {
    if (!Array.isArray(data)) {
      throw new Error('Importierte Daten sind kein Array')
    }

    if (type === 'customers') {
      // Validiere jedes Kundenobjekt
      return data.map(customer => {
        if (!customer.firstName) {
          throw new Error('Fehlender Vorname bei einem Kunden')
        }
        if (!customer.lastName) {
          throw new Error('Fehlender Nachname bei einem Kunden')
        }
        if (!customer.gender) {
          throw new Error('Fehlendes Geschlecht bei einem Kunden')
        }

        return {
          id: customer.id || null,
          firstName: customer.firstName,
          lastName: customer.lastName,
          birthDate: customer.birthDate || null,
          gender: customer.gender
        }
      })
    } else if (type === 'readings') {
      // Validiere jedes Ablesungsobjekt
      return data.map(reading => {
        if (!reading.dateOfReading) {
          throw new Error('Fehlendes Ablesungsdatum bei einer Ablesung')
        }
        if (!reading.meterId) {
          throw new Error('Fehlende Zähler-ID bei einer Ablesung')
        }
        if (reading.meterCount === undefined || reading.meterCount === null) {
          throw new Error('Fehlender Zählerstand bei einer Ablesung')
        }
        if (!reading.kindOfMeter) {
          throw new Error('Fehlende Zählerart bei einer Ablesung')
        }

        // Normalisiere die Ablesungsdaten
        const result = {
          id: reading.id || null,
          dateOfReading: reading.dateOfReading,
          meterId: reading.meterId,
          substitute: reading.substitute === true || reading.substitute === 'true',
          meterCount: parseFloat(reading.meterCount),
          kindOfMeter: reading.kindOfMeter,
          comment: reading.comment || null
        }


        // Normalisiere die Kundendaten, falls vorhanden
        if (reading.customer) {
          result.customer = {
            id: reading.customer.id,
            firstName: reading.customer.firstName,
            lastName: reading.customer.lastName,
            birthDate: reading.customer.birthDate || null,
            gender: reading.customer.gender
          }
        }

        return result
      })
    } else {
      throw new Error(`Unbekannter Datentyp: ${type}`)
    }
  }
}