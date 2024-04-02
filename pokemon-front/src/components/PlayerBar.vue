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
  const res = await fetch(`${apiUrl}/api/player/${props.name}`, {
        method: "GET"
      }
  )
  player.value = await res.json()
}

queryPlayer();
</script>

<template>
  <div class="player-bar">
    <Avatar image="src/assets/pokemonicon/touxiang.png"
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
      <p th:text="'Gxe: ' + ${playerRank.getGxe()}">
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
  width: 120px;
  height: 130px;
}

.player-info {
  margin-left: 15px;
}
</style>