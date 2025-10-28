<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import L, { Layer, type LeafletMouseEvent } from 'leaflet'
import type { Feature, GeoJsonProperties, Geometry } from 'geojson';

const mapEl = ref<string | HTMLElement>("")

let geojsonLayer: L.GeoJSON;
let map: L.Map;

const DATA_URL = "https://cartomap.github.io/nl/wgs84/gemeente_2025.geojson"

function baseStyle() {
  return {
    weight: 0.8,
    color: '#555',
    fillColor: '#4f7dc5',
    fillOpacity: 0.15
  }
}

function getName(props: GeoJsonProperties) {
  const candidates = ['statnaam',]
  for (const k of candidates) if (props?.[k]) return props[k]
  return 'Gemeente'
}

function onEachFeature(feature: Feature<Geometry>, layer: Layer) {
  const name = getName(feature.properties)

  layer.bindTooltip(name, {
    sticky: true,
    direction: 'center',
    opacity: 0.95,
  })

  layer.on({
    mouseover: (e: LeafletMouseEvent) => {
      const l = e.target
      l.setStyle({
        weight: 1.6,
        color: '#222',
        fillOpacity: 0.35
      })

      if (l._path) l._path.classList.add('hovered-muni')

      if (!L.Browser.ie && !L.Browser.opera && !L.Browser.edge) l.bringToFront()
    },
    mouseout: (e: LeafletMouseEvent) => {
      geojsonLayer.resetStyle(e.target)
      if (e.target._path) e.target._path.classList.remove('hovered-muni')
    },
  })
}

async function load_shapefile() {
  const response = await fetch(DATA_URL)
  const shape_obj = await response.json();
  console.log(shape_obj);
  return shape_obj;
}

async function main() {
  const json = await load_shapefile();
  geojsonLayer = L.geoJSON(json, {
    style: baseStyle,
    onEachFeature
  }).addTo(map)

  map.fitBounds(geojsonLayer.getBounds(), { padding: [20, 20] })
}

onMounted(async () => {
  // Initialize map
  map = L.map(mapEl.value, {
    minZoom: 5,
    maxZoom: 14,
    zoomControl: true,
    preferCanvas: false,
  }).setView([52.2, 5.3], 7)

  // Basemap
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)

  try {
    main()
  } catch (err: unknown) {
    console.error("Failed to load GeoJSON", err);
  }
})

onUnmounted(() => {
  if (map) {
    map.remove()
  }
})
</script>

<template>
  <div class="w-full h-[70vh] relative">

    <!-- Map container -->
    <div ref="mapEl" id="map" class="w-full h-full rounded-xl overflow-hidden"></div>
  </div>
</template>

<style scoped>
.leaflet-interactive {
  cursor: pointer;
}
</style>
