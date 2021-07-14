<template>
  <div class="discussion" v-if="clarifications.length > 0">
    <ul>
      <li
        style="margin-bottom: 25px !important"
        v-for="clarification in clarifications"
        :key="clarification.id"
      >
        <div class="text-left">
          <b
            >{{ clarification.name }} ({{ clarification.username }}) replied on
            {{ clarification.date }} :</b
          >
          <span v-html="convertMarkDown(clarification.message)" />
          <v-switch
            v-if="canChange"
            style="width: 12%"
            v-model="clarification.public"
            :label="clarification.public ? 'Public' : 'Private'"
            @change="changeReplyAvailability(clarification.id)"
          />
        </div>
      </li>
    </ul>
  </div>
  <div v-else>
    <v-card-text class="text-center"> No clarifications yet </v-card-text>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Reply from '@/models/management/Reply';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ClarificationComponent extends Vue {
  @Prop() readonly clarifications!: Reply[];
  @Prop() readonly canChange!: boolean;

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }

  async changeReplyAvailability(id: number) {
    await this.$store.dispatch('loading');
    await RemoteServices.changeReplyAvailability(id);
    await this.$store.dispatch('clearLoading');
    this.$emit('make-private', id);
  }
}
</script>

<style lang="scss" scoped>
ul {
  list-style-type: none;
}
</style>
