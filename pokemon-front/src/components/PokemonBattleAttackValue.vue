<!--
  -  MIT License
  -
  -  Copyright (c) 2025-2025 mimosa
  -->
<script setup>
import OverlayPanel from 'primevue/overlaypanel';
import {ref} from "vue";

const props = defineProps({
  data: Object
})

const op = ref();
const toggle = (event) => {
  op.value.toggle(event);
};

function getDamageTargetStatsMap(battleDamageStats) {
  if (!battleDamageStats) {
    return
  }

  let damageTargetStatsMap = {};
  for (const battleDamageStat of battleDamageStats) {
    if (damageTargetStatsMap[battleDamageStat.damageTarget]) {
      damageTargetStatsMap[battleDamageStat.damageTarget].push(battleDamageStat);
    } else {
      let damageTargetStats = [];
      damageTargetStats.push(battleDamageStat);
      damageTargetStatsMap[battleDamageStat.damageTarget] = damageTargetStats;
    }
  }
  return damageTargetStatsMap;
}

function getTotalDamage(damageTargetStats) {
  let total = 0;
  for (const damageTargetStat of damageTargetStats) {
    total += damageTargetStat.damage;
  }
  return total + "%";
}

function getIconUrl(pokemonName) {
  const iconName = pokemonName.replace(" ", "").replace("-*", "")
  return "pokemonicon/" + iconName + ".png"
}

</script>
<template>
  <div class="set-tip text-black">
    <span @mouseover="toggle" @mouseleave="toggle">{{ data.attackValue }}</span>
    <OverlayPanel ref="op" pt:content:class="bg-[#F0F0F0E6]" v-if="data.battleDamageStats.length >0">
      <div class="text-left text-black">
        <div v-for="(damageTargetStats, pokemon) in getDamageTargetStatsMap(data.battleDamageStats)">
          <img :src="getIconUrl(pokemon)" :alt="pokemon" :title="pokemon"/>
          <span> {{ getTotalDamage(damageTargetStats) }}</span>
          <p v-for="battleDamageStat in damageTargetStats" class="flex gap-2">
            <span>{{ battleDamageStat.triggerCount }}</span>
            <span>{{ "x" }}</span>
            <span>{{ battleDamageStat.damageFrom }}</span>
            <span>{{ battleDamageStat.damage + "%" }}</span>
          </p>
        </div>
      </div>
    </OverlayPanel>
  </div>
</template>