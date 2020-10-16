<template>
  <div class="clarification-container" v-if="clarifications.length > 0">
    <div class="discussion">
      <ul>
        <li
          style="margin-bottom: 25px !important;"
          v-for="clarification in clarifications"
          :key="clarification.id"
        >
          <div class="text-left">
            <b
              >{{ clarification.userName }} replied on
              {{ clarification.date }} :</b
            >
            <span v-html="convertMarkDown(clarification.message)" />
          </div>
        </li>
      </ul>
    </div>
  </div>
  <div v-else>
    <v-card-text class="text-center">
      No clarifications yet
    </v-card-text>
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
.clarification-container {
  color: rgb(51, 51, 51);
  user-select: none;
  caret-color: rgb(51, 51, 51);

  .discussion {
    margin: 5px;
    padding: 25px;
  }

  ul {
    list-style-type: none;
  }

  .reply {
    margin: 5px;
    padding: 15px 15px 0 30px;
  }

  .textarea-reply {
    margin-bottom: -18px;
  }
}
</style>
