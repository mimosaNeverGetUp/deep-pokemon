<script setup>
import Navi from '@/components/Navi.vue'
import {ref} from "vue";

const updateDate = ref("2024-02-03")

</script>

<template>
  <header>
    <Navi class="Navi"></Navi>

    <div class="global-info" v-if="updateDate">
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