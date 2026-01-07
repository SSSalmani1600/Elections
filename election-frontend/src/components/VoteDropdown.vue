<script setup lang="ts">
import { ChevronDown } from 'lucide-vue-next';

interface Props {
  modelValue: string;
  options: string[];
  label?: string;
  accentColor?: string;
}

const {
  modelValue,
  options = [],
  label = '',
  accentColor = 'white'
} = defineProps<Props>();

const emit = defineEmits(['update:modelValue']);

const handleChange = (event: Event) => {
  const target = event.target as HTMLSelectElement;
  emit('update:modelValue', target.value);
};
</script>

<template>
  <div class="relative group">
    <select :value="modelValue" @change="handleChange"
      class="appearance-none bg-[#111827] text-white border border-slate-700 hover:border-slate-500 pl-10 pr-9 py-2.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all cursor-pointer min-w-[130px] w-full"
      :style="{ color: accentColor === 'white' ? 'white' : '#94a3b8' }">
      <option v-for="opt in options" :key="opt" :value="opt">
        {{ opt }}
      </option>
    </select>

    <!-- Icon Slot replaces the {icon} prop -->
    <div class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none">
      <slot name="icon"></slot>
    </div>

    <div
      class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none group-hover:text-white transition-colors">
      <ChevronDown :size="14" />
    </div>

    <label
      class="absolute -top-2 left-2 px-1 bg-[#0B132B] rounded-[2px] text-[10px] text-slate-500 uppercase tracking-wider font-bold">
      {{ label }}
    </label>
  </div>
</template>
