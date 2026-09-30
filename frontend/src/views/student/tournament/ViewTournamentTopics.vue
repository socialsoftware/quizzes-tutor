<template>
  <div class="tournament-topics">
    <v-chip v-for="topic in tournament.topics" :key="String(topic)" size="small">
      {{ topic }}
    </v-chip>
  </div>
</template>

<script setup lang="ts">
import Tournament from '@/models/user/Tournament';

defineProps<{
  tournament: Tournament;
}>();
</script>

<style scoped>
.tournament-topics {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
  padding: 2px 0;
}
</style>
