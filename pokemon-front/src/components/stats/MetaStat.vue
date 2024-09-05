<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import MetaStatCard from '@/components/stats/MetaStatCard.vue';
import {ref} from "vue";

const props = defineProps({
  format: String
})
const apiUrl = import.meta.env.VITE_BACKEND_URL;
const meta = ref(null)

async function fetchMeta() {
  const res = await fetch(`${apiUrl}/api/stats/${props.format}/meta`, {
        method: "GET"
      }
  )
  meta.value = await res.json()
}

function convertString(data) {
  if (data instanceof String) {
    return (data * 100).toFixed(2) + "%";
  }
  return data ? data.toString() : "NA";
}


function convertPercentageString(data) {
  return data ? (data * 100).toFixed(2) + "%" : "NA";
}

function getYearMonth(dateStr) {
  if (!dateStr) {
    return "NA"
  }
  let date =new Date(dateStr)
  let monthNumber = date.getMonth() + 1;
  return date.getFullYear() + "-" + monthNumber.toString().padStart(2, "0");
}

fetchMeta();
</script>

<template>
  <div class="flex gap-4 flex-nowrap">
    <MetaStatCard class="ml-auto min-w-32" metric="Date" :value="getYearMonth(meta?.date)"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Battle" :value="convertString(meta?.total)"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Offense" :value="convertPercentageString(meta?.tags['offense'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Balance" :value="convertPercentageString(meta?.tags['balance'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="HO" :value="convertPercentageString(meta?.tags['hyperoffense'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Stall" :value="convertPercentageString(meta?.tags['stall'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Sun" :value="convertPercentageString(meta?.tags['sun'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Hail" :value="convertPercentageString(meta?.tags['hail'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Rain" :value="convertPercentageString(meta?.tags['rain'])"></MetaStatCard>
    <MetaStatCard class="min-w-28" metric="Sand" :value="convertPercentageString(meta?.tags['sand'])"></MetaStatCard>
    <MetaStatCard class="mr-auto min-w-32" metric="Trickroom" :value="convertPercentageString(meta?.tags['trickroom'])"></MetaStatCard>
  </div>

</template>