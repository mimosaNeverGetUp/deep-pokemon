<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import Card from 'primevue/card';

const props = defineProps({
  metric: String,
  value: String
})

</script>

<template>
  <Card>
    <template #title>
      <p class="font-bold text-center">{{ metric }}</p></template>
    <template #content>
      <p>{{ value }}</p>
    </template>
  </Card>
</template>