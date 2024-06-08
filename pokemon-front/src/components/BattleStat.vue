<script setup>
import Chart from "primevue/chart";
import ProgressSpinner from 'primevue/progressspinner';
import {ref} from "vue";

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const battleStat = ref()
const props = defineProps({
  playerName: String,
  data: Object,
})

async function queryBattleStat(battleId) {
  const res = await fetch(`${apiUrl}/api/battle/${battleId}/stat`, {
        method: "GET"
      }
  )
  battleStat.value = await res.json();
}

function battleChartData(battle) {
  const playerNames = [];
  playerNames.push(battle.teams[0].playerName)
  playerNames.push(battle.teams[1].playerName);

  return battleStatChartDataSet(playerNames, battleStat.value)
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
    plugins: {
      legend: {
        labels: {
          filter: function (legendItem, data) {
            let label = data.datasets[legendItem.datasetIndex].label || '';
            if (typeof (label) !== 'undefined') {
              if (legendItem.datasetIndex >= 2) {
                return false;
              }
            }
            return label;
          }
        }
      }
    },
    scales: scales
  }
}

function highLightChartDataSets(players, playerHighLights) {
  const highLightChartDataSets = [];
  for (let i = 0; i < playerHighLights.length; ++i) {
    let playerHighLight = playerHighLights[i];
    let player = players[i];
    for (const turnHighLight of playerHighLight) {
      let event = turnHighLight.description;
      let pointRadius = 2;
      let pointBackgroundColor;
      let y;
      let x = turnHighLight.turn;
      if (props.playerName === player) {
        pointBackgroundColor = "blue";
        y = -100;
      } else {
        pointBackgroundColor = "red";
        y = -200;
      }

      let data = {
        pointBackgroundColor: pointBackgroundColor,
        pointHitRadius: 8,
        label: event,
        type: "scatter",
        data: [
          {
            x: x,
            y: y
          }
        ]
      };
      if (turnHighLight.type === "KILL") {
        pointRadius = 8;
        const pointIcon = new Image(20, 20);
        pointIcon.src = "/flag-fill.svg";
        data["pointStyle"] = pointIcon;
      } else if (turnHighLight.type === "SIDE") {
        pointRadius = 4;
        const pointIcon = new Image(20, 20);
        pointIcon.src = "/chevron-down.svg";
        data["pointStyle"] = pointIcon;
      } else if (turnHighLight.type === "END_SIDE") {
        pointRadius = 4;
        const pointIcon = new Image(20, 20);
        pointIcon.src = "/chevron-up.svg";
        data["pointStyle"] = pointIcon;
      }
      data["pointRadius"] = pointRadius;
      data["pointHoverRadius"] = pointRadius;
      highLightChartDataSets.push(data);
    }
  }
  return highLightChartDataSets;
}

function battleStatChartDataSet(players, battleStat) {
  const turnStats = battleStat.turnStats;
  const playerStatList = battleStat.playerStatList;
  let playerHighLights = [];
  for (let i = 0; i < players.length; ++i) {
    playerHighLights.push(playerStatList[i].highLights);
  }

  let datasets = [];
  datasets = datasets.concat(healthLineChartDataSets(players, turnStats))

  datasets = datasets.concat(highLightChartDataSets(players, playerHighLights))
  return {datasets: datasets};
}

function healthLineChartDataSets(playerNames, turnStats) {
  const healthLineChartDataSets = [];
  for (let i = 0; i < playerNames.length; ++i) {
    const playerName = playerNames[i];
    let battleTrendChartData = [];
    for (const turnStat of turnStats) {
      battleTrendChartData.push({
        x: turnStat.turn,
        y: turnStat.turnPlayerStatList[i].totalHealth
      });
    }
    healthLineChartDataSets.push({
      label: playerName === "" ? "null" : playerName,
      data: battleTrendChartData,
      borderColor: playerName === props.playerName ? "blue" : "red",
      borderWidth: 1,
      pointRadius: 2
    });
  }
  return healthLineChartDataSets;
}

queryBattleStat(props.data.battleID)
</script>
<template>
  <div class="flex justify-center items-center">
    <Chart v-if="battleStat" :key="data.battleID" type="line"
           :data="battleChartData(data)" :options="battleChartOption(data)"
           class="size-3/4"/>
    <ProgressSpinner v-else/>
  </div>
</template>