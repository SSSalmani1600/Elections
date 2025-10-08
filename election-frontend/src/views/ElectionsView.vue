<template>
    <section class="text-white px-6 py-16 md:py-24">
        <div class="max-w-[100rem] mx-auto grid md:grid-cols-2 items-center gap-12 lg:gap-20">
            <!-- Tekst -->
            <div>
                <h1 class="text-4xl sm:text-5xl lg:text-6xl xl:text-6xl font-extrabold leading-tight tracking-tight">
                    Ontdek de verkiezingen<br />
                    in Nederland
                </h1>

                <p class="mt-6 text-lg sm:text-xl lg:text-2xl text-white/85 max-w-xl leading-relaxed">
                    Bekijk wanneer verkiezingen plaatsvinden, leer over de thema’s en
                    zie de resultaten van eerdere verkiezingen.
                </p>

                <div class="mt-8 flex flex-wrap gap-4">
                    <button type="button"
                        class="bg-[#ff4d61] font-semibold px-6 py-4 rounded-2xl text-base sm:text-lg shadow-md hover:shadow-lg hover:bg-[#ff334c] transition">
                        Komende verkiezingen
                    </button>
                    <button type="button"
                        class="bg-white text-black font-semibold px-6 py-4 rounded-2xl text-base sm:text-lg shadow hover:bg-gray-100 transition">
                        Uitslagen voorgaande jaren
                    </button>
                </div>
            </div>

            <!-- Afbeelding -->
            <div class="flex justify-center md:justify-end">
                <img src="/src/assets/img/elections-image.png" alt="Illustratie van verkiezingen"
                    class="w-full max-w-lg lg:max-w-xl xl:max-w-2xl" />
            </div>
        </div>
    </section>
</template>
