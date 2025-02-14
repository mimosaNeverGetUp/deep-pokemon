<!--
  -  MIT License
  -
  -  Copyright (c) 2025-2025 mimosa
  -->
<script setup>
function getRandomGif() {
  let random = Math.random();
  // if (random <= 0.33) {
  //   return "/gif/clefable.gif";
  // } else if (random <= 0.66) {
  //   return "/gif/blissey.gif";
  // } else {
  //   return "/gif/alcremie-caramel-swirl-love.gif";
  // }
  return "/gif/alcremie-caramel-swirl-love.gif";
}
</script>

<template>
  <div class="m-auto w-full">
    <div class="w-full text-center">
      <img :src="getRandomGif()" alt="loading...">
    </div>
    <p class="font-bold font-sans text-center">Happy Valentine's Day> <</p>
  </div>
</template>