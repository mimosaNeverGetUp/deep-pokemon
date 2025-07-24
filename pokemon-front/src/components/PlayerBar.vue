<script setup>
import {avatar} from "@/components/data/avatar.js";

import {ref} from "vue";

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  icon: {
    type: String,
    required: false
  },
  tourPlayer: {
    type: Boolean,
    required: false,
    default: false
  }
});

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const player = ref()

async function queryPlayer() {
  let encodePlayerName = encodeURIComponent(props.name);
  const res = await fetch(`${apiUrl}/api/player/${encodePlayerName}`, {
        method: "GET"
      }
  )
  player.value = await res.json()
}

function getDefaultPlayerImage() {
  return "unknownf.png";
}

function getPlayerIcon() {
  if (!props.icon) {
    return getDefaultPlayerImage();
  }

  if (props.icon in avatar) {
    return `https://play.pokemonshowdown.com/sprites/trainers/${avatar[props.icon]}.png`;
  }

  if (props.icon.charAt(0) === '#') {
    return `https://play.pokemonshowdown.com/sprites/trainers-custom/${props.icon.substr(1)}.png`;
  }

  return `https://play.pokemonshowdown.com/sprites/trainers/${props.icon}.png`;
}

queryPlayer();
</script>

<template>
  <div class="player-bar">
    <img :src="getPlayerIcon()"
         class="player-avatar bg-slate-400"/>
    <div class="player-info">
      <p class="font-bold text-2xl font-serif	">
        {{ player?.name }}
      </p>
      <p v-if="!tourPlayer">
        {{ "Elo: " + player?.elo }}
      </p>
      <p v-if="!tourPlayer">
        {{ $t('player rank', {rank: player?.rank}) }}
      </p>
      <p v-if="!tourPlayer" text="'Gxe: ' + ${playerRank.getGxe()}">
        {{ "Gxe: " + player?.gxe }}
      </p>
    </div>
  </div>

</template>

<style scoped>
.player-bar {
  margin-top: 60px;
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: flex-start;
}

.player-avatar {
  width: 86px;
  height: 90px;
  border: 5px solid rgb(148 163 184);
  border-radius: 50%;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
}

.player-info {
  margin-left: 15px;
}
</style>