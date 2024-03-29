<script setup>
import {ref} from "vue";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Team from '@/components/Team.vue'
import Button from 'primevue/button';
import Chart from 'primevue/chart';

const props = defineProps({
  name: {
    type: String,
    required: true
  }
});

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const battleList = ref()
const totalRecords = ref();
const battleListChartVisibility = ref();
const battleListButtonIcon = ref();
const page = ref(0);
const row = ref(25);


async function queryBattle(page, row) {
  const res = await fetch(`${apiUrl}/api/player/${props.name}/battle?page=${page}&row=${row}`, {
        method: "GET"
      }
  )
  const response = await res.json();
  battleList.value = response.data;
  totalRecords.value = response.totalRecords;
  battleListChartVisibility.value = {};
  battleListButtonIcon.value = {};
  for (let i = 0; i < battleList.value.length; ++i) {
    let id = battleList.value[i].battleID;
    battleListChartVisibility.value[id] = false;
    battleListButtonIcon.value[id] = "pi pi-angle-down"
  }
}

function toggleChartVisibility(id) {
  battleListChartVisibility.value[id] = !battleListChartVisibility.value[id];
  battleListButtonIcon.value[id] =
      battleListButtonIcon.value[id] === "pi pi-angle-down" ? "pi pi-angle-up" : "pi pi-angle-down"
}

function onPage(event) {
  queryBattle(event.page, event.rows);
}

function rowStyle(row) {
  const backgroundColor = row.winner === props.name ? '#a3cfec' : '#e2b6b3';
  return {backgroundColor: backgroundColor, margin: 0};
}

function battleChartData(battle) {
  const playerNames = [];
  playerNames.push(battle.teams[0].playerName)
  playerNames.push(battle.teams[1].playerName);
  const battleHealthLineTrends = JSON.parse(battle.healthLinePairJsonString);

  return battleStatChartDataSet(playerNames, battleHealthLineTrends)
}

function battleChartOption(battle) {
  const battleHealthLineTrends = JSON.parse(battle.healthLinePairJsonString);
  const turnLength = battleHealthLineTrends[0].length;
  const scales = {
    x: {
      type: "linear",
      ticks: {
        min: 0,
        max: turnLength,
        stepSize: 1,
      }
    }
  }
  return {
    animations: false,
    animation: {
      duration: 0 // 一般动画时间
    },
    hover: {
      animationDuration: 0 // 悬停项目时动画的持续时间
    },
    responsiveAnimationDuration: 0,// 调整大小后的动画持续时间
    legend: {
      display: false
    }, scales: scales
  }
}

function battleStatChartDataSet(players, battleHealthLineTrends) {
  return {datasets: healthLineChartDataSets(players, battleHealthLineTrends)};
}

function healthLineChartData(playerName, healthLineTrend) {
  let battleTrendChartData = []
  for (let turn = 0; turn < healthLineTrend.length; turn++) {
    if (turn === 0) {
      battleTrendChartData.push({
        x: 0,
        y: 600
      });
    }
    let health = 0;
    let pokemonNumber = 0;
    for (const pokemon in healthLineTrend[turn]) {
      health = health + healthLineTrend[turn][pokemon];
      pokemonNumber++;
    }
    health = health + 100 * (6 - pokemonNumber);
    battleTrendChartData.push({
      x: turn + 1,
      y: health
    });
  }

  return {
    label: playerName === "" ? "null" : playerName,
    data: battleTrendChartData,
    borderColor: playerName === props.name ? "blue" : "red",
    borderWidth: 1,
    pointRadius: 2
  };
}

function healthLineChartDataSets(playerNames, battleHealthLineTrends) {
  const healthLineChartDataSets = [];
  for (let playerNumber = 0; playerNumber < battleHealthLineTrends.length; playerNumber++) {
    const healthLineTrend = battleHealthLineTrends[playerNumber];
    healthLineChartDataSets.push(healthLineChartData(playerNames[playerNumber], healthLineTrend));
  }
  return healthLineChartDataSets;
}

queryBattle(page.value, row.value);
</script>

<template>
  <DataTable :value="battleList" class="ladder" lazy paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false"
             tableStyle="min-width: 50rem" :row-style="rowStyle">
    <Column field="battle" header="battle" :style="{ display:'flex', width:'100%', padding: 0 }" :headerStyle="{
      display:
    'none'
    }">
      <template #body="{data}">
        <div :class="data.winner===props.name ? 'deco-winner': 'deco-loser' "></div>
        <div class="battle-table">
          <a style="display:block" :href="`https://replay.pokemonshowdown.com/${data.battleID}`">
            {{ data.battleID }}
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
          <Button :icon="battleListButtonIcon[data.battleID]" severity="secondary"
                  @click="toggleChartVisibility(data.battleID)" rounded text/>
          <div class="flex justify-center items-center">
            <Chart :key="data.battleID" v-if="battleListChartVisibility[data.battleID]" type="line"
                   :data="battleChartData(data)" :options="battleChartOption(data)"
                   class="size-1/2"/>
          </div>
        </div>
      </template>
    </Column>
  </DataTable>
</template>

<style scoped>
.chart {
  width: 50%;
}

.deco-winner {
  background-color: #5383E8;
  width: 6px;
}

.deco-loser {
  background-color: #E84057;
  width: 6px;
}

.battle-table {
  text-align: center;
  margin: 0 auto;
  width: 100%;
}

.team-match {
  display: flex;
  justify-content: center;
}

.team-info {
  margin: 0 80px;
}

</style>