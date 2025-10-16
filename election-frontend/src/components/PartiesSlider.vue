<script lang="ts" setup>
import { onMounted } from 'vue';
import Swiper from 'swiper';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/grid';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import {Grid, Navigation, Pagination} from "swiper/modules";

onMounted(() => {
  new Swiper('.swiper', {
    modules: [Grid, Pagination, Navigation],
    slidesPerView: 4,
    grid: {
      rows: 2,
      fill: 'row',
    },
    spaceBetween: 20,
    slidesPerGroup: 4,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
    },
  });
});
</script>

<template>
  <div class="swiper w-full">
    <div class="swiper-wrapper">
      <div v-for="n in 18" :key="n" class="swiper-slide flex items-center justify-center bg-[#0C1532] text-white rounded-xl p-4">
        <div class="flex flex-col items-center">
          <span class="font-semibold">PvDA {{ n }}</span>
        </div>
      </div>
    </div>

    <div class="swiper-pagination mt-4"></div>
  </div>
</template>

<style scoped>
.swiper {
  width: 100%;
  padding-bottom: 2rem;
}

:deep(.swiper-pagination-bullet) {
  background: white;
  opacity: 0.5;
}
:deep(.swiper-pagination-bullet-active) {
  opacity: 1;
}

:deep(.swiper-slide) {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
