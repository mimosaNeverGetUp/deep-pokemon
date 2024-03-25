<script setup>
import {ref} from "vue";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Team from '@/components/Team.vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  }
});

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const battleList = ref()
const totalRecords = ref();
const page = ref(0);
const row = ref(25);

async function queryBattle(page, row) {
  const res = await fetch(`${apiUrl}/api/player/${props.name}/battle?page=${page}&row=${row}`, {
        method: "GET"
      }
  )
  const response = await res.json()
  battleList.value = response.data
  totalRecords.value = response.totalRecords
}

function onPage(event) {
  queryBattle(event.page, event.rows)
}

function rowStyle(row) {
  const backgroundColor = row.winner === props.name ? '#a3cfec' : '#e2b6b3';
  return {backgroundColor: backgroundColor, margin: 0}
}

queryBattle(page.value, row.value)
</script>

<template>
  <DataTable :value="battleList" class="ladder" lazy paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false"
             tableStyle="min-width: 50rem" :row-style="rowStyle">
    <Column field="battle" header="battle" :style="{ display:'flex', width:'100%', padding: 0 }" :headerStyle="{
      display:
    'none'
    }">
      <template #body="{data}">
        <div :class="data.winner===props.name ? 'deco-winner': 'deco-loser' "></div>
        <div class="battle-table">
          <a style="display:block" :href="`https://replay.pokemonshowdown.com/${data.battleID}`">
            {{ data.battleID }}
          </a>
          <span>
          {{ data.date }}
        </span>
          <div class="team-match">
            <div class="team-info" v-for="team in data.teams">
              <span>
                {{
                  (team.playerName !== null && team.playerName !== undefined && team.playerName !== "")
                      ? team.playerName : "null"
                }}
              </span>
              <Team :team="team"></Team>
            </div>
          </div>

        </div>
      </template>
    </Column>
  </DataTable>
</template>

<style scoped>
.deco-winner {
  background-color: #5383E8;
  width: 6px;
}

.deco-loser {
  background-color: #E84057;
  width: 6px;
}

.battle-table {
  text-align: center;
  width: 100%;
}

.team-match {
  display: flex;
  justify-content: center;
}

.team-info {
  margin: 0 80px;
}

</style>