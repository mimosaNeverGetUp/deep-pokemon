<script setup>
import {ref} from "vue";
import Team from "@/components/Team.vue";
import Button from 'primevue/button';
import BattleStat from "@/components/BattleStat.vue";

const props = defineProps({
  playerName: String,
  data: Object,
})
const battleChartVisibility = ref(false);

function toggleBattleStatVisibility() {
  battleChartVisibility.value = !battleChartVisibility.value;
}

function battleButtonIcon() {
  return battleChartVisibility.value ? "pi pi-angle-up" : "pi pi-angle-down"
}

</script>

<template>
  <div :class="data.winner===props.playerName ? 'deco-winner': 'deco-loser' "></div>
  <div class="battle-table text-lg">
    <a style="display:block" target="_blank" :href="`https://replay.pokemonshowdown.com/${data.id}`" class="text-black">
      {{ data.id }}
    </a>
    <span>
          {{ data.date }}
    </span>
    <div class="team-match">
      <div class="team-info" v-for="team in data.teams">
              <span>
                {{
                  (team.playerName !== null && team.playerName !== undefined && team.playerName !== "")
                      ? team.playerName : "null"
                }}
              </span>
        <Team :team="team"></Team>
      </div>
    </div>
    <Button :icon="battleButtonIcon()" severity="secondary"
            @click="toggleBattleStatVisibility()" rounded text/>
    <BattleStat :data=data :player-name="playerName" v-if="battleChartVisibility"/>
  </div>
</template>

<style scoped>
.battle-table {
  text-align: center;
  margin: 0 auto;
  width: 100%;
}

.deco-winner {
  background-color: #5383E8;
  width: 6px;
}

.deco-loser {
  background-color: #E84057;
  width: 6px;
}

.team-match {
  display: flex;
  justify-content: center;
}

.team-info {
  margin: 0 80px;
}
</style>