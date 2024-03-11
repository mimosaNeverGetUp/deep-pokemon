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
  display: block;
  padding: 14px 16px;
}



.navi-href {
  text-decoration: none;
}

.navi-href:hover {
  background-color: #111;
}
</style>