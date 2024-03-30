<script setup>
import Chart from "primevue/chart"
const props = defineProps({
  playerName: String,
  data: Object,
})

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
    borderColor: playerName === props.playerName ? "blue" : "red",
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
</script>
<template>
  <div class="flex justify-center items-center">
    <Chart :key="data.battleID" type="line"
           :data="battleChartData(data)" :options="battleChartOption(data)"
           class="size-1/2"/>
  </div>
</template>
