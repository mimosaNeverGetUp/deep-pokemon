<script setup>
import Header from "@/components/Header.vue";
import Divider from "primevue/divider";

</script>

<template>
  <Header class="text-lg" show-update-date="true">
  </Header>
  <div class="main">
    <router-view></router-view>
    <footer class="footer">
      <Divider type="solid"/>
      <div class="flex">
        <span class="text-gray-600">© 2024 Mimosa</span>
        <a target="_blank" :href="`https://space.bilibili.com/37832209`" >
          <img src="/bilibili.svg" alt="bilibili" class="size-5">
        </a>
      </div>
    </footer>
  </div>

</template>

<style scoped>
.main {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.footer {
  margin-top: auto;
  justify-items: center;
  align-items: center;
  gap: 0.25rem;
}
</style>