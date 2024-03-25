<script setup>
import Navi from '@/components/Navi.vue'
import {ref} from "vue";
const apiUrl = import.meta.env.VITE_BACKEND_URL;

defineProps({
  showUpdateDate: String
})
const updateDate = ref(null)

async function queryUpdateDate() {
  const res = await fetch(`${apiUrl}/api/rank/update-time`, {
        method: "GET"
      }
  )
  const response = await res.json()
  updateDate.value = response.date
}

queryUpdateDate()
</script>

<template>
  <header>
    <Navi class="Navi"></Navi>

    <div class="global-info" v-if="showUpdateDate">
      <p>{{ "更新日期: " + updateDate }}</p>
    </div>
  </header>
</template>

<style>
header {
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  z-index: 1;
  background-color: #333;
  justify-content: space-between;
  width: 100%;
}

.global-info {
  color: white;
  display: flex;
  align-items: center;
  margin-right: 15px;
}
</style>