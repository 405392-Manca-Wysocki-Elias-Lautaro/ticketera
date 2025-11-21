import { Event } from '@/types/Event'

export interface CSVExportData {
  title: string
  category: string
  venue: string
  address: string
  city: string
  state: string
  country: string
  startDateTime: string
  endDateTime: string
  minPrice: string
  currency: string
  totalTickets: number
  totalCapacity: number
  status: string
}

/**
 * Convierte un array de eventos a formato CSV
 */
export function convertEventsToCSV(events: Event[]): string {
  if (!events || events.length === 0) {
    return ''
  }

  // Definir las cabeceras del CSV
  const headers = [
    'Título',
    'Categoría',
    'Lugar',
    'Dirección',
    'Ciudad',
    'Estado/Provincia',
    'País',
    'Fecha y Hora de Inicio',
    'Fecha y Hora de Fin',
    'Precio Mínimo',
    'Moneda',
    'Tickets Disponibles',
    'Capacidad Total',
    'Estado'
  ]

  // Convertir eventos a filas CSV
  const rows = events.map(event => {
    const startDate = new Date(event.startsAt)
    const endDate = new Date(event.endsAt)
    
    const formatDateTime = (date: Date) => {
      return date.toLocaleString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      })
    }

    const minPriceFormatted = (event.minPriceCents / 100).toLocaleString('es-ES', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    })

    return [
      `"${event.title.replace(/"/g, '""')}"`, // Escapar comillas dobles
      `"${event.categoryName}"`,
      `"${event.venueName}"`,
      `"${event.addressLine}"`,
      `"${event.city}"`,
      `"${event.state}"`,
      `"${event.country}"`,
      formatDateTime(startDate),
      formatDateTime(endDate),
      minPriceFormatted,
      event.currency,
      event.totalAvailableTickets,
      event.totalCapacity,
      `"${event.status}"`
    ]
  })

  // Combinar cabeceras y filas
  const csvContent = [headers.join(','), ...rows.map(row => row.join(','))].join('\n')
  
  return csvContent
}

/**
 * Descarga un archivo CSV con los eventos
 */
export function downloadEventsCSV(events: Event[], filename?: string): void {
  const csvContent = convertEventsToCSV(events)
  
  if (!csvContent) {
    console.warn('No hay eventos para exportar')
    return
  }

  // Crear el archivo CSV con BOM para soporte de caracteres especiales
  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
  
  // Crear enlace de descarga
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', filename || `eventos_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  
  // Agregar al DOM, hacer clic y remover
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  // Limpiar URL del objeto
  URL.revokeObjectURL(url)
}

/**
 * Genera un nombre de archivo basado en la fecha actual
 */
export function generateCSVFilename(prefix: string = 'eventos'): string {
  const now = new Date()
  const dateStr = now.toISOString().split('T')[0] // YYYY-MM-DD
  const timeStr = now.toTimeString().split(' ')[0].replace(/:/g, '-') // HH-MM-SS
  
  return `${prefix}_${dateStr}_${timeStr}.csv`
}
