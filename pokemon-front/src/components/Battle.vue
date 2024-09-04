<script setup>
import {ref} from "vue";
import Team from "@/components/Team.vue";
import Button from 'primevue/button';
import BattleStat from "@/components/BattleStat.vue";

const props = defineProps({
  playerName: String,
  data: Object,
  tourPlayer: {
    type: Boolean,
    required: false,
    default: false
  }
})
const battleChartVisibility = ref(false);

function toggleBattleStatVisibility() {
  battleChartVisibility.value = !battleChartVisibility.value;
}

function battleButtonIcon() {
  return battleChartVisibility.value ? "pi pi-angle-up" : "pi pi-angle-down";
}

function getDecorateClass(data) {
  if (props.tourPlayer) {
    return data?.winSmogonPlayerName.toLowerCase() === props.playerName.toLowerCase() ? 'deco-winner' : 'deco-loser';
  } else {
    return data?.winner.toLowerCase() === props.playerName.toLowerCase() ? 'deco-winner' : 'deco-loser';
  }
}

</script>

<template>
  <div :class="getDecorateClass(data)"></div>
  <div class="battle-table text-lg">
    <p v-if="props.tourPlayer" class="font-sans font-bold">{{ data.tourId + ' ' + data.stage }}</p>
    <a style="display:block" target="_blank" :href="`https://replay.pokemonshowdown.com/${data.id}`" class="text-black">
      {{ data.id }}
    </a>
    <p>{{ data.date }}</p>
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
    <Button :icon="battleButtonIcon()" severity="secondary" @click="toggleBattleStatVisibility()" rounded text/>
    <BattleStat :data=data :player-name="data.winner" v-if="battleChartVisibility"/>
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