<script setup>
import Header from "@/components/Header.vue";
import Divider from "primevue/divider";
import {usePrimeVue} from 'primevue/config';
import {useRoute} from "vue-router";

const route = useRoute();
const PrimeVue = usePrimeVue();

detectTheme();

function detectTheme() {
  let theme = "light";
  if (localStorage.getItem("theme") === "dark") {
    theme = "dark";
  } else  if (localStorage.getItem("theme") === "light") {
    theme = "light";
  }else if (window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches) {
    theme = "dark";
  }
  if (theme === "dark") {
    document.documentElement.setAttribute("page-theme", "dark");
    PrimeVue.changeTheme('aura-light-green', 'aura-dark-green', 'theme-link', () => {
    });
  }else {
    document.documentElement.setAttribute("page-theme", "light");
    PrimeVue.changeTheme('aura-dark-green', 'aura-light-green', 'theme-link', () => {
    });
  }

  if (localStorage.getItem("theme") !== theme) {
    localStorage.setItem('theme', theme);
  }
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
          © 2024-2025
            <span>mimosa</span>
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