<template>
  <v-container
    fluid
    style="height: 100%; position: relative; display: flex; flex-direction: column"
    v-if="this.$store.getters.isLoggedIn"
  >
    <v-container fluid style="position: relative; max-height: 100%;">
      <v-row style="width: 100%; height: 100%">
        <v-col>
          <v-card class="dashCard flexCard">
            <v-card-title class="justify-center"
              >Tournament {{ this.id }} - Ranking</v-card-title
            >
            <v-data-table
              :headers="headers"
              :items="tournamentScores"
              :hide-default-footer="true"
              :mobile-breakpoint="0"
              class="fill-height"
              disable-sort
            >
              <template v-slot:item.ranking="{ item }">
                <v-chip
                  v-if="item.ranking < 4 && item.ranking != 0"
                  :color="TournamentScore.getRankingColor(item.ranking)"
                  text-color="white"
                >
                  {{ item.ranking }}
                </v-chip>
                <span v-else-if="item.ranking != 0">
                  {{ item.ranking }}
                </span>
              </template>
              <template v-slot:item.score="{ item }">
                <v-chip
                  v-if="isClosed"
                  :color="TournamentScore.getPercentageColor(item.score)"
                >
                  {{ item.score === '' ? 'NOT SOLVED' : item.score }}
                </v-chip>
                <v-chip v-else>
                  {{ 'NOT AVAILABLE YET' }}
                </v-chip>
              </template>
            </v-data-table>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Tournament from '@/models/user/Tournament';
import TournamentScore from '@/models/user/TournamentScore';
import RemoteServices from '@/services/RemoteServices';

@Component({})
export default class ParticipantsTournament extends Vue {
  @Prop({ type: String, required: true }) id!: number;

  selectedTournament: Tournament | undefined;
  tournamentScores: TournamentScore[] = [];
  correctAnswers: number = 0;

  headers: object = [
    { text: 'Ranking', value: 'ranking', align: 'center' },
    { text: 'Name', value: 'name', align: 'center' },
    { text: 'Username', value: 'username', align: 'center' },
    { text: 'Score', value: 'score', align: 'center' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.selectedTournament = await RemoteServices.getTournament(this.id);
      if (this.selectedTournament) {
        this.selectedTournament.participants.map(async participant => {
          let score;
          if (this.selectedTournament?.isClosed)
            score = await TournamentScore.getScore(
              this.selectedTournament,
              this.correctAnswers
            );
          else score = '';
          this.tournamentScores.push(
            new TournamentScore(participant, score, this.correctAnswers)
          );
        });
      }
      if (this.selectedTournament.isClosed)
        this.tournamentScores.sort((a, b) => TournamentScore.sortByScore(a, b));
      this.tournamentScores = TournamentScore.setRankings(
        this.tournamentScores
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
@mixin background-opacity($color, $opacity: 1) {
  $red: red($color);
  $green: green($color);
  $blue: blue($color);
  background: rgba($red, $green, $blue, $opacity) !important;
}

.flexCard {
  display: flex;
  flex-direction: column;
}

.dashCard {
  height: 100%;
  @include background-opacity(#fff, 0.6);
}
</style>
