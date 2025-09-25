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
  <div class="m-auto flex flex-col items-center justify-center">
    <div>
      <img :src="getRandomGif()" alt="loading..." class="loadingIcon bg-gray-200 p-3">
    </div>
    <p class="font-sans">{{$t('loading')}}</p>
  </div>
</template>

<style>
.loadingIcon {
  border: 1px solid;
  border-radius: 50%;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
}
</style>