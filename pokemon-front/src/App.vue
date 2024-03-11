<script setup>
import Header from "@/components/Header.vue";
import Ladder from "@/components/Ladder.vue";
import {ref} from "vue";

const updateDate =ref("2024-02-03")
</script>

<template>
  <Header :updateDate="updateDate">
  </Header>
  <Ladder></Ladder>
</template>

<style scoped>

</style>
