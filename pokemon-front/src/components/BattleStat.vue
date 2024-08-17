<script setup>
import {ref} from "vue";
import Chart from "primevue/chart";
import ProgressSpinner from 'primevue/progressspinner';
import TabView from 'primevue/tabview';
import TabPanel from 'primevue/tabpanel';
import Column from "primevue/column";
import DataTable from "primevue/datatable";

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const battleStat = ref()
const playerPokemonBattleStat = ref()
const load = ref()
const props = defineProps({
  playerName: String,
  data: Object,
})

async function queryBattleStat(battleId) {
  const res = await fetch(`${apiUrl}/api/battle/${battleId}/stat`, {
        method: "GET"
      }
  )
  if (!res.ok) {
    load.value = false;
    return;
  }

  let stat = await res.json();
  battleStat.value = stat;
  let pokemonStat = [];
  for (const playerStat of stat.playerStatList) {
    for (const pokemonName in playerStat.pokemonBattleStats) {
      playerStat.pokemonBattleStats[pokemonName].playerName = playerStat.playerName
      pokemonStat.push(playerStat.pokemonBattleStats[pokemonName])
    }
  }
  playerPokemonBattleStat.value = pokemonStat;
  load.value = true
}

function getIconUrl(pokemonName) {
  const iconName = pokemonName.replace(" ", "").replace("-*", "")
  return "pokemonicon/" + iconName + ".png"
}

function battleChartData(battle) {
  const playerNames = [];
  playerNames.push(battle.teams[0].playerName)
  playerNames.push(battle.teams[1].playerName);

  return battleStatChartDataSet(playerNames, battleStat.value)
}

function battleChartOption(battleStat) {
  const turnLength = battleStat.turnStats.length;
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
      if (props.playerName.toLowerCase() === player.toLowerCase()) {
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
        const killPokemonIcon = new Image(30, 22);
        let pokemon = turnHighLight.description.split("kill")[0].trim();
        killPokemonIcon.src = getIconUrl(pokemon);
        killPokemonIcon.onerror = function () {
          killPokemonIcon.src = "pokemonicon/null.png";
        };
        data["pointStyle"] = killPokemonIcon;

        // build faint pokemon
        let faintPokemon = turnHighLight.description.split("opponent")[1].split(" by ")[0].trim();
        let opponentY = -300 - y;
        let opponentPointColor = pointBackgroundColor === "blue" ? "red" : "blue";
        let faintPokemonPointData = buildPointData(opponentPointColor, event, x, opponentY);
        const faintPokemonIcon = new Image(30, 22);
        createTransparentImage(getIconUrl(faintPokemon), 0.3, faintPokemonIcon)
        faintPokemonIcon.onerror = function () {
          faintPokemonIcon.src = "pokemonicon/null.png";
        };
        faintPokemonPointData["pointStyle"] = faintPokemonIcon;
        highLightChartDataSets.push(faintPokemonPointData);

        // build kill event data
        let killEventPointData = buildPointData("black", event, x, -150);
        const killEventIcon = new Image(20, 20);
        killEventIcon.src = "kill.png";
        killEventPointData["pointStyle"] = killEventIcon;
        highLightChartDataSets.push(killEventPointData);

      } else if (turnHighLight.type === "SIDE") {
        pointRadius = 4;
        const sideIcon = new Image(15, 15);
        let side = turnHighLight.description.split("side")[1].split("start")[0].trim();
        if (side === "Stealth Rock") {
          sideIcon.src = "side/rock.png";
        } else if (side === "Spikes") {
          sideIcon.src = "side/caltrop.png";
        } else if (side === "Toxic Spikes") {
          sideIcon.src = "side/poisoncaltrop.png";
        } else {
          continue;
        }
        data["pointStyle"] = sideIcon;
      } else if (turnHighLight.type === "END_SIDE") {
        pointRadius = 4;
        const sideIcon = new Image(15, 15);
        let side = turnHighLight.description.split("side")[1].split("end")[0].trim();
        let sideIconSrc;
        if (side === "Stealth Rock") {
          sideIconSrc = "side/rock.png";
        } else if (side === "Spikes") {
          sideIconSrc = "side/caltrop.png";
        } else if (side === "Toxic Spikes") {
          sideIconSrc = "side/poisoncaltrop.png";
        } else {
          continue;
        }
        createTransparentImage(sideIconSrc, 0.3, sideIcon)
        data["pointStyle"] = sideIcon;
      }
      data["pointRadius"] = pointRadius;
      data["pointHoverRadius"] = pointRadius;
      highLightChartDataSets.push(data);
    }
  }
  return highLightChartDataSets;
}

function buildPointData(pointBackgroundColor, event, x, y) {
  return {
    pointBackgroundColor: pointBackgroundColor,
    pointHitRadius: 8,
    pointRadius: 8,
    pointHoverRadius: 8,
    label: event,
    type: "scatter",
    data: [
      {
        x: x,
        y: y
      }
    ]
  };
}

function createTransparentImage(imageSrc, opacity, pointImage) {
  const img = new Image();
  img.src = imageSrc;
  let canvas = document.getElementById('canvas');
  if (!canvas) {
    canvas = document.createElement('canvas');
  }
  img.onload = function () {
    const ctx = canvas.getContext('2d');
    canvas.width = 30;
    canvas.height = 22;

    ctx.globalAlpha = opacity;
    ctx.drawImage(img, 0, 0, 30, 22);
    pointImage.src = canvas.toDataURL();
  }
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
      borderColor: playerName.toLowerCase() === props.playerName.toLowerCase() ? "blue" : "red",
      borderWidth: 1,
      pointRadius: 2
    });
  }
  return healthLineChartDataSets;
}

function rowStyle(row) {
  const backgroundColor = props.data.winner === row.playerName ? '#a3cfec' : '#e2b6b3';
  return {backgroundColor: backgroundColor, margin: 0};
}

queryBattleStat(props.data.id)
</script>
<template>
  <div>
    <TabView v-if="load === true">
      <TabPanel header="trend" headerClass="text-lg w-1/2 text-black">
        <div class="flex justify-center items-center">
          <Chart :key="data.battleID" type="line"
                 :data="battleChartData(data)" :options="battleChartOption(battleStat)"
                 class="size-3/4"/>
        </div>
      </TabPanel>
      <TabPanel header="stat" headerClass="text-lg w-1/2 text-black">
        <DataTable :value="playerPokemonBattleStat" class="ladder" :scrollable="false"
                   tableStyle="min-width: 50rem" :row-style="rowStyle">
          <Column field="name" header="pokemon" :style="{ width:'10%'}">
            <template #body="{data}">
              <img :src="getIconUrl(data.name)" :alt="data.name"/>
              <span>{{ data.name }}</span>
            </template>
          </Column>
          <Column field="playerName" header="player" :style="{ width:'5%' }"></Column>
          <Column field="switchCount" header="switch" :sortable="true" :style="{ width:'5%' }"></Column>
          <Column field="moveCount" header="move" :sortable="true" :style="{ width:'5%' }"></Column>
          <Column field="killCount" header="kill" :sortable="true" :style="{ width:'5%' }"></Column>
          <Column field="healthValue" header="正负值" :sortable="true" :style="{ width:'5%' }"></Column>
          <Column field="attackValue" header="进攻贡献值" :sortable="true" :style="{ width:'5%' }"></Column>

        </DataTable>
      </TabPanel>
    </TabView>
    <ProgressSpinner v-else-if="load === null || load === undefined"/>
    <span v-else-if="load === false">load stat fail😢</span>
  </div>
</template>

<style>
.p-tabview-panels {
  padding: 0;
}
</style>