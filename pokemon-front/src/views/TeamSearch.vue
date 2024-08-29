<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import Button from 'primevue/button';
import Calendar from 'primevue/calendar';
import MultiSelect from 'primevue/multiselect';
import SelectButton from 'primevue/selectbutton';

import {pokemoninfo} from "@/components/data/pokemoninfo.js"
import {ref} from "vue";

const pokemons = ref()
const selectTags = ref()
const selectedSort = ref("rating")
const selectedRange = ref("Last week")
const tags = ref(["Offense", "Balance", "HO", "Stall"]);
const ranges = ref(["Last 3 days", "Last week", "Last month", "Last 3 months"]);
const sortModes = ref(["rating", "popularity", "date"])

const selectMonth = ref(new Date())
const useMonthRange = ref(false)
const maxMonth = ref(new Date())
const minMonth = ref(new Date())
maxMonth.value.setMonth(maxMonth.value.getMonth() - 1);
minMonth.value.setMonth(9);
maxMonth.value.setFullYear(2024);

function changeShowMode() {
  useMonthRange.value = !useMonthRange.value;
}

function getTeamSearchUrl(pokemons, tags, range, month, sort) {
  if (!pokemons) {
    pokemons = '';
  }

  if (!tags) {
    tags = '';
  }

  if (useMonthRange.value) {
    let date = new Date(month);
    let monthNumber = date.getMonth() + 1;
    let monthId = date.getFullYear() + monthNumber.toString().padStart(2, "0");
    return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&range=${monthId}`;
  }
  return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&range=${range}`;
}
</script>

<template>
  <div class="flex flex-col gap-2 mt-[60px]">
    <span>包含精灵</span>
    <MultiSelect v-model="pokemons" :options=" Object.values(pokemoninfo).map(item => item.name)" display="chip" filter
                 placeholder="select pokemons" variant="filled" class="size-auto font-normal min-w-80 min-h-9"
                 :virtualScrollerOptions="{ itemSize: 44 }"/>
  </div>

  <div class="flex flex-col gap-2 mt-5">
    <span>队伍标签</span>
    <SelectButton v-model="selectTags" :options="tags" aria-labelledby="basic"/>
    <span>Sort mode</span>
    <SelectButton v-model="selectedSort" :options="sortModes" aria-labelledby="basic"/>
    <div>
      <span class="items-center text-center">Range</span>
<!--      <i class="ml-2 pi pi-calendar cursor-pointer hover:bg-green-500" style="font-size: 1.5rem"-->
<!--         @click="changeShowMode"></i>-->
    </div>

    <Calendar v-if="useMonthRange" class="w-96" v-model="selectMonth" view="month" dateFormat="dd/mm" :maxDate="maxMonth"
              :minDate="minMonth" inline/>
    <SelectButton v-else v-model="selectedRange" :options="ranges" aria-labelledby="basic"/>
  </div>
  <router-link :to="getTeamSearchUrl(pokemons, selectTags, selectedRange, selectMonth, selectedSort)">
    <Button class="mt-3" icon="pi pi-search" label="Submit"/>
  </router-link>
</template>