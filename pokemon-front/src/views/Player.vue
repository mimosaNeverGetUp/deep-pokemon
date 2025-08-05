<script setup>
import Header from "@/components/Header.vue";
import Battles from "@/components/Battles.vue";
</script>

<template>
  <Header></Header>
  <div class="max-lg:w-full lg:w-3/4 mx-auto">
    <Battles :playerName="$route.query.name"
             :tourPlayer="$route.query.tourPlayer==='true'"></Battles>
  </div>
</template>