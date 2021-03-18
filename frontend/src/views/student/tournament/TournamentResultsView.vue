<template>
  <v-container
    fluid
    style="
      height: 100%;
      position: relative;
      display: flex;
      flex-direction: column;
    "
  >
    <h2>Tournament</h2>
    <v-container fluid style="position: relative; max-height: 100%; flex: 1">
      <v-row style="width: 100%; height: 100%">
        <v-col>
          <v-card class="dashCard">
            <v-card-title class="justify-center">Tournament Info</v-card-title>
            <div class="text-left" style="padding-left: 25px">
              <b style="color: #1976d2">Course: </b>
              <span data-cy="Course">{{
                selectedTournament !== null
                  ? selectedTournament.courseAcronym
                  : 'Unknown tournament'
              }}</span
              ><br />
              <b style="color: #1976d2">Number: </b>
              <span data-cy="Id">{{
                selectedTournament !== null
                  ? selectedTournament.id
                  : 'Unknown tournament'
              }}</span
              ><br />
              <b style="color: #1976d2">Creator: </b>
              <span>{{
                selectedTournament !== null
                  ? selectedTournament.creator.name
                  : 'Unknown tournament'
              }}</span
              ><br />
              <b style="color: #1976d2">Topics: </b>
              <span data-cy="Topics"
                >{{ selectedTournament !== null ? '' : 'Unknown tournament' }}
                <ul
                  v-if="
                    selectedTournament !== null &&
                    selectedTournament.topics !== null &&
                    selectedTournament.topics.length
                  "
                >
                  <div
                    v-for="topic in selectedTournament.topics"
                    :key="topic.id"
                  >
                    <v-chip>
                      {{ topic }}
                    </v-chip>
                  </div>
                </ul>
              </span>
            </div>
            <v-container>
              <v-col>
                <v-card-title class="justify-center">Status</v-card-title>
                <div id="statsContainer" v-if="selectedTournament !== null">
                  <div class="square text-center">
                    <span data-cy="Id" class="num2">{{
                      selectedTournament !== null
                        ? selectedTournament.startTime
                        : 'Unknown tournament'
                    }}</span>
                    <p class="statName">Start Time</p>
                  </div>
                  <div class="square text-center">
                    <span data-cy="Id" class="num2">{{
                      selectedTournament !== null
                        ? selectedTournament.endTime
                        : 'Unknown tournament'
                    }}</span>
                    <p class="statName">End Time</p>
                  </div>
                  <div class="square">
                    <animated-number
                      class="num"
                      :number="selectedTournament.numberOfQuestions"
                    />
                    <p class="statName">Number of Questions</p>
                  </div>
                  <div class="square">
                    <animated-number
                      class="num"
                      :number="selectedTournament.participants.length"
                    ></animated-number>
                    <p class="statName">Number of Participants</p>
                  </div>
                  <div class="square text-center">
                    <span data-cy="Id" class="num2"
                      ><b>{{
                        selectedTournament !== null
                          ? selectedTournament.getStateName()
                          : 'Unknown tournament'
                      }}</b></span
                    >
                    <p class="statName">State</p>
                  </div>
                  <div class="square text-center">
                    <span data-cy="Id" class="num2"
                      ><b>{{
                        selectedTournament !== null
                          ? selectedTournament.getPrivateName()
                          : 'Unknown tournament'
                      }}</b></span
                    >
                    <p class="statName">Privacy</p>
                  </div>
                </div>
              </v-col>
            </v-container>
          </v-card>
        </v-col>
        <v-col :cols="8">
          <v-card class="dashCard flexCard">
            <v-card-title class="justify-center">Participants</v-card-title>
            <v-data-table
              :headers="headers"
              :items="participants"
              :sort-by="['name']"
              :hide-default-footer="true"
              :mobile-breakpoint="0"
              class="fill-height"
            >
            </v-data-table>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Tournament from '@/models/user/Tournament';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TournamentParticipant from '@/models/user/TournamentParticipant';

@Component({
  components: { AnimatedNumber },
})
export default class TournamentResultsView extends Vue {
  @Prop({ type: String, required: true }) id!: number;

  selectedTournament: Tournament | null = null;
  participants: TournamentParticipant[] = [];

  headers: object = [
    { text: 'Name', value: 'name', align: 'center' },
    { text: 'Username', value: 'username', align: 'center' },
    { text: 'Number of Answers', value: 'numberOfAnswered', align: 'center' },
    {
      text: 'Number of Correct Answers',
      value: 'numberOfCorrect',
      align: 'center',
    },
    { text: 'Score', value: 'score', align: 'center' },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.selectedTournament = await RemoteServices.getTournament(this.id);
      if (this.selectedTournament)
        this.participants = this.selectedTournament.participants;
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

  .dashInfo {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    padding: 20px;
    align-items: stretch;
    height: 100%;
  }

  #statsContainer {
    display: grid;
    grid-template-areas: 'a a';
    grid-auto-columns: 1fr;
    grid-auto-rows: 1fr;
    justify-items: stretch;
    justify-content: space-around;
    align-items: stretch;
    align-content: stretch;
    column-gap: 15px;
    row-gap: 15px;
  }

  .square {
    border: 3px solid #ffffff;
    border-radius: 5px;
    display: flex;
    color: #1976d2;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    @include background-opacity(#ffffff, 0.85);

    .num {
      display: block;
      font-size: 30pt;
      transition: all 0.5s;
    }

    .num2 {
      display: block;
      font-size: 20pt;
      transition: all 0.5s;
    }

    .statName {
      display: block;
      font-size: 15pt;
    }
  }

  .square:hover {
    border: 2px solid #1976d2;
    font-weight: bolder;
  }
}

.description {
  font-size: 15pt;
  font-weight: bold;
  color: inherit;
}
</style>
