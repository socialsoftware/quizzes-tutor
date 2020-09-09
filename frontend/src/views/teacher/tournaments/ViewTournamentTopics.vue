<template>
  <v-form>
    <v-select
      v-model="tournamentTopics"
      :items="tournament.topics"
      multiple
      disabled
      append-icon="false"
    >
      <template v-slot:selection="data">
        <v-chip v-bind="data.attrs">
          {{ data.item }}
        </v-chip>
      </template>
    </v-select>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import Tournament from '@/models/user/Tournament';

@Component
export default class ViewTournamentTopics extends Vue {
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  tournamentTopics: Topic[] = JSON.parse(
    JSON.stringify(this.tournament.topics)
  );
}
</script>
