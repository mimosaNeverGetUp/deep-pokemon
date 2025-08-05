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
    <p v-if="props.tourPlayer" class="font-sans font-bold text-black">{{ data.tourId + ' ' + data.stage }}</p>
    <a style="display:block" target="_blank" :href="`https://replay.pokemonshowdown.com/${data.id}`"
       class="text-black underline">
      {{ data.id }}
    </a>
    <p class="text-black">{{ data.date }}</p>
    <div class="team-match flex justify-center items-center max-lg:flex-col max-lg:gap-8">
      <div class="lg:mx-20 mx-6">
              <span class="text-black">
                {{(data.teams[0].playerName !== null && data.teams[0].playerName !== undefined && data.teams[0].playerName !== "")
                      ? data.teams[0].playerName : "null" }}
              </span>
        <Team :team="data.teams[0]" :compact="false"></Team>
      </div>
      <p class="text-gray-500 text-lg font-serif"> vs. </p>
      <div class="lg:mx-20 mx-6">
              <span class="text-black">
                {{(data.teams[1].playerName !== null && data.teams[1].playerName !== undefined && data.teams[1].playerName !== "")
                  ? data.teams[1].playerName : "null" }}
              </span>
        <Team :team="data.teams[1]" :compact="false"></Team>
      </div>
    </div>
    <Button :icon="battleButtonIcon()" severity="secondary" @click="toggleBattleStatVisibility()" rounded text/>
    <div v-if="battleChartVisibility" class="overflow-x-scroll max-lg:w-screen">
      <BattleStat :data=data :player-name="data.winner" />
    </div>
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
</style>