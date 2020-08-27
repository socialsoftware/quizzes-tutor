<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card class="mx-auto" max-width="500" outlined>
          <v-list-item three-line>
            <v-col>
              <v-list-item-content>
                <div class="overline mb-4"></div>
                  <v-list-item-title class="headline mb-1"
                    >{{info !== null ? info.name.substring(0,25) : 'Unknown user'}}</v-list-item-title
                  >
                  <v-list-item-subtitle
                    >{{ info !== null ? info.username.substring(0,20) : 'Unknown user' }}</v-list-item-subtitle
                  >
              </v-list-item-content>
            </v-col>
            <v-col>
              <v-list-item-avatar tile size="80" color="grey"
                ><img src="https://cdn.vuetifyjs.com/images/john.jpg" alt="John"
              /></v-list-item-avatar>
            </v-col>
          </v-list-item>

          <v-card-actions>
            <v-btn text>Button</v-btn>
            <v-btn text>Button</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-col>
            <v-list-item-content>
              <v-list-item-title class="headline mb-1"
                >Discussions</v-list-item-title
              >
              <v-list-item-subtitle
                >Discussions created:
                {{ info.numDiscussions }}</v-list-item-subtitle
              >
            </v-list-item-content>
          </v-col>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import DashboardInfo from '@/models/management/DashboardInfo';
import RemoteServices from '@/services/RemoteServices';
@Component
export default class DashboardView extends Vue {
  info: DashboardInfo | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.info = await RemoteServices.getDashboardInfo();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
