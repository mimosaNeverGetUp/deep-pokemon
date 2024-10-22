<script setup>
import Navi from '@/components/Navi.vue'
import Sidebar from 'primevue/sidebar';
import {ref} from "vue";

const apiUrl = import.meta.env.VITE_BACKEND_URL;

defineProps({
  showUpdateDate: String
})
const updateDate = ref(null)
const visible = ref()

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
      <i class="pi pi-question-circle ml-1" @click="visible = true" />
      <Sidebar v-model:visible="visible" header="关于本站" position="right" >
        <p>一个整合<span class="font-bold">PokemonShowdown</span>相关数据及资源的网站，以<span class="font-bold">Gen9 OU</span>为主。</p>
        <br/>

        <p class="font-bold">数据更新时间：</p>
        <p>1. 每天早上9点更新Ladder</p>
        <br/>

        <p>2. 每月2号早上8点15分更新分级统计</p>
        <br/>

        <p>3. 不定期更新Tour队伍，一般在主办方更新轮次后的一天内</p>
        <br/>

        <p>4. 不定期更新Smogon Analysis、PokePaste等资源</p>
        <br/>

        <p>如有建议和反馈(特别是BUG)，欢迎通过qq或邮箱联系2070132549@qq.com</p>
        <br/>

        <p class="font-bold">特别感谢：</p>
        <p>1. <a target="_blank" href="https://github.com/pkmn/smogon">@pkmn</a>的数据API</p>
        <p>2. <a target="_blank" href="https://pschina.one/topic/2087/%E6%B1%89%E5%8C%96%E6%95%99%E7%A8%8B%E4%B8%8E%E5%8F%8D%E9%A6%88">PS China</a>的汉化脚本</p>

      </Sidebar>
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