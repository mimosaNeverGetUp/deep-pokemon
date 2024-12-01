<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import InputText from 'primevue/inputtext';
import {ref} from "vue";
import Button from 'primevue/button';
import BattleStat from "@/components/BattleStat.vue";
import ProgressSpinner from "primevue/progressspinner";

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const input = ref();
const battle = ref();
const load = ref()

async function queryBattle() {
  if (input.value) {
    if (!input.value.includes("gen9") || input.value.includes("vgc")) {
      return;
    }
    load.value = false;
    battle.value = null;
    let battleId = input.value.includes("/") ? input.value.substring(input.value.lastIndexOf('/') + 1) : input.value;
    const res = await fetch(`${apiUrl}/api/battle/${battleId}`, {
          method: "GET"
        }
    )
    battle.value = await res.json();
  }
  load.value = true;
}

</script>

<template>
  <div class="mt-[100px] min-w-max">
    <InputText class="w-1/2" type="text" v-model="input"
               placeholder="paste replay url or battle id (only support gen9 singles game)"/>
    <Button label="Submit" @click="queryBattle"/>
    <BattleStat class="min-w-[1020px]" :data=battle :player-name="battle.winner" v-if="battle"/>
    <ProgressSpinner v-else-if="load === false"/>
  </div>
</template>