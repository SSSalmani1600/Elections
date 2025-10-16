<script lang="ts" setup>
import {onMounted} from 'vue';
import Swiper from 'swiper';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/grid';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import 'swiper/css/autoplay';

import {Autoplay, Grid, Navigation, Pagination} from "swiper/modules";

onMounted(() => {
  new Swiper('.swiper', {
    modules: [Grid, Pagination, Navigation, Autoplay],
    slidesPerView: 4,
    grid: {
      rows: 2,
      fill: 'row',
    },
    autoplay: {
      pauseOnMouseEnter: true,
      delay: 3000,
    },
    spaceBetween: 20,
    slidesPerGroup: 4,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
      bulletElement: 'button',
    },
  });
});
</script>

<template>
  <div class="flex gap-16 w-[80%] items-center justify-center">
    <div class="w-[60%] flex flex-col gap-4">
      <div class="flex justify-between items-center">
        <span class="text-2xl font-bold">Partijen</span>
        <router-link to="/partijen" class="text-primary underline">Bekijk alles</router-link>
      </div>
      <div class="swiper">
        <div class="swiper-wrapper">
          <div v-for="n in 18" :key="n"
               class="swiper-slide flex items-center justify-center bg-[#0C1532] text-white rounded-xl p-4">
            <div class="flex justify-center items-center gap-4 overflow-hidden">
              <div class="w-[50px] h-[50px]">
                <img src="../assets/partij-img.svg" class="w-full h-full" alt="">
              </div>
              <span class="font-semibold text-ellipsis">PvDA {{ n }}</span>
            </div>
          </div>
        </div>
        <div class="swiper-pagination mt-4"></div>
      </div>
    </div>
    <img src="../assets/party-slide-img.svg" width="400" alt="">
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
