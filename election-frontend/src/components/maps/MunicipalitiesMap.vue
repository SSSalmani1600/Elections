<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import L, { Layer, type LeafletMouseEvent } from 'leaflet'
import type { Feature, GeoJsonProperties, Geometry } from 'geojson'

defineProps<{
  year: string
}>()
const mapEl = ref<string | HTMLElement>('')

let geojsonLayer: L.GeoJSON
let map: L.Map

const DATA_URL = 'https://cartomap.github.io/nl/wgs84/gemeente_2025.geojson'

function baseStyle() {
  return {
    weight: 0.8,
    color: '#93a4bf', // softer outline on dark bg
    fillColor: '#5aa9e6', // calm blue
    fillOpacity: 0.12,
  }
}

const emit = defineEmits(['update:selectedMunicipality'])

function changeMunicipality(municipalityName: string): void {
  emit('update:selectedMunicipality', municipalityName)
}

function getName(props: GeoJsonProperties) {
  const candidates = ['statnaam']
  for (const k of candidates) if (props?.[k]) return props[k]
  return 'Gemeente'
}

function onEachFeature(feature: Feature<Geometry>, layer: Layer) {
  const name = getName(feature.properties)

  layer.bindTooltip(name, {
    sticky: true,
    direction: 'auto',
    opacity: 0.95,
    offset: [0, -4],
    className: 'muni-tooltip', // custom class (styled below)
  })

  layer.on({
    mouseover: (e: LeafletMouseEvent) => {
      const l = e.target
      l.setStyle({
        weight: 1.6,
        color: '#e2e8f0', // brighter outline on hover
        fillOpacity: 0.28,
      })
      if (l._path) l._path.classList.add('hovered-muni')
      // l.bringToFront(); // optional: usually not needed anymore
    },
    mouseout: (e: LeafletMouseEvent) => {
      geojsonLayer.resetStyle(e.target)
      const l = e.target
      if (l._path) l._path.classList.remove('hovered-muni')
    },
    click: () => {
      changeMunicipality(name)
    },
  })
}

async function load_shapefile() {
  const response = await fetch(DATA_URL)
  const shape_obj = await response.json()
  console.log(shape_obj)
  return shape_obj
}

async function main() {
  const json = await load_shapefile()
  geojsonLayer = L.geoJSON(json, {
    style: baseStyle,
    onEachFeature,
  }).addTo(map)
}

onMounted(async () => {
  map = L.map(mapEl.value as HTMLElement, {
    zoomControl: true,
    attributionControl: false,
    dragging: false,
    scrollWheelZoom: false,
    doubleClickZoom: false,
    boxZoom: false,
    keyboard: false,
    tap: false,
    touchZoom: false,
    preferCanvas: false,
  }).setView([52.2, 5.3], 7)

  const nlBounds = L.latLngBounds([50.5, 3.2], [53.7, 7.3])
  // "Viscosity" makes the map resist leaving the bounds
  map.setMaxBounds(nlBounds.pad(0.5))
    ; (map as L.Map).options.maxBoundsViscosity = 0.9

  const container = map.getContainer()

  container.tabIndex = 0

  const enableInteractions = () => {
    map.dragging.enable()
    map.scrollWheelZoom.enable()
    map.touchZoom.enable()
    map.doubleClickZoom.enable()
  }
  const disableInteractions = () => {
    map.dragging.disable()
    map.scrollWheelZoom.disable()
    map.touchZoom.disable()
    map.doubleClickZoom.disable()
  }

  // Turn interactions on when pointer enters, off when it leaves
  container.addEventListener('mouseenter', enableInteractions)
  container.addEventListener('mouseleave', disableInteractions)

  // (Mobile) when a gesture starts inside and ends outside, stop it at the edge
  container.addEventListener('touchend', disableInteractions)
  container.addEventListener('touchcancel', disableInteractions)

  // Basemap
  try {
    main()
  } catch (err: unknown) {
    console.error('Failed to load GeoJSON', err)
  }
})

onUnmounted(() => {
  if (map) map.remove()
})
</script>

<template>
  <div class="w-full rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm shadow-lg">
    <!-- Header -->
    <div class="px-4 py-3 border-b border-white/10 flex items-center justify-between">
      <h3 class="text-sm font-semibold text-white/90">Gemeentenkaart</h3>
      <span class="text-xs text-white/60">{{ year }}</span>
    </div>

    <!-- Map container -->
    <div class="p-3">
      <!-- responsive height by breakpoint; full width -->
      <div ref="mapEl" class="w-full h-[360px] sm:h-[420px] md:h-[460px] lg:h-[520px] rounded-xl overflow-hidden" />
    </div>
  </div>
</template>

<style scoped>
/* Make Leaflet canvas transparent, consistently */
:deep(.leaflet-container) {
  background: transparent !important;
}

:deep(.leaflet-container .leaflet-interactive:focus) {
  outline: none;
}

/* Pointer + smooth transforms */
:deep(path.leaflet-interactive) {
  cursor: pointer;
  transition:
    transform 140ms ease,
    fill-opacity 140ms ease,
    stroke 140ms ease;
  transform-box: fill-box;
  transform-origin: center;
}

/* Subtle scale on hover */
:deep(.hovered-muni) {
  transform: scale(1.02);
}

/* Tooltip styling (Leaflet’s .leaflet-tooltip + our class) */
:deep(.leaflet-tooltip.muni-tooltip) {
  background: rgba(15, 23, 42, 0.9);
  /* slate-900/90 */
  color: #e2e8f0;
  /* slate-200 */
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 6px 8px;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}
</style>
