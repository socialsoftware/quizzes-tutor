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
            >{{ clarification.username }} replied on
            {{ clarification.date }} :</b
          >
          <span v-html="convertMarkDown(clarification.message)" />
        </div>
      </li>
    </ul>
  </div>
  <div v-else>
    <v-card-text class="text-center"> No clarifications yet </v-card-text>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Reply from '@/models/management/Reply';
@Component
export default class ClarificationComponent extends Vue {
  @Prop() readonly clarifications!: Reply[];

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }
}
</script>

<style lang="scss" scoped>
ul {
  list-style-type: none;
}
</style>
