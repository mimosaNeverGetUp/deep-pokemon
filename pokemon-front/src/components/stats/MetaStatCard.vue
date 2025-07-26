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
      <p class="text-sm  text-center text-gray-500">{{ $t(metric) }}</p></template>
    <template #content>
      <p class="text-center text-sm">{{ value }}</p>
    </template>
  </Card>
</template>