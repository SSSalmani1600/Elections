<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from 'vue';
import Swiper from 'swiper';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/grid';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import 'swiper/css/autoplay';

import {Autoplay, Grid, Navigation, Pagination} from "swiper/modules";
import {getParties} from "@/services/PartyService.ts";

const data = ref<string[]>([]);
const loading = ref(false);
const error = ref<string>("");

onMounted(async () => {
  loading.value = true;
  try {
    data.value = await getParties();
    loading.value = false;
  } catch (err: any) {
    console.error(err.message)
    error.value = "Er konden geen partijen gevonden worden!";
  } finally {
    loading.value = false;
  }
  new Swiper('.swiper', {
    modules: [Grid, Pagination, Navigation, Autoplay],
    slidesPerView: 1,
    grid: {
      rows: 2,
      fill: 'row',
    },

    speed: 1000,
    autoplay: {
      pauseOnMouseEnter: true,
      delay: 4000,
    },
    spaceBetween: 20,
    slidesPerGroup: 1,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
      bulletElement: 'button',
      dynamicBullets: true,
      dynamicMainBullets: 3,
    },
    breakpoints: {
      1480: {
        slidesPerView: 4,
        slidesPerGroup: 4,
        grid: {
          rows: 2,
          fill: 'row',
        },
        pagination: {
          dynamicBullets: true,
          dynamicMainBullets: 3,
        }
      },
      1280: {
        slidesPerView: 3,
        slidesPerGroup: 3,
        grid: {
          rows: 2,
          fill: 'row',
        },
        pagination: {
          dynamicBullets: true,
          dynamicMainBullets: 3,
        }
      },
      1024: {
        slidesPerView: 2,
        slidesPerGroup: 2,
        grid: {
          rows: 2,
          fill: 'row',
        },
        pagination: {
          dynamicBullets: true,
          dynamicMainBullets: 3,
        }
      },
      768: {
        slidesPerView: 3,
        slidesPerGroup: 3,
        grid: {
          rows: 2,
          fill: 'row',
        },
        pagination: {
          dynamicBullets: true,
          dynamicMainBullets: 3,
        }
      },
      440: {
        slidesPerView: 2,
        slidesPerGroup: 2,
        grid: {
          rows: 2,
          fill: 'row',
        },
        pagination: {
          dynamicBullets: true,
          dynamicMainBullets: 3,
        }
      }
    }
  });
});
</script>

<template>
  <div class="flex gap-16 w-[80%] items-center justify-center max-lg:flex-col">
    <div class="w-[60%] flex flex-col gap-4 max-lg:order-1 max-lg:w-full">
      <div class="flex justify-between items-center">
        <span class="text-2xl font-bold">Partijen</span>
        <router-link to="/partijen" class="text-primary link">Bekijk alles</router-link>
      </div>
      <div class="swiper">
        <div class="swiper-wrapper">
          <router-link to="/" v-for="party in data" :key="party"
                       class="swiper-slide p-2 h-fit flex items-center justify-center bg-[#0C1532] text-white rounded-xl">
            <div class="flex justify-center items-center gap-4 overflow-hidden">
              <div class="w-[60px] h-[60px]">
                <img v-if="party % 2 == 0" src="../assets/fvd-logo.png" class="w-full h-full" alt="">
                <img v-if="party % 2 !== 0" src="../assets/d66-logo.png"
                     class="w-full h-full object-contain" alt="">
              </div>
              <span class="font-semibold truncate text-white">PvDA {{ party }}</span>
            </div>
          </router-link>
        </div>
        <div class="swiper-pagination flex gap-2! w-full items-center justify-center mt-4"></div>
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
  transition-duration: 1000ms;
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
