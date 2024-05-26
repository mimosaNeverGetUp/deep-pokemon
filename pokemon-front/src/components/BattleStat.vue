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
  const battleHighLights = JSON.parse(battle.highLightJsonString);

  return battleStatChartDataSet(playerNames, battleHealthLineTrends, battleHighLights)
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

function highLightChartDataSets(players, battleHighLights) {
  const highLightChartDataSets = [];
  for (let i = 0; i < battleHighLights.length; ++i) {
    let battleHighLight = battleHighLights[i];
    let player = players[i];
    for (let j = 0; j < battleHighLight.length; ++j) {
      let event = battleHighLight[j];
      let pointRadius = 2;
      let pointBackgroundColor;
      let y;
      let x = j + 1;
      if (props.playerName === player) {
        pointBackgroundColor = "blue";
        y = -100;
      } else {
        pointBackgroundColor = "red";
        y = -200;
      }

      let data = {
        pointBackgroundColor: pointBackgroundColor,
        pointHitRadiu: 8,
        label: event,
        type: "scatter",
        data: [
          {
            x: x,
            y: y
          }
        ]
      };
      if (event.indexOf("faint") !== -1) {
        pointRadius = 8;
        const pointIcon = new Image(20,20);
        pointIcon.src = "/flag-fill.svg";
        data["pointStyle"] = pointIcon;
      } else if (event.indexOf("Stealth Rock") !== -1) {
        pointRadius = 4;
      } else if (event.indexOf("(") !== -1) {
        pointRadius = 4;
      } else if (event.indexOf("Spikes") !== -1) {
        pointRadius = 4;
      } else if (event.indexOf("Toxic Spikes") !== -1) {
        pointRadius = 4;
      } else if (event.indexOf("Defog") !== -1) {
        pointRadius = 4;
      } else if (event.indexOf("Rapid Spin") !== -1) {
        pointRadius = 4;
      }
      data["pointRadius"] = pointRadius;
      data["pointHoverRadius"] = pointRadius;
      highLightChartDataSets.push(data);
    }
  }
  return highLightChartDataSets;
}

function battleStatChartDataSet(players, battleHealthLineTrends, battleHighLights) {
  let datasets = [];
  datasets = datasets.concat(healthLineChartDataSets(players, battleHealthLineTrends))

  datasets = datasets.concat(highLightChartDataSets(players, battleHighLights))
  return {datasets: datasets};
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
           class="size-3/4"/>
  </div>
</template>