<script setup>
import Avatar from 'primevue/avatar';
import {ref} from "vue";

const props = defineProps({
  name: {
    type: String,
    required: true
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

function getRandomPlayerImage() {
  return Math.random() >= 0.5 ? "Jirachi.jpg" : "Sprigatito.jpg"
}

queryPlayer();
</script>

<template>
  <div class="player-bar">
    <Avatar :image="getRandomPlayerImage()"
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
  width: 128px;
  height: 128px;
}

.player-info {
  margin-left: 15px;
}
</style>