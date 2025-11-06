<script lang="ts" setup>
import {nextTick, onMounted, onUnmounted, ref} from 'vue';
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

  await nextTick();

  new Swiper('.swiper', {
    modules: [Grid, Pagination, Navigation, Autoplay],
    slidesPerView: 1,
    grid: {
      rows: 2,
      fill: 'row',
    },

    speed: 1000,
    // autoplay: {
    //   pauseOnMouseEnter: true,
    //   delay: 4000,
    // },
    spaceBetween: 20,
    slidesPerGroup: 1,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
      bulletElement: 'button',
      dynamicBullets: true,
      dynamicMainBullets: 3,
    },
    navigation: {
      prevEl: '.swiper-button-prev',
      nextEl: '.swiper-button-next'
    },
    breakpoints: {
      1920: {
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
      440: {
        slidesPerView: 1,
        slidesPerGroup: 1,
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
    <div class="w-[60%] flex flex-col relative min-h-[164px] gap-4 max-lg:order-1 max-lg:w-full">
      <div class="flex justify-between items-center">
        <span class="text-2xl font-bold">Partijen</span>
        <router-link to="/partijen" class="text-primary link">Bekijk alles</router-link>
      </div>
      <span v-show="error !== ''"
            class="text-lg bg-background py-4 px-8 rounded-lg shadow-lg text-primary absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center w-full md:w-fit">{{
          error
        }}</span>
      <div class="relative">
        <div class="swiper">
          <div class="swiper-wrapper overflow-y-auto">
            <router-link :to="{path: '/partij/' + party}" v-for="(party, index) in data"
                         :key="party"
                         class="swiper-slide p-4 px-10 h-fit w-full flex items-center justify-between bg-background text-white rounded-xl z-20">
              <div class="flex items-center gap-4 overflow-hidden w-full">
                <div class="w-[40px] h-[40px] shrink-0">
                  <img v-if="index % 2 == 0" src="../assets/fvd-logo.png"
                       class="w-full h-full object-contain" alt="">
                  <img v-if="index % 2 !== 0" src="../assets/d66-logo.png"
                       class="w-full h-full object-contain" alt="">
                </div>
                <span class="font-semibold truncate text-white">{{ party }}</span>
              </div>
            </router-link>
          </div>

        </div>
        <div class="swiper-button-prev navigation-btn"><i class="pi pi-angle-left"></i></div>
        <div class="swiper-button-next navigation-btn"><i class="pi pi-angle-right"></i></div>

        <div class="swiper-pagination"></div>
      </div>
    </div>
    <img src="../assets/party-slide-img.svg" width="400" alt="">
  </div>
</template>

<style scoped>
.swiper {
  width: 100%;
}

.swiper-pagination {
  display: flex;
  gap: 4px;
  position: absolute;
  bottom: -25px;
  left: 50%;
  transform: translateX(-50%);
  justify-content: center;

}

:deep(.navigation-btn svg) {
  display: none !important;
}

.navigation-btn {
  position: absolute;
  color: white;
  cursor: pointer;
  transform: translateY(-50%);
  bottom: 50%;
  width: 28px;
  height: 28px;
  margin-top: 0;
}

.navigation-btn i {
  font-size: 24px !important;
}

.swiper-button-next {
  right: -40px;
}

.swiper-button-prev {
  left: -40px;
}

:deep(.swiper-pagination-bullet) {
  background: white;
  opacity: 0.5;
  transition-duration: 1000ms;
  min-width: 7px;
}

:deep(.swiper-pagination-bullet-active) {
  opacity: 1;
}

.swiper-slide {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
