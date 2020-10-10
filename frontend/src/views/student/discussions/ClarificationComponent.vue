<template>
  <div class="clarification-container">
    <v-card>
      <v-card-title
        class="justify-center headline"
        style="background-color: #1976d2; color: white"
      >
        Clarifications
      </v-card-title>
      <div class="discussion" v-if="clarifications.length > 0">
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
      <div v-else>
        <v-card-text class="text-center">
          No clarifications yet
        </v-card-text>
      </div>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit, Watch } from 'vue-property-decorator';
import User from '@/models/user/User';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Reply from '@/models/management/Reply';
@Component
export default class ClarificationComponent extends Vue {
  @Prop() readonly clarifications!: Reply[];
  user: User = this.$store.getters.getUser;

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }
}
</script>

<style lang="scss" scoped>
.clarification-container {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  max-width: 1024px;
  text-decoration: none solid;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  overflow: hidden;
  margin: -100px auto 120px;
  border-radius: 0;

  ul {
    list-style-type: none;
  }

  .discussion-message {
    width: 95%;
    margin: 20px auto auto;
  }

  .reply {
    margin: 5px;
    padding: 15px 15px 0 30px;
  }

  .discussion {
    width: 100%;
    left: 0;
    margin: 5px;
    padding: 15px;
  }
}
</style>
