<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Clarifications</span>
      </v-card-title>

      <show-clarifications :clarifications="clarifications">
      </show-clarifications>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import ShowClarifications from '@/views/teacher/discussions/ShowClarifications.vue';
import Reply from '@/models/management/Reply';

@Component({
  components: {
    'show-clarifications': ShowClarifications
  }
})
export default class ShowClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;
  clarifications: Reply[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.clarifications] = await Promise.all([
        RemoteServices.getClarificationsByQuestionId(this.question.id!)
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
