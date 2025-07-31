<script setup>
import NaviItem from '@/components/NaviItem.vue'
</script>

<template>
  <nav>
    <NaviItem url="/ladder">
      <img class="max-sm:w-[32px] max-sm:h-[24px]" src="/pokemonicon/Kingambit.png" alt="deep-pokemon">
      <p class="font-sans max-sm:text-base">{{$t('Ladder')}}</p>
    </NaviItem>

    <NaviItem url="/teamSearch">
      <img class="max-sm:w-[32px] max-sm:h-[24px]" src="/pokemonicon/Maushold-Four.png" alt="deep-pokemon">
      <p class="font-sans max-sm:text-base">{{$t('Teams')}}</p>
    </NaviItem>

    <NaviItem url="/stats?format=gen9ou">
      <img class="max-sm:w-[32px] max-sm:h-[24px]" src="/pokemonicon/Ninetales-Alola.png" alt="deep-pokemon">
      <p class="font-sans max-sm:text-base">{{ $t('Stats')}}</p>
    </NaviItem>
  </nav>
</template>

<style scoped>
nav {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
</style>