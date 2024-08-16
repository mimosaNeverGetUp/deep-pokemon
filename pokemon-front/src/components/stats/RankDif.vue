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
  if (oldValue === 0) {
    return "new"
  }

  return Math.abs(oldValue - newValue);
}

function getDifTextClass(newValue, oldValue) {
  if (oldValue === 0) {
    return "w-1.5 text-red-600"
  }

  return newValue > oldValue ? "w-1.5 text-green-600" : "w-1.5 text-red-600";
}
</script>

<template>
  <div class="flex gap-1 items-center" v-if="props.oldValue !== undefined && props.oldValue !== props.newValue">
    <i v-if=" props.oldValue === 0 || props.oldValue > props.newValue" class="pi pi-chevron-up" style="color: red"></i>
    <i v-else class="pi pi-chevron-down" style="color: green"></i>
    <span :class="getDifTextClass(props.newValue, props.oldValue)">
      {{getUsageDif(props.newValue, props.oldValue)}}
    </span>
  </div>
</template>