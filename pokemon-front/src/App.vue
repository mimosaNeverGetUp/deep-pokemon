<script setup>
import Header from "@/components/Header.vue";
import Divider from "primevue/divider";
import { usePrimeVue } from 'primevue/config';

const PrimeVue = usePrimeVue();
if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
  PrimeVue.changeTheme('aura-light-green', 'aura-dark-green', 'theme-link', () => {});
}

</script>

<template>
  <Header class="text-lg" show-update-date="true">
  </Header>
  <div class="main">
    <router-view></router-view>
    <footer class="footer">
      <Divider type="solid"/>
      <div class="flex">
        <span class="text-gray-600">
          © 2024
          <a target="_blank" :href="`https://space.bilibili.com/37832209`" >
            <span class="text-[#80DAF6]">Mimosa</span>
          </a>
        </span>


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
  align-items: center;
  gap: 0.25rem;
}
</style>