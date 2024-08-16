<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->
<script setup>
const props = defineProps({
  newValue: Number,
  oldValue: Number
})

function getUsageDif(newValue, oldValue) {
  return convertToPercentage(Math.abs(newValue - oldValue));
}

function convertToPercentage(f) {
  return (f * 100).toFixed(2) + '%'
}

function getDifTextClass(newValue, oldValue) {
  return newValue > oldValue ? "w-1.5 text-red-600" : "w-1.5 text-green-600";
}
</script>

<template>
  <div class="flex w-20 gap-1 items-center" v-if="props.oldValue !== undefined && props.oldValue !== props.newValue">
    <i v-if=" props.newValue > props.oldValue" class="pi pi-chevron-up" style="color: red"></i>
    <i v-else class="pi pi-chevron-down" style="color: green"></i>
    <span :class="getDifTextClass(props.newValue, props.oldValue)">{{getUsageDif(props.newValue, props.oldValue) }}</span>
  </div>
</template>