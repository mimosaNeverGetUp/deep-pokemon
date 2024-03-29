<script setup>

const props = defineProps({
  url: String
})

</script>
<template>
  <div>
    <router-link class="navi-item" :to="url">
      <slot>

      </slot>
    </router-link>
  </div>
</template>

<style scoped>
.navi-item {
  color: white;
  height: 100%;
  display: flex;
  padding: 14px 16px;
}

</style>