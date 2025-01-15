<script setup>
import Avatar from 'primevue/avatar';
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
    <Avatar :image="getPlayerIcon()"
            class="player-avatar"/>
    <div class="player-info">
      <p style="font-weight:bold">
        {{ player?.name }}
      </p>
      <p>
        {{ player?.elo }}
      </p>
      <p>
        {{ "排行第" + player?.rank + "位" }}
      </p>
      <p text="'Gxe: ' + ${playerRank.getGxe()}">
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
  justify-content: flex-start;
}

.player-avatar {
  width: 86px;
  height: 90px;
}

.player-info {
  margin-left: 15px;
}
</style>