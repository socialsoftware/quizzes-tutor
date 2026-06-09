<template>
  <v-form>
    <v-select
      v-model="tournamentTopics"
      :items="tournament.topics"
      multiple
      chips
      disabled
      append-icon="false"
    ></v-select>
  </v-form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Topic from '@/models/management/Topic';
import Tournament from '@/models/user/Tournament';

const props = defineProps<{
  tournament: Tournament;
}>();

const tournamentTopics = ref<Topic[]>([]);

onMounted(() => {
  tournamentTopics.value = JSON.parse(JSON.stringify(props.tournament.topics));
});
</script>
