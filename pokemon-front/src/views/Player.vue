<script setup>
import Header from "@/components/Header.vue";
import PlayerBar from "@/components/PlayerBar.vue";
import Battles from "@/components/Battles.vue";
</script>

<template>
  <Header></Header>
  <div class="w-5/6 mx-auto">
    <PlayerBar :name="$route.query.name"></PlayerBar>
    <Battles class="mx-auto w-full" :playerName="$route.query.name"
             :tourPlayer="$route.query.tourPlayer==='true'"></Battles>
  </div>
</template>