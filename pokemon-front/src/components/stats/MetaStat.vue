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
  <div class="flex justify-center items-center gap-4 flex-wrap">
    <MetaStatCard metric="Date" :value="getYearMonth(meta?.date)"></MetaStatCard>
    <MetaStatCard metric="Battle" :value="convertString(meta?.total)"></MetaStatCard>
    <MetaStatCard metric="Offense" :value="convertPercentageString(meta?.tags['offense'])"></MetaStatCard>
    <MetaStatCard metric="Balance" :value="convertPercentageString(meta?.tags['balance'])"></MetaStatCard>
    <MetaStatCard metric="HO" :value="convertPercentageString(meta?.tags['hyperoffense'])"></MetaStatCard>
    <MetaStatCard metric="Stall" :value="convertPercentageString(meta?.tags['stall'])"></MetaStatCard>
    <MetaStatCard metric="Sun" :value="convertPercentageString(meta?.tags['sun'])"></MetaStatCard>
    <MetaStatCard metric="Hail" :value="convertPercentageString(meta?.tags['hail'])"></MetaStatCard>
    <MetaStatCard metric="Rain" :value="convertPercentageString(meta?.tags['rain'])"></MetaStatCard>
    <MetaStatCard metric="Sand" :value="convertPercentageString(meta?.tags['sand'])"></MetaStatCard>
    <MetaStatCard metric="Trickroom" :value="convertPercentageString(meta?.tags['trickroom'])"></MetaStatCard>
  </div>

</template>