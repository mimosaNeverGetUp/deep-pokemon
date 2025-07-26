<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import Button from 'primevue/button';
import MultiSelect from 'primevue/multiselect';
import SelectButton from 'primevue/selectbutton';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import TreeSelect from 'primevue/treeselect';

import {pokemoninfo} from "@/components/data/pokemoninfo.js"
import {ref} from "vue";
import {useI18n} from 'vue-i18n'

const {t} = useI18n()

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const pokemons = ref()
const players = ref()
const selectStages = ref()
const selectTags = ref()
const selectedSort = ref("rating")
const selectedTier = ref("gen9ou")
const selectedRange = ref("Last week")
const tags = ref([
  {name: t('Offense'), value: "Offense"},
  {name: t('Balance'), value: "Balance"},
  {name: t('HO'), value: "HO"},
  {name: t('Stall'), value: "Stall"}]
);
const ranges = ref([
  {name: t('Last 3 days'), value: "Last 3 days"},
  {name: t('Last week'), value: "Last week"},
  {name: t('Last month'), value: "Last month"},
  {name: t('Last 3 months'), value: "Last 3 months"}]);
const ladderTier = ref(["gen9ou", "gen9nationaldex"]);
const sortModes = ref([
  {name: t('rating'), value: "rating"},
  {name: t('popularity'), value: "popularity"},
  {name: t('date'), value: "date"},
  {name: t('unexpectedness'), value: "unexpectedness"}])
const ladderSortModes = ref([
  {name: t('rating'), value: "rating"},
  {name: t('popularity'), value: "popularity"},
  {name: t('date'), value: "date"},
  {name: t('unexpectedness'), value: "unexpectedness"}])
const pokepastesOptions = ref([  {name: t('exist'), value: "exist"}])

const types = ref([
  {name: t('ladder'), value: "ladder"},
  {name: t('tour'), value: "tour"}]
);
const selectType = ref("ladder")
const searchTour = ref(false);
const selectTour = ref()
const selectPokepastes = ref();
const tourShortName = ref()
const tourPlaceHolder = ref(t('loading'));
const tourSortModes = ref([
    {name: t('win dif'), value: "win dif"},
    {name: t('popularity'), value: "popularity"},
    {name: t('date'), value: "date"},
    {name: t('unexpectedness'), value: "unexpectedness"}])
const tourNodes = [];
const tourPlayers = ref([]);
const tourTiers = ref([]);
const stages = ref([]);
const stagesMap = {};
let tourPlayersMap = {}
let tourTiersMap = {}

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
      tourPlaceHolder.value = t('select tour');
    } catch (e) {
      console.log("response is empty or invalid");
      tourPlaceHolder.value = t('query tour fail')
    }
  } else {
    tourPlaceHolder.value = "query tour fail."
  }
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

  let pokepaste = !!selectPokepastes.value;
  if (searchTour.value) {
    let selectPlayers = players.value ? players.value : '';
    let selectStage = selectStages.value ? selectStages.value : '';
    let tourGroupName;
    if (selectedTier.value === "gen9ou") {
      tourGroupName = "tour_" + tourShortName.value;
    } else {
      tourGroupName = "tour_" + tourShortName.value + "_" + selectedTier.value;
    }

    return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&pokepaste=${pokepaste}&range=${tourGroupName}&tour=true&players=${selectPlayers}&stages=${selectStage}&tier=${selectedTier.value}`;
  }

  let teamGroupName = getTeamGroupName(range, selectedTier.value);
  return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&pokepaste=${pokepaste}&range=${teamGroupName}&tier=${selectedTier.value}`;
}

function getTeamGroupName(range, tier) {
  let name = "";
  switch (range) {
    case "Last 3 days":
      name = "last_3_days";
      break;
    case "Last week":
      name = "last_7_days";
      break;
    case "Last month":
      name = "last_30_days";
      break;
    case "Last 3 months":
      name = "last_90_days";
      break;
  }
  if (tier !== "gen9ou") {
    name = name + "_" + tier;
  }
  return name;
}

queryAllTour();
</script>

<template>
  <div class="min-w-max">
    <div class="flex flex-col gap-2 mt-[40px]">
      <span class="text-gray-500 text-sm">{{ $t('Type') }}</span>
      <SelectButton v-model="selectType" :options="types" optionLabel="name" optionValue="value" aria-labelledby="basic"
                    @change="changeBattleType"/>
    </div>

    <div class="flex flex-col gap-2 mt-2">
      <span class="text-gray-500 text-sm">{{ $t('Tag') }}</span>
      <SelectButton v-model="selectTags" :options="tags" optionLabel="name" optionValue="value" aria-labelledby="basic"/>
      <span class="text-gray-500 text-sm">{{ $t('Sort') }}</span>
      <SelectButton v-model="selectedSort" :options="sortModes" optionLabel="name" optionValue="value" aria-labelledby="basic"/>
      <div v-if="!searchTour" class="mt-2">
        <div class="mb-2">
          <span class="text-gray-500 text-sm">{{ $t('Tier') }}</span>
          <SelectButton v-model="selectedTier" :options="ladderTier" aria-labelledby="basic"/>
        </div>
      </div>

      <div class="mt-2">
        <span v-if="!searchTour" class="text-gray-500 text-sm">{{ $t('Range') }}</span>
        <SelectButton v-if="!searchTour" v-model="selectedRange" :options="ranges" optionLabel="name" optionValue="value" aria-labelledby="basic"/>
        <div v-else>
          <div v-if="tourTiers.length >=1" class="mb-2">
            <span class="text-gray-500 text-sm">{{ $t('Tier') }}</span>
            <SelectButton v-model="selectedTier" :options="tourTiers" aria-labelledby="basic" @change="onTierChange"/>
          </div>
          <p class="items-center text-gray-500 text-sm">{{ $t('Tour') }}</p>
          <TreeSelect v-model="selectTour" filter :options="tourNodes" :placeholder="tourPlaceHolder"
                      class="w-80 mt-1.5" @node-select="onNodeSelect"/>
        </div>
      </div>
    </div>

    <Accordion class=" w-96 mt-12">
      <AccordionTab :header="$t('Advanced search')">
        <div class="flex flex-col w-full justify-start gap-2 mt-3">
          <div>
            <span class="text-gray-500 text-sm">{{ $t('pokemons') }}</span>
            <MultiSelect v-model="pokemons" :options=" Object.values(pokemoninfo).map(item => item.name)" display="chip"
                         filter
                         :placeholder="$t('select pokemons')" variant="filled"
                         class="size-auto font-normal min-w-80 min-h-9"
                         :virtualScrollerOptions="{ itemSize: 44 }"/>
          </div>

          <div v-if="searchTour">
            <p class="text-gray-500 text-sm">{{$t('include stages')}}</p>
            <MultiSelect v-model="selectStages" :options="stages"
                         display="chip" filter
                         :placeholder="t('select stages')" variant="filled" class="size-auto font-normal min-w-80 min-h-9"
                         :virtualScrollerOptions="{ itemSize: 44 }"/>

            <p class="mt-1 text-gray-500 text-sm">{{$t('include players')}}</p>
            <MultiSelect v-model="players" :options="tourPlayers"
                         display="chip" filter
                         :placeholder="t('select players')" variant="filled"
                         class="size-auto font-normal min-w-80 min-h-9"
                         :virtualScrollerOptions="{ itemSize: 44 }"/>
          </div>
          <div>
            <p class="mt-1 text-gray-500 text-sm">{{ $t('pokepaste') }}</p>
            <SelectButton v-model="selectPokepastes" :options="pokepastesOptions" optionLabel="name" optionValue="value"
                          aria-labelledby="basic"/>
          </div>
        </div>
      </AccordionTab>
    </Accordion>

    <router-link :to="getTeamSearchUrl(pokemons, selectTags, selectedRange, selectMonth, selectedSort)">
      <Button class="mt-3" icon="pi pi-search" :label="$t('Submit')"/>
    </router-link>
  </div>

</template>