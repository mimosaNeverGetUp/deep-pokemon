<script setup>
import Header from "@/components/Header.vue";
import PlayerBar from "@/components/PlayerBar.vue";
import Battles from "@/components/Battles.vue";
</script>

<template>
  <Header></Header>

  <PlayerBar :name="$route.query.name"></PlayerBar>
  <Battles :playerName="$route.query.name" :tourPlayer="$route.query.tourPlayer==='true'"></Battles>
</template>