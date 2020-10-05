<template>
  <div class="clarification-container" v-if="discussions.length > 0">
    <div class="discussion">
      <ul>
        <li
          style="margin-bottom: 25px !important;"
          v-for="discussion in discussions"
          :key="discussion.message"
        >
          <div class="text-left">
            <b v-if="discussion.userId !== user.id"
            >{{ discussion.userName }} opened a discussion on
              {{ discussion.date }} :</b
            >
            <b v-else>You opened a discussion on {{ discussion.date }} :</b>
            <span v-html="convertMarkDown(discussion.message)" />
          </div>
          <v-expansion-panels v-if="discussion.replies !== null" :inset="true">
            <v-expansion-panel>
              <v-expansion-panel-header style="background-color: #d5d5d5"
              >Show replies
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <div
                  v-for="reply in discussion.replies"
                  :key="reply.id"
                  class="text-left reply"
                >
                  <div v-if="reply.available === true">
                    <b v-if="user.id !== reply.userId"
                    >{{ reply.userName }} replied on {{ reply.date }} :
                    </b>
                    <b v-else>You replied on {{ reply.date }} :</b>
                    <span v-html="convertMarkDown(reply.message)" />
                  </div>
                </div>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>
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
import { Component, Vue, Prop, Emit, Watch } from 'vue-property-decorator';
import Discussion from '@/models/management/Discussion';
import User from '@/models/user/User';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
@Component
export default class ClarificationComponent extends Vue {
  @Prop() readonly discussions!: Discussion[];
  user: User = this.$store.getters.getUser;

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
