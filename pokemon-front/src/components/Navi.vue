<script setup>
import NaviItem from '@/components/NaviItem.vue'
</script>

<template>
  <nav>
    <NaviItem url="/">
      <img class="navi-logo" src="@/assets/pokemonicon/pokemon.png" alt="deep-pokemon">
    </NaviItem>

    <NaviItem url="/list">
      排行榜
    </NaviItem>

    <NaviItem url="/team">
      队伍
    </NaviItem>

    <NaviItem url="/stat">
      宝可梦
    </NaviItem>
  </nav>
</template>

<style scoped>
nav {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.navi-logo {
  height: 30px;
  width: 30px;
  object-fit: cover;
}
</style>