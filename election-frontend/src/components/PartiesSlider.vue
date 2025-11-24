<script lang="ts" setup>
import { nextTick, onMounted, ref } from 'vue';
import Swiper from 'swiper';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/grid';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import 'swiper/css/autoplay';

import { Autoplay, Grid, Navigation, Pagination } from "swiper/modules";
import { getParties } from "@/services/PartyService.ts";
import type {SwiperEvents} from "swiper/types";
import type { Party2 } from '@/types/api.ts'
import { getWikipediaPartyData } from '@/services/WikipediaService.ts'

const data = ref<Party2[]>([]);
const partyWithImg = ref<{ name: string; img: string;}[]>([])
const loading = ref(false);
const error = ref<string>("");

onMounted(async () => {
  loading.value = true;
  try {
    data.value = Array.from(await getParties());
    const promises = data.value.map(async (party) => {
      const wikiInfo = await getWikipediaPartyData(party.name);
      return {
        name: party.name,
        img: wikiInfo?.img || '',
      }
    })
    partyWithImg.value = await Promise.all(promises)
  } catch (err: any) {
    console.error(err.message);
    error.value = "Er konden geen partijen gevonden worden!";
  } finally {
    loading.value = false;
  }

  await nextTick();

  const swiper = new Swiper('.swiper', {
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
    loop: true,
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
      2130: {slidesPerView: 4, slidesPerGroup: 4, grid: {rows: 2, fill: 'row'}},
      1480: {slidesPerView: 3, slidesPerGroup: 3, grid: {rows: 2, fill: 'row'}},
      1024: {slidesPerView: 2, slidesPerGroup: 2, grid: {rows: 2, fill: 'row'}},
      768: {slidesPerView: 2, slidesPerGroup: 2, grid: {rows: 2, fill: 'row'}}
    },
  });

  const rootEl = swiper.el as HTMLElement;

  const setSlideFocusable = (slide: HTMLElement, focusable: boolean) => {
    if (focusable) {
      slide.removeAttribute('tabindex');
      slide.removeAttribute('inert');
      slide.setAttribute('aria-hidden', 'false');

      slide.tabIndex = 0;
    } else {
      slide.setAttribute('tabindex', '-1');
      slide.setAttribute('inert', '');
      slide.setAttribute('aria-hidden', 'true');
    }

    slide.querySelectorAll<HTMLElement>('a')
      .forEach(el => {
        if (focusable) el.removeAttribute('tabindex');
        else el.setAttribute('tabindex', '-1');
      });
  };

  const io = new IntersectionObserver(
    entries => {
      entries.forEach(entry => {
        const slide = entry.target as HTMLElement;
        const isVisible = entry.isIntersecting && entry.intersectionRatio > 0.5;
        setSlideFocusable(slide, isVisible);
      });
    },
    {
      root: rootEl,
      threshold: 0.6,
    }
  );

  swiper.slides.forEach(slide => io.observe(slide as HTMLElement));

  setTimeout(() => {
    swiper.slides.forEach(slide => {
      const rect = (slide as HTMLElement).getBoundingClientRect();
      const rootRect = rootEl.getBoundingClientRect();
      const horizontallyOverlaps =
        rect.left < rootRect.right && rect.right > rootRect.left;
      const verticallyOverlaps =
        rect.top < rootRect.bottom && rect.bottom > rootRect.top;
      setSlideFocusable(slide as HTMLElement, horizontallyOverlaps && verticallyOverlaps);
    });
  }, 0);


  const events: Array<keyof SwiperEvents> = ['slideChange', 'transitionEnd', 'resize']
  events.forEach(evt => {
    swiper.on(evt, () => {

    });
  });
  swiper.init?.();
});
</script>

<template>
  <div class="flex gap-16 w-[80%] items-center mx-4 max-lg:flex-col">
    <div class="w-[60%] flex flex-col relative min-h-[164px] gap-4 max-lg:order-1 max-lg:w-full">
      <div class="flex justify-between items-center">
        <span class="text-2xl font-bold">Partijen</span>
        <router-link to="/partijen" class="text-primary link">Bekijk alles</router-link>
      </div>

      <span
        v-show="error !== ''"
        class="text-lg bg-background py-4 px-8 rounded-lg shadow-lg text-primary absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center w-full md:w-fit"
      >
        {{ error }}
      </span>

      <div class="relative">
        <div class="swiper">
          <div class="swiper-wrapper">
            <router-link
              :to="{ path: '/partij/' + party.name }"
              v-for="(party) in partyWithImg"
              :key="party"
              class="swiper-slide p-4 px-10 h-fit w-full flex items-center justify-between bg-background text-white rounded-xl z-20"
            >
              <div class="flex items-center gap-4 overflow-hidden w-full">
                <div class="w-[40px] h-[40px] shrink-0">
                  <img
                    :src="party.img"
                    class="w-full h-full object-contain"
                    alt=""
                  />
                </div>
                <span class="font-semibold truncate text-white">{{ party.name }}</span>
              </div>
            </router-link>
          </div>
        </div>

        <button class="swiper-button-prev navigation-btn" aria-label="Vorige slide">
          <i class="pi pi-angle-left"></i>
        </button>
        <button class="swiper-button-next navigation-btn" aria-label="Volgende slide">
          <i class="pi pi-angle-right"></i>
        </button>

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
  gap: 4px;
  position: absolute;
  bottom: -25px;
  left: 50%;
  transform: translateX(-50%);
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
  transition-duration: 200ms;
}

.navigation-btn:active {
  color: #EF3054;
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
