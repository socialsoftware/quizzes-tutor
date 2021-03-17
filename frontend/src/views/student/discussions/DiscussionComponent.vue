<template>
  <div class="discussions-clarifications-container">
    <div class="clarification-container">
      <v-card>
        <v-card-title
          class="justify-center headline"
          style="background-color: #1976d2; color: white"
        >
          Clarifications
        </v-card-title>
        <clarification-component :clarifications="clarifications">
        </clarification-component>
      </v-card>
    </div>
    <div class="discussion-container">
      <v-card>
        <v-card-title
          class="justify-center headline"
          style="background-color: #1976d2; color: white"
        >
          Discussions
        </v-card-title>
        <div v-if="userDiscussion == null" class="discussion-message">
          <v-textarea
            solo
            data-cy="discussionTextArea"
            v-model="discussionMessage"
            v-on:input="onInput"
            name="input-7-4"
            label="Enter discussion message here"
          ></v-textarea>
          <v-card-actions>
            <v-spacer />
            <v-btn
              data-cy="submitDiscussionButton"
              class="submit-button"
              @click="submitDiscussion"
              >Submit</v-btn
            >
          </v-card-actions>
        </div>
        <div v-else class="reply-container">
          <div class="discussion">
            <ul>
              <li
                style="margin-bottom: 25px !important"
                :key="userDiscussion.message"
              >
                <div style="display: inline-flex; width: 100%">
                  <div style="width: 88%" class="text-left">
                    <b v-if="user.role === 'TEACHER'"
                      >{{ user.name }} ({{ user.username }}) opened a discussion
                      on {{ discussion.date }} :
                    </b>
                    <b v-else
                      >You opened a discussion on {{ userDiscussion.date }} :</b
                    >
                    <span v-html="convertMarkDown(userDiscussion.message)" />
                  </div>
                  <v-switch
                    v-if="
                      userDiscussion.replies.length > 0 && userDiscussion.closed
                    "
                    style="width: 12%"
                    v-model="userDiscussion.closed"
                    :label="userDiscussion.closed ? 'Closed' : 'Reopen'"
                    @change="changeDiscussionStatus(userDiscussion.id)"
                  />
                </div>
                <reply-component :discussion="userDiscussion" />
              </li>
            </ul>
          </div>
        </div>
      </v-card>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Discussion from '@/models/management/Discussion';
import ReplyComponent from '@/views/student/discussions/ReplyComponent.vue';
import RemoteServices from '@/services/RemoteServices';
import ClarificationComponent from './ClarificationComponent.vue';
import Reply from '@/models/management/Reply';
import StatementQuestion from '@/models/statement/StatementQuestion';
import User from '@/models/user/User';

@Component({
  components: {
    'reply-component': ReplyComponent,
    'clarification-component': ClarificationComponent,
  },
})
export default class DiscussionComponent extends Vue {
  @Prop() readonly question!: StatementQuestion;
  @Prop() readonly userDiscussion?: Discussion;
  clarifications: Reply[] = [];
  discussionMessage: string = '';
  user: User = this.$store.getters.getUser;

  async created() {
    await this.$store.dispatch('loading');
    this.clarifications = await RemoteServices.getClarificationsByQuestionId(
      this.question.questionId!
    );
    await this.$store.dispatch('clearLoading');
  }

  @Emit()
  submitDiscussion() {
    this.discussionMessage = '';
    return 1;
  }

  @Emit('discussionMessage')
  onInput() {
    return this.discussionMessage;
  }

  @Watch('question')
  async onQuestionChange() {
    this.discussionMessage = '';
    await this.$store.dispatch('loading');
    this.clarifications = await RemoteServices.getClarificationsByQuestionId(
      this.question.questionId!
    );
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }

  async changeDiscussionStatus(id: number) {
    await this.$store.dispatch('loading');
    await RemoteServices.changeDiscussionStatus(id);
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.discussion-container {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  max-width: 1024px;
  text-decoration: none solid;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  overflow: hidden;
  margin: -100px auto 100px;
  border-radius: 0;

  .discussion-message {
    width: 95%;
    margin: 20px auto auto;
  }

  .discussion {
    width: 100%;
    left: 0;
    margin: 5px;
    padding: 15px;
  }

  .submit-button {
    background-color: #1976d2 !important;
    color: white;
  }
}

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

.reply-container {
  ul {
    list-style-type: none;
  }

  .reply-message {
    width: 95%;
    margin: 0 25px;
  }

  .discussion {
    width: 100%;
    margin: 5px;
    padding: 25px 25px 2px;
  }

  .reply {
    margin: 5px;
    padding: 15px 15px 0 30px;
  }

  .submit-button {
    background-color: #1976d2 !important;
    color: white;
    margin-left: 89%;
  }

  .textarea-reply {
    margin-bottom: -18px;
  }
}
</style>
