<script setup>
import Header from "@/components/Header.vue";
import PlayerBar from "@/components/PlayerBar.vue";
import BattleTable from "@/components/BattleTable.vue";
</script>

<template>
  <Header></Header>

  <PlayerBar :name="$route.query.name"></PlayerBar>
  <BattleTable :name="$route.query.name"></BattleTable>
</template>