<script setup>
import NaviItem from '@/components/NaviItem.vue'
</script>

<template>
  <nav>
    <NaviItem url="/">
      <img class="navi-logo" src="/pokemonicon/pokemon.png" alt="deep-pokemon">
    </NaviItem>

    <NaviItem url="/ladder">
      <img class="navi-label-icon" src="/pokemonicon/Incineroar.png" alt="deep-pokemon">
      <p>排行榜</p>
    </NaviItem>

    <NaviItem url="/teamSearch">
      <img class="navi-label-icon" src="/pokemonicon/Maushold-Four.png" alt="deep-pokemon">
      <p>队伍</p>
    </NaviItem>

    <NaviItem url="/stats?format=gen9ou">
      <img class="navi-label-icon" src="/pokemonicon/Ninetales-Alola.png" alt="deep-pokemon">
      <p>环境</p>
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

.navi-label-icon {
  height: 30px;
  width: 40px;
  margin-right:5px
}
</style>