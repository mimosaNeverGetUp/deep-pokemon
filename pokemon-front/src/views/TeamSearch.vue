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
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import TreeSelect from 'primevue/treeselect';

import {pokemoninfo} from "@/components/data/pokemoninfo.js"
import {ref} from "vue";

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const pokemons = ref()
const players = ref()
const selectStages = ref()
const selectTags = ref()
const selectedSort = ref("rating")
const selectedTier = ref("gen9ou")
const selectedRange = ref("Last week")
const tags = ref(["Offense", "Balance", "HO", "Stall"]);
const ranges = ref(["Last 3 days", "Last week", "Last month", "Last 3 months"]);
const sortModes = ref(["rating", "popularity", "date"])
const ladderSortModes = ref(["rating", "popularity", "date"])

const types = ref(["ladder", "tour"]);
const selectType = ref("ladder")
const searchTour = ref(false);
const selectTour = ref()
const tourShortName = ref()
const tourPlaceHolder = ref("loading...");
const tourSortModes = ref(["win dif", "popularity", "date"])
const tourNodes = [];
const tourPlayers = ref([]);
const tourTiers = ref([]);
const stages = ref([]);
const stagesMap = {};
let tourPlayersMap = {}
let tourTiersMap = {}


const useMonthRange = ref(false)
const maxMonth = ref(new Date())
const minMonth = ref(new Date())
maxMonth.value.setMonth(maxMonth.value.getMonth() - 1);
minMonth.value.setMonth(7);
minMonth.value.setFullYear(2024);
const selectMonth = ref(minMonth)

async function queryAllTour() {
  let url = new URL(`${apiUrl}/api/tour/tours`);

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  if (res.ok) {
    try {
      let result = await res.json();
      for (let tour of result) {
        tourNodes.push({
          key: tour.shortName,
          label: tour.id,
          children: []
        });

        try {
          for (let tier in tour.tierPlayers) {
            let key = tour.shortName + "_" + tier;
            tourPlayersMap[key] = tour.tierPlayers[tier];
          }
        } catch (e) {
          console.log("init tour players fail")
        }
        stagesMap[tour.shortName] = tour.stages;
        tourTiersMap[tour.shortName] = tour.tires;
      }
      tourPlaceHolder.value = "select tour";
    } catch (e) {
      console.log("response is empty or invalid");
      tourPlaceHolder.value = "query tour fail."
    }
  } else {
    tourPlaceHolder.value = "query tour fail."
  }

}

function changeShowMode() {
  useMonthRange.value = !useMonthRange.value;
}

function changeBattleType(event) {
  if (event.value === "tour") {
    searchTour.value = true;
    selectedSort.value = "win dif"
    sortModes.value = tourSortModes.value;
  } else {
    searchTour.value = false;
    selectedSort.value = "rating"
    sortModes.value = ladderSortModes.value;
  }
}

function onNodeSelect(event) {
  tourShortName.value = event.key;
  let key = event.key + "_" + selectedTier.value;
  tourPlayers.value = tourPlayersMap[key];
  stages.value = stagesMap[event.key];
  tourTiers.value = tourTiersMap[event.key];
  selectStages.value = null;
  selectedTier.value = tourTiers.value[0];
  players.value = null;
}

function onTierChange(event) {
  let key = tourShortName.value + "_" + event.value;
  tourPlayers.value = tourPlayersMap[key];
  players.value = null;
}

function getTeamSearchUrl(pokemons, tags, range, month, sort) {
  if (!pokemons) {
    pokemons = '';
  }

  if (!tags) {
    tags = '';
  }

  if (searchTour.value) {
    let selectPlayers = players.value ? players.value : '';
    let selectStage = selectStages.value ? selectStages.value : '';
    let tourGroupName;
    if (selectedTier.value === "gen9ou") {
      tourGroupName = "tour_" + tourShortName.value;
    } else {
      tourGroupName = "tour_" + tourShortName.value + "_" + selectedTier.value;
    }
    return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&range=${tourGroupName}&tour=true&players=${selectPlayers}&stages=${selectStage}`;
  }

  if (useMonthRange.value) {
    let date = new Date(month);
    let monthNumber = date.getMonth() + 1;
    let monthId = date.getFullYear() + monthNumber.toString().padStart(2, "0");
    return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&range=${monthId}`;
  }
  return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&range=${range}`;
}

queryAllTour();
</script>

<template>
  <div class="flex flex-col gap-2 mt-[40px]">
    <span>Type</span>
    <SelectButton v-model="selectType" :options="types" aria-labelledby="basic" @change="changeBattleType"/>
  </div>

  <div class="flex flex-col gap-2 mt-2">
    <span>Tag</span>
    <SelectButton v-model="selectTags" :options="tags" aria-labelledby="basic"/>
    <span>Sort</span>
    <SelectButton v-model="selectedSort" :options="sortModes" aria-labelledby="basic"/>
    <div v-if="!searchTour" class="mt-2">
      <span class="items-center text-center">Range</span>
      <i class="ml-2 pi pi-calendar cursor-pointer hover:bg-green-500" style="font-size: 1.5rem"
         @click="changeShowMode"></i>
    </div>

    <div class="mt-2">
      <Calendar v-if="useMonthRange && !searchTour" class="w-96" v-model="selectMonth" view="month" dateFormat="dd/mm"
                :maxDate="maxMonth"
                :minDate="minMonth" inline/>
      <SelectButton v-else-if="!searchTour" v-model="selectedRange" :options="ranges" aria-labelledby="basic"/>
      <div v-else>
        <div v-if="tourTiers.length >=1" class="mb-2">
          <span>tier</span>
          <SelectButton v-model="selectedTier" :options="tourTiers" aria-labelledby="basic" @change="onTierChange"/>
        </div>
        <p class="items-center">Tour</p>
        <TreeSelect v-model="selectTour" filter :options="tourNodes" :placeholder="tourPlaceHolder"
                    class="w-80 mt-1.5" @node-select="onNodeSelect"/>
      </div>
    </div>
  </div>

  <Accordion class=" w-96 mt-12">
    <AccordionTab header="Advanced search">
      <div class="flex flex-col w-full justify-start gap-2 mt-3">
        <div>
          <span>pokemons</span>
          <MultiSelect v-model="pokemons" :options=" Object.values(pokemoninfo).map(item => item.name)" display="chip"
                       filter
                       placeholder="select pokemons" variant="filled" class="size-auto font-normal min-w-80 min-h-9"
                       :virtualScrollerOptions="{ itemSize: 44 }"/>
        </div>

        <div v-if="searchTour">
          <span>stages</span>
          <MultiSelect v-model="selectStages" :options="stages"
                       display="chip" filter
                       placeholder="select stages" variant="filled" class="size-auto font-normal min-w-80 min-h-9"
                       :virtualScrollerOptions="{ itemSize: 44 }"/>

          <span>players</span>
          <MultiSelect v-model="players" :options="tourPlayers"
                       display="chip" filter
                       placeholder="select players" variant="filled" class="size-auto font-normal min-w-80 min-h-9"
                       :virtualScrollerOptions="{ itemSize: 44 }"/>
        </div>
      </div>
    </AccordionTab>
  </Accordion>

  <router-link :to="getTeamSearchUrl(pokemons, selectTags, selectedRange, selectMonth, selectedSort)">
    <Button class="mt-3" icon="pi pi-search" label="Submit"/>
  </router-link>
</template>