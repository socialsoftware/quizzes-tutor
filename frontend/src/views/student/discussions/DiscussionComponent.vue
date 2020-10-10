<template>
  <div class="discussions-clarifications-container">
    <clarification-component :clarifications="clarifications">
    </clarification-component>
    <div class="discussion-container" v-if="answered">
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
        <reply-component v-else :discussion="userDiscussion" />
      </v-card>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Discussion from '@/models/management/Discussion';
import ReplyComponent from '@/views/student/discussions/ReplyComponent.vue';
import RemoteServices from '@/services/RemoteServices';
import ClarificationComponent from './ClarificationComponent.vue';
import Reply from '@/models/management/Reply';

@Component({
  components: {
    'reply-component': ReplyComponent,
    'clarification-component': ClarificationComponent
  }
})
export default class DiscussionComponent extends Vue {
  @Prop(Boolean) readonly hasDiscussion!: boolean;
  @Prop() readonly question!: Question;
  clarifications: Reply[] = [];
  @Prop() readonly userDiscussion?: Discussion;
  @Prop(Boolean) readonly answered!: boolean;
  discussionMessage: string = '';

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
    try {
      [this.clarifications] = await Promise.all([
        RemoteServices.getClarificationsByQuestionId(this.question.id!)
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
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
    border-top: #1e88e5 solid 2px;
  }

  .submit-button {
    background-color: #1976d2 !important;
    color: white;
  }
}

</style>
