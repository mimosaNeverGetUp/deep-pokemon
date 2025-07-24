<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref, watch} from "vue";
import Team from "@/components/Team.vue";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import {Dex} from '@pkmn/dex';
import LoadingIcon from "@/views/LoadingIcon.vue";

const props = defineProps({
  teamId: {
    type: String,
    required: true
  },
  teamTier: {
    type: String,
    required: true
  }
});

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const teamInfo = ref()
const loading = ref(true);
const loadFail = ref(false);
const isLoadSimilarTeam = ref(false);

async function queryTeam(teamId, teamTier) {
  loading.value = true;
  teamInfo.value = null
  loadFail.value = false;

  const res = await fetch(`${apiUrl}/api/team/${teamId}?replayNum=30&format=${teamTier}`, {
        method: "GET"
      }
  )
  try {
    if (res.ok) {
      teamInfo.value = await res.json();
      loading.value = false;

      // 让每支队伍里面的宝可梦按同种顺序排列，避免混乱
      sortPokemons(teamInfo.value.pokemons);
      sortPokemons(teamInfo.value.teamSet.pokemons);

      if (teamInfo.value.similarTeams?.length > 0) {
        for (let similarTeam of teamInfo.value.similarTeams) {
          sortPokemons(similarTeam.pokemons);
        }
      }

    } else {
      loading.value = false;
      loadFail.value = true;
    }
  } catch (e) {
    console.log("query team fail")
    loading.value = false;
    loadFail.value = true;
  } finally {
    isLoadSimilarTeam.value = teamId !== props.teamId;
  }
}

function sortPokemons(pokemons) {
  pokemons.sort((a, b) => {
    return a.name.localeCompare(b.name)
  });
}

function getPokemonNameColor(pokemon) {
  let species = Dex.forGen(9).species.get(pokemon);
  if (!species) {
    return "";
  }
  return getTypeColor(species.types);
}

function getTypeColor(Types) {
  if (Types?.length === 0) {
    return "";
  }
  let type = Types[0];
  switch (type) {
    case "Steel":
      return "text-[#B8B8D0]";
    case "Water":
      return "text-[#6890F0]";
    case "Bug":
      return "text-[#A8B820]";
    case "Dark":
      return "text-[#705848]";
    case "Dragon":
      return "text-[#7038F8]";
    case "Fairy":
      return "text-[#EE99AC]";
    case "Electric":
      return "text-[#F8D030]";
    case "Fighting":
      return "text-[#C03028]";
    case "Fire":
      return "text-[#F08030]";
    case "Flying":
      return "text-[#A890F0]";
    case "Ghost":
      return "text-[#705898]";
    case "Grass":
      return "text-[#78C850]";
    case "Ground":
      return "text-[#E0C068]";
    case "Ice":
      return "text-[#98D8D8]";
    case "Normal":
      return "text-[#A8A878]";
    case "Poison":
      return "text-[#A040A0]";
    case "Psychic":
      return "text-[#F85888]"
    case "Rock":
      return "text-[#B8A038]";
  }
}

function getPlayerUrl(data) {
  if (data.battleId?.includes("smogtours")) {
    return `/player-record?name=${data.playerName.toLowerCase()}&tourPlayer=true`;
  }

  return `/player-record?name=${data.playerName}`;
}

function getPokemonAbilityText(pokemon, abilities) {
  if (uniquePokemonAbility(pokemon)) {
    return uniquePokemonAbility(pokemon);
  }

  return abilities?.length === 0 ? "???" : abilities[0];
}

function uniquePokemonAbility(pokemon) {
  let abilities = Dex.forGen(9).species.get(pokemon)?.abilities;
  if (abilities && Object.keys(abilities).length === 1) {
    let abilityKey = Object.keys(abilities)[0];
    return abilities[abilityKey];
  }
  return null;
}

queryTeam(props.teamId, props.teamTier);

watch(() => [props.teamId, props.teamTier], async ([newTeamId, newTeamTier]) => {
  await queryTeam(newTeamId, newTeamTier);
});
</script>

<template>
  <div v-if="teamInfo" v-show="loading===false && loadFail===false">
    <!--team-->
    <div>
      <i v-if="isLoadSimilarTeam" class="mr-2 pi pi-backward cursor-pointer"
         @click="queryTeam(props.teamId, props.teamTier)"/>
      <a class="font-bold">{{$t('team')}}</a>
    </div>
    <div class="flex items-center gap-1">
      <Team :team="teamInfo" :compact="true" :teamSet="teamInfo?.teamSet"></Team>
      <a class="ml-2" target="_blank" v-if="teamInfo?.pokepasts?.length > 0"
         v-for="pokepast in teamInfo?.pokepasts"
         :href="pokepast.url">
        <i class="pi pi-link" style="color: darkblue"></i>
      </a>
    </div>
    <Accordion>
      <AccordionTab :header="$t('export')" :headerStyle='{"font-weight": 700}'>
        <div class="">
          <div v-for="pokemon in teamInfo?.teamSet.pokemons" class="flex gap-1">
            <div class="">
              <div class="min-w-80">
                <span :class="getPokemonNameColor(pokemon.name)">{{ pokemon.name }}</span>
                <span v-if="pokemon.items" class="">
                  {{ " @ " + (pokemon.items?.length === 0 ? "???" : pokemon.items[0]) }}
                </span>
              </div>
              <div>
                {{ "Ability: " + getPokemonAbilityText(pokemon.name, pokemon.abilities) }}
              </div>
              <div class="flex items-center gap-1">
                <span> {{ "Tera Type: " + (pokemon.teraTypes?.length === 0 ? "???" : pokemon.teraTypes[0]) }}</span>
              </div>
              <div v-if="pokemon.moves && pokemon.moves.length !==0">
                <div v-for="move in pokemon.moves.slice(0, 4)">
                  {{ "-" + move }}
                </div>
                <br/>
              </div>
              <br v-else>
            </div>

            <div class="select-none font-light">
              <p v-if="pokemon.moves.length > 4 || pokemon.items.length > 1" class="font-bold">{{ $t('alternative sets')}}</p>
              <p v-for="item in pokemon.items.slice(1, pokemon.items.length)" class="font-light">
                {{ "@" + item }}
              </p>
              <p v-if="pokemon.teraTypes.length > 1" class="font-light">
                {{ "Tera Type: " + pokemon.teraTypes.slice(1, pokemon.teraTypes.length) }}
              </p>
              <p v-if="pokemon.abilities.length > 1" class="font-light">
                {{ "Ability: " + pokemon.abilities.slice(1, pokemon.abilities.length) }}
              </p>
              <p v-for="move in pokemon.moves.slice(4, pokemon.moves.length)" class="font-light">
                {{ "-" + move }}
              </p>
            </div>
          </div>
        </div>
      </AccordionTab>
    </Accordion>

    <!--similar teams-->
    <p class="font-bold mt-3">{{$t('similar teams')}}</p>
    <span v-if="!teamInfo.similarTeams || teamInfo.similarTeams.length ===0">NA</span>
    <div v-else class="mt-2" v-for="similarTeam in teamInfo.similarTeams">
      <div class="flex items-center gap-1">
        <Team :team="similarTeam" :compact="true" :teamSet="similarTeam?.teamSet"></Team>
        <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
           @click="queryTeam(similarTeam.id.data, props.teamTier)"/>
        <a class="ml-2" target="_blank" v-if="similarTeam.pokepasts?.length > 0"
           v-for="pokepast in similarTeam.pokepasts"
           :href="pokepast.url">
          <i class="pi pi-link" style="color: darkblue"></i>
        </a>
      </div>
    </div>

    <!--recent replays-->
    <p class="font-bold mt-3">{{$t('recent replays')}}</p>
    <DataTable :value="teamInfo.teams" sortField="battleDate" :sortOrder="-1" paginator :rows="10">
      <Column field="teamId" :header="$t('team')" :style="{ width:'20%'}">
        <template #body="slotProps">
          <div class="overflow-visible flex items-center gap-1">
            <Team :team="slotProps.data" :compact="true"></Team>
          </div>
        </template>
      </Column>
      <Column field="playerName" :header="$t('player name')" :style="{ width:'10%'}">
        <template #body="{data}">
          <a :href="getPlayerUrl(data)" target="_blank"
             class="dynamicThemeText">
            {{ data.playerName }}
          </a>
        </template>
      </Column>
      <Column field="battleDate" sortable :header="$t('replay date')" :style="{ width:'10%'}"/>
      <Column field="rating" sortable :header="$t('rating')" :style="{ width:'10%'}"/>
      <Column field="battle-example" :header="$t('replay')" :style="{ width:'20%'}">
        <template #body="{data}">
          <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank"
             class="dynamicThemeText">
            {{ data.battleId }}
          </a>
        </template>
      </Column>
    </DataTable>
  </div>
  <LoadingIcon v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">No results found.</p>
</template>