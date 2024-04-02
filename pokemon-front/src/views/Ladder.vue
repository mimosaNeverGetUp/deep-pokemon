<script setup>
import {ref} from "vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Team from '@/components/Team.vue'


// 在需要使用后端 URL 的地方
const apiUrl = import.meta.env.VITE_BACKEND_URL;
const rank = ref(null)
const page = ref(0);
const row = ref(25);
const totalRecords = ref(null);

async function fetchData(page, row) {
  rank.value = null
  const res = await fetch(`${apiUrl}/api/rank?page=${page}&row=${row}`, {
        method: "GET"
      }
  )
  const response = await res.json()
  rank.value = response.data
  totalRecords.value = response.totalRecords
}

function onPage(event) {
  fetchData(event.page, event.rows)
}

fetchData(page.value, row.value)
</script>

<template>
  <DataTable :value="rank" class="ladder" lazy paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false" stripedRows
             tableStyle="min-width: 50rem">
    <Column field="rank" header="排名"
            :style="{ width:'5%' }"></Column>
    <Column field="name" header="玩家名" :style="{ width:'20%' }">
      <template #body="{data}">
        <a :href="`/player-record?name=${data.name}`">
          {{data.name}}
        </a>
      </template>
    </Column>
    <Column field="elo" header="elo" :style="{ width:'5%' }"></Column>
    <Column field="gxe" header="gxe" :style="{ width:'5%' }"></Column>
    <Column field="recentTeam" header="最近使用队伍" :style="{ width:'30%' }">
      <template #body="{data}">
        <div class="team-list">
          <Team v-for="team in data.recentTeam" :team="team"></Team>
        </div>
      </template>
    </Column>
  </DataTable>
</template>

<style scoped>

/*排行榜表格样式*/
.ladder {
  width: 90%;
  margin: 60px auto 0; /*表格下移以适应绝对定位的导航栏*/
}

.team-list {
  border: 0;
  margin: 0;
}
</style>
